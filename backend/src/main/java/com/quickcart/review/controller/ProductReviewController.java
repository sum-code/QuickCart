package com.quickcart.review.controller;

import com.quickcart.review.dto.ReviewCreateRequest;
import com.quickcart.review.dto.ReviewResponse;
import com.quickcart.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ProductReviewController {

	private final ReviewService reviewService;

	public ProductReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public ResponseEntity<List<ReviewResponse>> getByProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ReviewResponse> create(
			@PathVariable Long productId,
			@Valid @RequestBody ReviewCreateRequest request,
			Authentication authentication
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(reviewService.createReview(productId, authentication.getName(), request));
	}
}
