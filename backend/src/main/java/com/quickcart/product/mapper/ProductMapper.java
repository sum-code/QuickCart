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
		product.setSku(request.getSku());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());
		product.setCategory(request.getCategory());
	}

	public ProductResponse toResponse(Product product) {
		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getSku(),
				product.getDescription(),
				product.getPrice(),
				product.getStockQuantity(),
				product.getImageUrl(),
				product.getCategory(),
				product.getCreatedAt(),
				product.getUpdatedAt()
		);
	}
}
