package com.quickcart.product.service.impl;

import com.quickcart.product.dto.PagedResponse;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.entity.Product;
import com.quickcart.product.exception.ProductNotFoundException;
import com.quickcart.product.mapper.ProductMapper;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.product.service.ProductCatalogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCatalogServiceImpl implements ProductCatalogService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	public ProductCatalogServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
		this.productRepository = productRepository;
		this.productMapper = productMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<ProductResponse> getProducts(String search, Pageable pageable) {
		Page<Product> page;
		if (search == null || search.isBlank()) {
			page = productRepository.findAll(pageable);
		} else {
			page = productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(search, search, pageable);
		}

		return new PagedResponse<>(
				page.getContent().stream().map(productMapper::toResponse).toList(),
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
		return productMapper.toResponse(product);
	}
}
