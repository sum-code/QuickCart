package com.quickcart.order.service.impl;

import com.quickcart.cart.entity.CartItem;
import com.quickcart.cart.repository.CartItemRepository;
import com.quickcart.order.dto.OrderItemResponse;
import com.quickcart.order.dto.OrderResponse;
import com.quickcart.order.entity.Order;
import com.quickcart.order.entity.OrderItem;
import com.quickcart.order.entity.OrderStatus;
import com.quickcart.order.repository.OrderItemRepository;
import com.quickcart.order.repository.OrderRepository;
import com.quickcart.order.service.OrderService;
import com.quickcart.product.entity.Product;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserService userService;

	public OrderServiceImpl(
			OrderRepository orderRepository,
			OrderItemRepository orderItemRepository,
			CartItemRepository cartItemRepository,
			ProductRepository productRepository,
			UserService userService
	) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
		this.userService = userService;
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
		order.setStatus(OrderStatus.PENDING);
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
		AppUser user = userService.getCurrentUser();
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		if (!order.getUser().getId().equals(user.getId())) {
			throw new IllegalArgumentException("Order not found");
		}
		return toResponse(order);
	}

	@Override
	@Transactional
	public OrderResponse updateStatus(Long orderId, OrderStatus status) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		order.setStatus(status);
		return toResponse(orderRepository.save(order));
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> allOrders() {
		return orderRepository.findAll().stream().map(this::toResponse).toList();
	}

	private OrderResponse toResponse(Order order) {
		List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
				.map(oi -> {
					BigDecimal lineTotal = oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()));
					return new OrderItemResponse(oi.getProduct().getId(), oi.getProductName(), oi.getPrice(), oi.getQuantity(), lineTotal);
				}).toList();

		return new OrderResponse(order.getId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), items);
	}
}
