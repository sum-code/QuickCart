package com.quickcart.product.repository;

import com.quickcart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	boolean existsByBrandIgnoreCase(String brand);
	boolean existsByBrandIgnoreCaseAndIdNot(String brand, Long id);
	Page<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category, Pageable pageable);
}
