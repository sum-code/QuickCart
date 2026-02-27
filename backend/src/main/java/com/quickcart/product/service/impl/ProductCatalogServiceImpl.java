package com.quickcart.product.service.impl;

import com.quickcart.product.dto.PagedResponse;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.entity.Product;
import com.quickcart.product.exception.ProductNotFoundException;
import com.quickcart.product.mapper.ProductMapper;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.product.specification.ProductSpecifications;
import com.quickcart.review.repository.ReviewRepository;
import com.quickcart.product.service.ProductCatalogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductCatalogServiceImpl implements ProductCatalogService {

	private final ProductRepository productRepository;
	private final ReviewRepository reviewRepository;
	private final ProductMapper productMapper;

	public ProductCatalogServiceImpl(ProductRepository productRepository, ReviewRepository reviewRepository, ProductMapper productMapper) {
		this.productRepository = productRepository;
		this.reviewRepository = reviewRepository;
		this.productMapper = productMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<ProductResponse> getProducts(String search, String category, BigDecimal minPrice, BigDecimal maxPrice, Integer minRating, Pageable pageable) {
		Page<Product> page = productRepository.findAll(
				ProductSpecifications.filterBy(search, category, minPrice, maxPrice, minRating),
				pageable
		);

		Map<Long, ReviewRepository.ProductRatingSummary> summaryByProductId = page.getContent().isEmpty()
				? Collections.emptyMap()
				: reviewRepository.findRatingSummariesByProductIds(page.getContent().stream().map(Product::getId).toList())
						.stream()
						.collect(Collectors.toMap(ReviewRepository.ProductRatingSummary::getProductId, Function.identity()));

		return new PagedResponse<>(
				page.getContent().stream()
						.map(product -> {
							ReviewRepository.ProductRatingSummary summary = summaryByProductId.get(product.getId());
							double averageRating = summary == null || summary.getAverageRating() == null ? 0.0 : summary.getAverageRating();
							long reviewCount = summary == null || summary.getReviewCount() == null ? 0L : summary.getReviewCount();
							return productMapper.toResponse(product, averageRating, reviewCount);
						})
						.toList(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isLast()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
		ReviewRepository.ProductRatingSummary summary = reviewRepository.findRatingSummaryByProductId(id).orElse(null);
		double averageRating = summary == null || summary.getAverageRating() == null ? 0.0 : summary.getAverageRating();
		long reviewCount = summary == null || summary.getReviewCount() == null ? 0L : summary.getReviewCount();
		return productMapper.toResponse(product, averageRating, reviewCount);
	}
}
