package com.quickcart.product.service;

import com.quickcart.product.dto.PagedResponse;
import com.quickcart.product.dto.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductCatalogService {
	PagedResponse<ProductResponse> getProducts(String search, String category, BigDecimal minPrice, BigDecimal maxPrice, Integer minRating, Pageable pageable);
	ProductResponse getProductById(Long id);
}
