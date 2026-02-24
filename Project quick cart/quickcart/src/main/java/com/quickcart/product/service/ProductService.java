package com.quickcart.product.service;

import com.quickcart.product.dto.ProductCreateRequest;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.dto.ProductUpdateRequest;

import java.util.List;

public interface ProductService {
	ProductResponse create(ProductCreateRequest request);
	ProductResponse update(Long id, ProductUpdateRequest request);
	void delete(Long id);
	ProductResponse getById(Long id);
	List<ProductResponse> getAll();
}
