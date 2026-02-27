package com.quickcart.review.dto;

import java.time.Instant;

public class ReviewResponse {
	private Long id;
	private Long productId;
	private Long userId;
	private String userEmail;
	private Integer rating;
	private String comment;
	private Instant createdAt;

	public ReviewResponse() {
	}

	public ReviewResponse(Long id, Long productId, Long userId, String userEmail, Integer rating, String comment, Instant createdAt) {
		this.id = id;
		this.productId = productId;
		this.userId = userId;
		this.userEmail = userEmail;
		this.rating = rating;
		this.comment = comment;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
