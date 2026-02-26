package com.quickcart.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.dto.ProductUpsertRequest;
import com.quickcart.product.service.AdminProductService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

	private final AdminProductService adminProductService;
	private final ObjectMapper objectMapper;
	private final Validator validator;

	public AdminProductController(AdminProductService adminProductService, ObjectMapper objectMapper, Validator validator) {
		this.adminProductService = adminProductService;
		this.objectMapper = objectMapper;
		this.validator = validator;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ProductResponse> create(
			@RequestPart("product") String productJson,
			@RequestPart(value = "image", required = false) MultipartFile image
	) throws JsonProcessingException {
		ProductUpsertRequest request = objectMapper.readValue(productJson, ProductUpsertRequest.class);
		return ResponseEntity.ok(adminProductService.create(validate(request), image));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ProductResponse> update(
			@PathVariable Long id,
			@RequestPart("product") String productJson,
			@RequestPart(value = "image", required = false) MultipartFile image
	) throws JsonProcessingException {
		ProductUpsertRequest request = objectMapper.readValue(productJson, ProductUpsertRequest.class);
		return ResponseEntity.ok(adminProductService.update(id, validate(request), image));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		adminProductService.delete(id);
		return ResponseEntity.noContent().build();
	}

	private @Valid ProductUpsertRequest validate(@Valid ProductUpsertRequest request) {
		var violations = validator.validate(request);
		if (!violations.isEmpty()) {
			throw new IllegalArgumentException(
					violations.stream()
							.map(v -> v.getPropertyPath() + ": " + v.getMessage())
							.sorted()
							.reduce((a, b) -> a + ", " + b)
							.orElse("Validation failed")
			);
		}
		return request;
	}
}
