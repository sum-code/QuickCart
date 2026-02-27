package com.quickcart.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductResponse {
	private Long id;
	private String name;
	private String brand;
	private String description;
	private BigDecimal price;
	private Integer stockQuantity;
	private String imageUrl;
	private String category;
	private Double averageRating;
	private Long reviewCount;
	private Instant createdAt;
	private Instant updatedAt;

	public ProductResponse() {
	}

	public ProductResponse(Long id, String name, String brand, String description, BigDecimal price, Integer stockQuantity,
						 String imageUrl, String category, Double averageRating, Long reviewCount, Instant createdAt, Instant updatedAt) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.description = description;
		this.price = price;
		this.stockQuantity = stockQuantity;
		this.imageUrl = imageUrl;
		this.category = category;
		this.averageRating = averageRating;
		this.reviewCount = reviewCount;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Long getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(Long reviewCount) {
		this.reviewCount = reviewCount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
