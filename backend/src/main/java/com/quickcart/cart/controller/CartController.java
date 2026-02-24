package com.quickcart.cart.controller;

import com.quickcart.cart.dto.AddToCartRequest;
import com.quickcart.cart.dto.CartResponse;
import com.quickcart.cart.dto.UpdateCartItemRequest;
import com.quickcart.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
	public ResponseEntity<CartResponse> getCart() {
		return ResponseEntity.ok(cartService.getCart());
	}

	@PostMapping("/items")
	public ResponseEntity<CartResponse> add(@Valid @RequestBody AddToCartRequest request) {
		return ResponseEntity.ok(cartService.add(request));
	}

	@PutMapping("/items/{productId}")
	public ResponseEntity<CartResponse> update(@PathVariable Long productId, @Valid @RequestBody UpdateCartItemRequest request) {
		return ResponseEntity.ok(cartService.updateQuantity(productId, request.getQuantity()));
	}

	@DeleteMapping("/items/{productId}")
	public ResponseEntity<CartResponse> remove(@PathVariable Long productId) {
		return ResponseEntity.ok(cartService.remove(productId));
	}

	@DeleteMapping
	public ResponseEntity<CartResponse> clear() {
		return ResponseEntity.ok(cartService.clear());
	}
}
