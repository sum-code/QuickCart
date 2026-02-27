package com.quickcart.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewCreateRequest {

	@NotNull(message = "rating is required")
	@Min(value = 1, message = "rating must be at least 1")
	@Max(value = 5, message = "rating must be at most 5")
	private Integer rating;

	@Size(max = 1200, message = "comment must not exceed 1200 characters")
	private String comment;

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
}
