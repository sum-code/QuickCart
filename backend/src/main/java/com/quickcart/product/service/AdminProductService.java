package com.quickcart.product.service;

import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.dto.ProductUpsertRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AdminProductService {
	ProductResponse create(ProductUpsertRequest request, MultipartFile image);
	ProductResponse update(Long id, ProductUpsertRequest request, MultipartFile image);
	void delete(Long id);
}
