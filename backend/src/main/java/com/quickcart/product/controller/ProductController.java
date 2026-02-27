package com.quickcart.product.controller;

import com.quickcart.product.dto.PagedResponse;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.service.ProductCatalogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final ProductCatalogService productCatalogService;

	public ProductController(ProductCatalogService productCatalogService) {
		this.productCatalogService = productCatalogService;
	}

	@GetMapping
	public ResponseEntity<PagedResponse<ProductResponse>> getAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(required = false) Integer minRating
	) {
		Sort sort = "asc".equalsIgnoreCase(direction)
				? Sort.by(resolveSortField(sortBy)).ascending()
				: Sort.by(resolveSortField(sortBy)).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return ResponseEntity.ok(productCatalogService.getProducts(search, category, minPrice, maxPrice, minRating, pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(productCatalogService.getProductById(id));
	}

	private String resolveSortField(String requestedField) {
		Set<String> allowed = Set.of("createdAt", "price", "name");
		return allowed.contains(requestedField) ? requestedField : "createdAt";
	}
}
