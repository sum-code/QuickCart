package com.quickcart.review.service.impl;

import com.quickcart.product.entity.Product;
import com.quickcart.product.exception.ProductNotFoundException;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.review.dto.ReviewCreateRequest;
import com.quickcart.review.dto.ReviewResponse;
import com.quickcart.review.entity.Review;
import com.quickcart.review.repository.ReviewRepository;
import com.quickcart.review.service.ReviewService;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final ProductRepository productRepository;
	private final AppUserRepository appUserRepository;

	public ReviewServiceImpl(
			ReviewRepository reviewRepository,
			ProductRepository productRepository,
			AppUserRepository appUserRepository
	) {
		this.reviewRepository = reviewRepository;
		this.productRepository = productRepository;
		this.appUserRepository = appUserRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReviewResponse> getReviewsByProduct(Long productId) {
		if (!productRepository.existsById(productId)) {
			throw new ProductNotFoundException(productId);
		}

		return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public ReviewResponse createReview(Long productId, String userEmail, ReviewCreateRequest request) {
		AppUser user = appUserRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));

		boolean alreadyReviewed = reviewRepository.existsByProductIdAndUserId(productId, user.getId());
		if (alreadyReviewed) {
			throw new IllegalStateException("You have already reviewed this product.");
		}

		Review review = new Review();
		review.setProduct(product);
		review.setUser(user);
		review.setRating(request.getRating());
		review.setComment(request.getComment());

		Review saved = reviewRepository.save(review);
		return toResponse(saved);
	}

	private ReviewResponse toResponse(Review review) {
		return new ReviewResponse(
				review.getId(),
				review.getProduct().getId(),
				review.getUser().getId(),
				review.getUser().getEmail(),
				review.getRating(),
				review.getComment(),
				review.getCreatedAt()
		);
	}
}
