package com.quickcart.wishlist.controller;

import com.quickcart.wishlist.dto.WishlistResponseDTO;
import com.quickcart.wishlist.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

	private final WishlistService wishlistService;

	public WishlistController(WishlistService wishlistService) {
		this.wishlistService = wishlistService;
	}

	@GetMapping
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<List<WishlistResponseDTO>> getCurrentUserWishlist() {
		return ResponseEntity.ok(wishlistService.getCurrentUserWishlist());
	}

	@PostMapping("/{productId}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<WishlistResponseDTO> addProduct(@PathVariable Long productId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.addProduct(productId));
	}

	@DeleteMapping("/{productId}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<Void> removeProduct(@PathVariable Long productId) {
		wishlistService.removeProduct(productId);
		return ResponseEntity.noContent().build();
	}
}
