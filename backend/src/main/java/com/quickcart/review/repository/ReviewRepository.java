package com.quickcart.review.repository;

import com.quickcart.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	boolean existsByProductIdAndUserId(Long productId, Long userId);

	List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

	@Query("""
			select r.product.id as productId,
			       avg(r.rating) as averageRating,
			       count(r.id) as reviewCount
			from Review r
			where r.product.id in :productIds
			group by r.product.id
			""")
	List<ProductRatingSummary> findRatingSummariesByProductIds(@Param("productIds") Collection<Long> productIds);

	@Query("""
			select r.product.id as productId,
			       avg(r.rating) as averageRating,
			       count(r.id) as reviewCount
			from Review r
			where r.product.id = :productId
			group by r.product.id
			""")
	Optional<ProductRatingSummary> findRatingSummaryByProductId(@Param("productId") Long productId);

	interface ProductRatingSummary {
		Long getProductId();
		Double getAverageRating();
		Long getReviewCount();
	}
}
