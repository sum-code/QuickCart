package com.quickcart.wishlist.service;

import com.quickcart.wishlist.dto.WishlistResponseDTO;

import java.util.List;

public interface WishlistService {
	List<WishlistResponseDTO> getCurrentUserWishlist();
	WishlistResponseDTO addProduct(Long productId);
	void removeProduct(Long productId);
}
