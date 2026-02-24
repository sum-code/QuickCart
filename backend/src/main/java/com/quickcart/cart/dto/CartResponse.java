package com.quickcart.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
	private List<CartItemResponse> items;
	private Integer totalQuantity;
	private BigDecimal totalAmount;

	public CartResponse() {
	}

	public CartResponse(List<CartItemResponse> items, Integer totalQuantity, BigDecimal totalAmount) {
		this.items = items;
		this.totalQuantity = totalQuantity;
		this.totalAmount = totalAmount;
	}

	public List<CartItemResponse> getItems() {
		return items;
	}

	public void setItems(List<CartItemResponse> items) {
		this.items = items;
	}

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
}
