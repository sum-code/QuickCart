package com.quickcart.product.repository;

import com.quickcart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	boolean existsBySkuIgnoreCase(String sku);
	boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);
	Page<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category, Pageable pageable);
}
