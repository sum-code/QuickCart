package com.quickcart.order.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
	private Long productId;
	private String productName;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal lineTotal;

	public OrderItemResponse() {
	}

	public OrderItemResponse(Long productId, String productName, BigDecimal price, Integer quantity, BigDecimal lineTotal) {
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
		this.lineTotal = lineTotal;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}
}
