package com.quickcart.product.specification;

import com.quickcart.product.entity.Product;
import com.quickcart.review.entity.Review;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

	private ProductSpecifications() {
	}

	public static Specification<Product> filterBy(
			String search,
			String category,
			BigDecimal minPrice,
			BigDecimal maxPrice,
			Integer minRating
	) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (search != null && !search.isBlank()) {
				String pattern = "%" + search.trim().toLowerCase() + "%";
				predicates.add(
						cb.or(
								cb.like(cb.lower(root.get("name")), pattern),
								cb.like(cb.lower(root.get("description")), pattern),
								cb.like(cb.lower(root.get("category")), pattern)
						)
				);
			}

			if (category != null && !category.isBlank()) {
				predicates.add(cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
			}

			if (minPrice != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
			}

			if (maxPrice != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
			}

			if (minRating != null && minRating > 0) {
				predicates.add(buildMinRatingPredicate(root, query.subquery(Long.class), cb, minRating));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private static Predicate buildMinRatingPredicate(
			Root<Product> productRoot,
			Subquery<Long> subquery,
			jakarta.persistence.criteria.CriteriaBuilder cb,
			Integer minRating
	) {
		Root<Review> reviewRoot = subquery.from(Review.class);
		subquery.select(reviewRoot.get("product").get("id"));
		subquery.where(cb.equal(reviewRoot.get("product").get("id"), productRoot.get("id")));
		subquery.groupBy(reviewRoot.get("product").get("id"));
		subquery.having(cb.greaterThanOrEqualTo(cb.avg(reviewRoot.get("rating")), minRating.doubleValue()));
		return cb.exists(subquery);
	}
}
