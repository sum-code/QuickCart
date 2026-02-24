package com.quickcart.product.service.impl;

import com.quickcart.product.dto.ProductCreateRequest;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.dto.ProductUpdateRequest;
import com.quickcart.product.entity.Product;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	@Transactional
	public ProductResponse create(ProductCreateRequest request) {
		Product product = new Product();
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());
		return toResponse(productRepository.save(product));
	}

	@Override
	@Transactional
	public ProductResponse update(Long id, ProductUpdateRequest request) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		if (request.getName() != null) {
			product.setName(request.getName());
		}
		if (request.getDescription() != null) {
			product.setDescription(request.getDescription());
		}
		if (request.getPrice() != null) {
			product.setPrice(request.getPrice());
		}
		if (request.getStock() != null) {
			product.setStock(request.getStock());
		}

		return toResponse(productRepository.save(product));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!productRepository.existsById(id)) {
			throw new IllegalArgumentException("Product not found");
		}
		productRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getById(Long id) {
		return productRepository.findById(id)
				.map(this::toResponse)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getAll() {
		return productRepository.findAll().stream().map(this::toResponse).toList();
	}

	private ProductResponse toResponse(Product product) {
		return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock());
	}
}
