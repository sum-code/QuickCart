package com.quickcart.review.service;

import com.quickcart.review.dto.ReviewCreateRequest;
import com.quickcart.review.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
	List<ReviewResponse> getReviewsByProduct(Long productId);
	ReviewResponse createReview(Long productId, String userEmail, ReviewCreateRequest request);
}
