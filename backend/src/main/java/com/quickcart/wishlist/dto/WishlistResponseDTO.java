package com.quickcart.wishlist.dto;

import com.quickcart.product.dto.ProductResponse;

import java.time.Instant;

public class WishlistResponseDTO {
	private Long id;
	private ProductResponse product;
	private Instant createdAt;

	public WishlistResponseDTO() {
	}

	public WishlistResponseDTO(Long id, ProductResponse product, Instant createdAt) {
		this.id = id;
		this.product = product;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductResponse getProduct() {
		return product;
	}

	public void setProduct(ProductResponse product) {
		this.product = product;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
