package com.quickcart.product.service.impl;

import com.quickcart.cloudinary.service.ImageUploadService;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.dto.ProductUpsertRequest;
import com.quickcart.product.entity.Product;
import com.quickcart.product.exception.ProductNotFoundException;
import com.quickcart.product.mapper.ProductMapper;
import com.quickcart.product.repository.ProductRepository;
import com.quickcart.product.service.AdminProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminProductServiceImpl implements AdminProductService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final ImageUploadService imageUploadService;

	public AdminProductServiceImpl(
			ProductRepository productRepository,
			ProductMapper productMapper,
			ImageUploadService imageUploadService
	) {
		this.productRepository = productRepository;
		this.productMapper = productMapper;
		this.imageUploadService = imageUploadService;
	}

	@Override
	@Transactional
	public ProductResponse create(ProductUpsertRequest request, MultipartFile image) {
		validateUniqueBrandForCreate(request.getBrand());
		Product product = productMapper.toEntity(request);
		if (image != null && !image.isEmpty()) {
			product.setImageUrl(imageUploadService.upload(image));
		}
		return productMapper.toResponse(productRepository.save(product));
	}

	@Override
	@Transactional
	public ProductResponse update(Long id, ProductUpsertRequest request, MultipartFile image) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
		validateUniqueBrandForUpdate(request.getBrand(), id);
		productMapper.apply(product, request);
		if (image != null && !image.isEmpty()) {
			product.setImageUrl(imageUploadService.upload(image));
		}
		return productMapper.toResponse(productRepository.save(product));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!productRepository.existsById(id)) {
			throw new ProductNotFoundException(id);
		}
		productRepository.deleteById(id);
	}

	private void validateUniqueBrandForCreate(String brand) {
		if (productRepository.existsByBrandIgnoreCase(brand)) {
			throw new IllegalArgumentException("Brand already exists: " + brand);
		}
	}

	private void validateUniqueBrandForUpdate(String brand, Long id) {
		if (productRepository.existsByBrandIgnoreCaseAndIdNot(brand, id)) {
			throw new IllegalArgumentException("Brand already exists: " + brand);
		}
	}
}
