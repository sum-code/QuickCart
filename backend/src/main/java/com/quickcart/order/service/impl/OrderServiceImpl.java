package com.quickcart.order.service.impl;

import com.quickcart.cart.entity.CartItem;
import com.quickcart.cart.repository.CartItemRepository;
import com.quickcart.order.dto.OrderItemResponse;
import com.quickcart.order.dto.OrderResponse;
import com.quickcart.order.dto.OrderStatusHistoryDTO;
import com.quickcart.order.dto.OrderTrackingResponseDTO;
import com.quickcart.order.dto.UpdateOrderStatusRequest;
import com.quickcart.order.entity.Order;
import com.quickcart.order.entity.OrderItem;
import com.quickcart.order.entity.OrderStatus;
import com.quickcart.order.entity.OrderStatusHistory;
import com.quickcart.order.event.OrderStatusChangedEvent;
import com.quickcart.order.repository.OrderItemRepository;
import com.quickcart.order.repository.OrderRepository;
import com.quickcart.order.repository.OrderStatusHistoryRepository;
import com.quickcart.order.service.OrderService;
import com.quickcart.product.entity.Product;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.service.UserService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {
	private static final Duration RETURN_WINDOW = Duration.ofDays(30);
	private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = buildAllowedTransitions();

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderStatusHistoryRepository orderStatusHistoryRepository;
	private final ProductRepository productRepository;
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;

	public OrderServiceImpl(
			OrderRepository orderRepository,
			OrderItemRepository orderItemRepository,
			CartItemRepository cartItemRepository,
			OrderStatusHistoryRepository orderStatusHistoryRepository,
			ProductRepository productRepository,
			UserService userService,
			ApplicationEventPublisher eventPublisher
	) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.cartItemRepository = cartItemRepository;
		this.orderStatusHistoryRepository = orderStatusHistoryRepository;
		this.productRepository = productRepository;
		this.userService = userService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public OrderResponse placeOrderFromCart() {
		AppUser user = userService.getCurrentUser();
		List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
		if (cartItems.isEmpty()) {
			throw new IllegalArgumentException("Cart is empty");
		}

		BigDecimal total = BigDecimal.ZERO;
		Order order = new Order();
		order.setUser(user);
		order.setStatus(OrderStatus.PLACED);
		order.setTotalAmount(BigDecimal.ZERO);
		Order savedOrder = orderRepository.save(order);

		for (CartItem ci : cartItems) {
			Product product = productRepository.findById(ci.getProduct().getId())
					.orElseThrow(() -> new IllegalArgumentException("Product not found"));
			int requested = ci.getQuantity();
			if (product.getStock() == null || product.getStock() < requested) {
				throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
			}
			product.setStock(product.getStock() - requested);
			productRepository.save(product);

			BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(requested));
			total = total.add(lineTotal);

			OrderItem item = new OrderItem();
			item.setOrder(savedOrder);
			item.setProduct(product);
			item.setProductName(product.getName());
			item.setPrice(product.getPrice());
			item.setQuantity(requested);
			orderItemRepository.save(item);
		}

		savedOrder.setTotalAmount(total);
		Order finalOrder = orderRepository.save(savedOrder);
		appendHistory(finalOrder, OrderStatus.PLACED, "Order placed successfully");
		cartItemRepository.deleteByUserId(user.getId());

		return toResponse(finalOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> myOrders() {
		AppUser user = userService.getCurrentUser();
		return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponse myOrder(Long orderId) {
		return toResponse(getOwnedOrder(orderId));
	}

	@Override
	@Transactional
	public OrderResponse updateStatus(Long orderId, OrderStatus status) {
		UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
		request.setStatus(status);
		request.setAdminNote("Order status updated by admin");
		return toResponseFromTracking(adminUpdateStatus(orderId, request));
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> allOrders() {
		return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderTrackingResponseDTO getOrderTracking(Long orderId) {
		return toTrackingResponse(getOwnedOrder(orderId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderTrackingResponseDTO> myTrackingOrders() {
		AppUser user = userService.getCurrentUser();
		return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
				.map(this::toTrackingResponse)
				.toList();
	}

	@Override
	@Transactional
	public OrderTrackingResponseDTO adminUpdateStatus(Long orderId, UpdateOrderStatusRequest request) {
		Order order = getOrderById(orderId);
		OrderStatus current = order.getStatus();
		OrderStatus next = request.getStatus();

		if (next == null) {
			throw new IllegalArgumentException("Status is required");
		}

		boolean statusChanged = current != next;
		if (statusChanged) {
			assertTransitionAllowed(current, next);
		}

		if (next == OrderStatus.SHIPPED) {
			if (isBlank(request.getCourierName()) || isBlank(request.getTrackingNumber())) {
				throw new IllegalArgumentException("Courier name and tracking number are required when status is SHIPPED");
			}
			order.setCourierName(trimToNull(request.getCourierName()));
			order.setTrackingNumber(trimToNull(request.getTrackingNumber()));
		}

		order.setStatus(next);
		Order saved = orderRepository.save(order);

		if (statusChanged) {
			appendHistory(saved, next, request.getAdminNote());
			eventPublisher.publishEvent(new OrderStatusChangedEvent(
					saved.getId(),
					saved.getUser().getEmail(),
					next,
					request.getAdminNote(),
					saved.getCourierName(),
					saved.getTrackingNumber()
			));
		}

		return toTrackingResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderTrackingResponseDTO> allTrackingOrders() {
		return orderRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::toTrackingResponse)
				.toList();
	}

	@Override
	@Transactional
	public OrderTrackingResponseDTO requestReturn(Long orderId) {
		Order order = getOwnedOrder(orderId);
		if (order.getStatus() != OrderStatus.DELIVERED) {
			throw new IllegalStateException("Return request is only allowed for DELIVERED orders");
		}

		Instant deliveredAt = orderStatusHistoryRepository
				.findFirstByOrderIdAndStatusOrderByCreatedAtDesc(orderId, OrderStatus.DELIVERED)
				.map(OrderStatusHistory::getCreatedAt)
				.orElse(order.getCreatedAt());

		if (deliveredAt.plus(RETURN_WINDOW).isBefore(Instant.now())) {
			throw new IllegalStateException("Return window has expired (30 days)");
		}

		order.setStatus(OrderStatus.RETURN_REQUESTED);
		Order saved = orderRepository.save(order);
		appendHistory(saved, OrderStatus.RETURN_REQUESTED, "Customer requested return");
		eventPublisher.publishEvent(new OrderStatusChangedEvent(
				saved.getId(),
				saved.getUser().getEmail(),
				OrderStatus.RETURN_REQUESTED,
				"Customer requested return",
				saved.getCourierName(),
				saved.getTrackingNumber()
		));

		return toTrackingResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] generateInvoice(Long orderId) {
		Order order = getOwnedOrder(orderId);

		try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {
				content.beginText();
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
				content.newLineAtOffset(60, 770);
				content.showText("QuickCart Invoice");
				content.endText();

				content.beginText();
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
				content.newLineAtOffset(60, 740);
				content.showText("Order ID: " + order.getId());
				content.newLineAtOffset(0, -18);
				content.showText("Customer: " + order.getUser().getEmail());
				content.newLineAtOffset(0, -18);
				content.showText("Status: " + order.getStatus());
				content.newLineAtOffset(0, -18);
				content.showText("Total Amount: $" + order.getTotalAmount());
				content.newLineAtOffset(0, -18);
				content.showText("Placed At: " + order.getCreatedAt());
				content.newLineAtOffset(0, -18);
				content.showText("Generated At: " + Instant.now());
				content.endText();
			}

			document.save(output);
			return output.toByteArray();
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to generate invoice PDF", ex);
		}
	}

	private OrderResponse toResponse(Order order) {
		List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
				.map(oi -> {
					BigDecimal lineTotal = oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()));
					return new OrderItemResponse(oi.getProduct().getId(), oi.getProductName(), oi.getPrice(), oi.getQuantity(), lineTotal);
				}).toList();

		return new OrderResponse(order.getId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), items);
	}

	private OrderResponse toResponseFromTracking(OrderTrackingResponseDTO tracking) {
		return new OrderResponse(
				tracking.getId(),
				tracking.getStatus(),
				tracking.getTotalAmount(),
				tracking.getCreatedAt(),
				tracking.getItems()
		);
	}

	private OrderTrackingResponseDTO toTrackingResponse(Order order) {
		List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
				.map(oi -> {
					BigDecimal lineTotal = oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()));
					return new OrderItemResponse(oi.getProduct().getId(), oi.getProductName(), oi.getPrice(), oi.getQuantity(), lineTotal);
				})
				.toList();

		List<OrderStatusHistoryDTO> history = orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(order.getId()).stream()
				.map(item -> new OrderStatusHistoryDTO(item.getStatus(), item.getNotes(), item.getCreatedAt()))
				.toList();

		return new OrderTrackingResponseDTO(
				order.getId(),
				order.getUser().getEmail(),
				order.getStatus(),
				order.getTotalAmount(),
				order.getCreatedAt(),
				order.getCourierName(),
				order.getTrackingNumber(),
				items,
				history
		);
	}

	private void appendHistory(Order order, OrderStatus status, String notes) {
		OrderStatusHistory history = new OrderStatusHistory();
		history.setOrder(order);
		history.setStatus(status);
		history.setNotes(trimToNull(notes));
		orderStatusHistoryRepository.save(history);
	}

	private void assertTransitionAllowed(OrderStatus current, OrderStatus next) {
		Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
		if (!allowed.contains(next)) {
			throw new IllegalStateException("Invalid order status transition: " + current + " -> " + next);
		}
	}

	private Order getOwnedOrder(Long orderId) {
		AppUser user = userService.getCurrentUser();
		return orderRepository.findByIdAndUserId(orderId, user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
	}

	private Order getOrderById(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
	}

	private static String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private static Map<OrderStatus, Set<OrderStatus>> buildAllowedTransitions() {
		Map<OrderStatus, Set<OrderStatus>> transitions = new EnumMap<>(OrderStatus.class);
		transitions.put(OrderStatus.PLACED, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED));
		transitions.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
		transitions.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
		transitions.put(OrderStatus.DELIVERED, EnumSet.of(OrderStatus.RETURN_REQUESTED));
		transitions.put(OrderStatus.RETURN_REQUESTED, EnumSet.of(OrderStatus.RETURN_APPROVED));
		transitions.put(OrderStatus.RETURN_APPROVED, EnumSet.of(OrderStatus.REFUNDED));
		transitions.put(OrderStatus.REFUNDED, EnumSet.noneOf(OrderStatus.class));
		transitions.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
		return transitions;
	}
}
