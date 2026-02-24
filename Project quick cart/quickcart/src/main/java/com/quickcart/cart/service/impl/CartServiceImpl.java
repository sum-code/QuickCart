package com.quickcart.cart.service.impl;

import com.quickcart.cart.dto.AddToCartRequest;
import com.quickcart.cart.dto.CartItemResponse;
import com.quickcart.cart.dto.CartResponse;
import com.quickcart.cart.entity.CartItem;
import com.quickcart.cart.repository.CartItemRepository;
import com.quickcart.cart.service.CartService;
import com.quickcart.product.entity.Product;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserService userService;

	public CartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository, UserService userService) {
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
		this.userService = userService;
	}

	@Override
	@Transactional(readOnly = true)
	public CartResponse getCart() {
		AppUser user = userService.getCurrentUser();
		List<CartItem> items = cartItemRepository.findByUserId(user.getId());
		return toResponse(items);
	}

	@Override
	@Transactional
	public CartResponse add(AddToCartRequest request) {
		AppUser user = userService.getCurrentUser();
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));
		if (product.getStock() == null || product.getStock() < request.getQuantity()) {
			throw new IllegalArgumentException("Insufficient stock");
		}

		CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
				.orElseGet(() -> {
					CartItem ci = new CartItem();
					ci.setUser(user);
					ci.setProduct(product);
					ci.setQuantity(0);
					return ci;
				});

		item.setQuantity(item.getQuantity() + request.getQuantity());
		cartItemRepository.save(item);

		return getCart();
	}

	@Override
	@Transactional
	public CartResponse updateQuantity(Long productId, int quantity) {
		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity must be >= 1");
		}
		AppUser user = userService.getCurrentUser();
		CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
				.orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

		Product product = item.getProduct();
		if (product.getStock() == null || product.getStock() < quantity) {
			throw new IllegalArgumentException("Insufficient stock");
		}

		item.setQuantity(quantity);
		cartItemRepository.save(item);
		return getCart();
	}

	@Override
	@Transactional
	public CartResponse remove(Long productId) {
		AppUser user = userService.getCurrentUser();
		cartItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
		return getCart();
	}

	@Override
	@Transactional
	public CartResponse clear() {
		AppUser user = userService.getCurrentUser();
		cartItemRepository.deleteByUserId(user.getId());
		return new CartResponse(List.of(), 0, BigDecimal.ZERO);
	}

	private CartResponse toResponse(List<CartItem> items) {
		List<CartItemResponse> responses = items.stream().map(ci -> {
			BigDecimal price = ci.getProduct().getPrice();
			BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(ci.getQuantity()));
			return new CartItemResponse(
					ci.getProduct().getId(),
					ci.getProduct().getName(),
					price,
					ci.getQuantity(),
					lineTotal
			);
		}).toList();

		int totalQty = responses.stream().mapToInt(CartItemResponse::getQuantity).sum();
		BigDecimal totalAmount = responses.stream().map(CartItemResponse::getLineTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new CartResponse(responses, totalQty, totalAmount);
	}
}
