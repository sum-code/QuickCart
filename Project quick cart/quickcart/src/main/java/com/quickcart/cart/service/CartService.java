package com.quickcart.cart.service;

import com.quickcart.cart.dto.AddToCartRequest;
import com.quickcart.cart.dto.CartResponse;

public interface CartService {
	CartResponse getCart();
	CartResponse add(AddToCartRequest request);
	CartResponse updateQuantity(Long productId, int quantity);
	CartResponse remove(Long productId);
	CartResponse clear();
}
