package com.quickcart.product.service;

import com.quickcart.product.dto.PagedResponse;
import com.quickcart.product.dto.ProductResponse;
import org.springframework.data.domain.Pageable;

public interface ProductCatalogService {
	PagedResponse<ProductResponse> getProducts(String search, Pageable pageable);
	ProductResponse getProductById(Long id);
}
