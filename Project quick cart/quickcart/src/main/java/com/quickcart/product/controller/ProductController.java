package com.quickcart.product.controller;

import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAll() {
		return ResponseEntity.ok(productService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getById(id));
	}
}
