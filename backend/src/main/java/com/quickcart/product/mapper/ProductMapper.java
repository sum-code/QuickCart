package com.quickcart.product.mapper;

import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.dto.ProductUpsertRequest;
import com.quickcart.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

	public Product toEntity(ProductUpsertRequest request) {
		Product product = new Product();
		apply(product, request);
		return product;
	}

	public void apply(Product product, ProductUpsertRequest request) {
		product.setName(request.getName());
		product.setBrand(request.getBrand());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());
		product.setCategory(request.getCategory());
	}

	public ProductResponse toResponse(Product product) {
		return toResponse(product, 0.0, 0L);
	}

	public ProductResponse toResponse(Product product, Double averageRating, Long reviewCount) {
		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getBrand(),
				product.getDescription(),
				product.getPrice(),
				product.getStockQuantity(),
				product.getImageUrl(),
				product.getCategory(),
				averageRating,
				reviewCount,
				product.getCreatedAt(),
				product.getUpdatedAt()
		);
	}
}
