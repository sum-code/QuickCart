package com.quickcart.wishlist.service.impl;

import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.entity.Product;
import com.quickcart.product.exception.ProductNotFoundException;
import com.quickcart.product.mapper.ProductMapper;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.review.repository.ReviewRepository;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.service.UserService;
import com.quickcart.wishlist.dto.WishlistResponseDTO;
import com.quickcart.wishlist.entity.Wishlist;
import com.quickcart.wishlist.repository.WishlistRepository;
import com.quickcart.wishlist.service.WishlistService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

	private final WishlistRepository wishlistRepository;
	private final ProductRepository productRepository;
	private final UserService userService;
	private final ProductMapper productMapper;
	private final ReviewRepository reviewRepository;

	public WishlistServiceImpl(
			WishlistRepository wishlistRepository,
			ProductRepository productRepository,
			UserService userService,
			ProductMapper productMapper,
			ReviewRepository reviewRepository
	) {
		this.wishlistRepository = wishlistRepository;
		this.productRepository = productRepository;
		this.userService = userService;
		this.productMapper = productMapper;
		this.reviewRepository = reviewRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public java.util.List<WishlistResponseDTO> getCurrentUserWishlist() {
		AppUser user = userService.getCurrentUser();
		java.util.List<Wishlist> wishlistItems = wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

		Map<Long, ReviewRepository.ProductRatingSummary> summaryByProductId = wishlistItems.isEmpty()
				? Collections.emptyMap()
				: reviewRepository.findRatingSummariesByProductIds(
						wishlistItems.stream().map(item -> item.getProduct().getId()).toList()
				)
				.stream()
				.collect(Collectors.toMap(ReviewRepository.ProductRatingSummary::getProductId, Function.identity()));

		return wishlistItems.stream()
				.map(item -> toResponse(item, summaryByProductId.get(item.getProduct().getId())))
				.toList();
	}

	@Override
	@Transactional(noRollbackFor = DataIntegrityViolationException.class)
	public WishlistResponseDTO addProduct(Long productId) {
		AppUser user = userService.getCurrentUser();

		Wishlist existing = wishlistRepository.findByUserIdAndProductId(user.getId(), productId).orElse(null);
		if (existing != null) {
			ReviewRepository.ProductRatingSummary existingSummary = reviewRepository.findRatingSummaryByProductId(productId).orElse(null);
			return toResponse(existing, existingSummary);
		}

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));

		Wishlist wishlist = new Wishlist();
		wishlist.setUser(user);
		wishlist.setProduct(product);

		try {
			Wishlist saved = wishlistRepository.save(wishlist);
			ReviewRepository.ProductRatingSummary summary = reviewRepository.findRatingSummaryByProductId(productId).orElse(null);
			return toResponse(saved, summary);
		} catch (DataIntegrityViolationException ex) {
			Wishlist recovered = wishlistRepository.findByUserIdAndProductId(user.getId(), productId)
					.orElseThrow(() -> ex);
			ReviewRepository.ProductRatingSummary recoveredSummary = reviewRepository.findRatingSummaryByProductId(productId).orElse(null);
			return toResponse(recovered, recoveredSummary);
		}
	}

	@Override
	@Transactional
	public void removeProduct(Long productId) {
		AppUser user = userService.getCurrentUser();
		wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
	}

	private WishlistResponseDTO toResponse(Wishlist wishlist, ReviewRepository.ProductRatingSummary summary) {
		double averageRating = summary == null || summary.getAverageRating() == null ? 0.0 : summary.getAverageRating();
		long reviewCount = summary == null || summary.getReviewCount() == null ? 0L : summary.getReviewCount();

		ProductResponse productResponse = productMapper.toResponse(wishlist.getProduct(), averageRating, reviewCount);
		return new WishlistResponseDTO(wishlist.getId(), productResponse, wishlist.getCreatedAt());
	}
}
