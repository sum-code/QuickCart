package com.quickcart.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductUpsertRequest {

	@NotBlank
	@Size(max = 120)
	private String name;

	@NotBlank
	@Size(max = 64)
	private String sku;

	@Size(max = 2000)
	private String description;

	@NotNull
	@DecimalMin(value = "0.01")
	private BigDecimal price;

	@NotNull
	@Min(0)
	private Integer stockQuantity;

	@NotBlank
	@Size(max = 100)
	private String category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public Integer getStock() {
		return stockQuantity;
	}

	public void setStock(Integer stock) {
		this.stockQuantity = stock;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
