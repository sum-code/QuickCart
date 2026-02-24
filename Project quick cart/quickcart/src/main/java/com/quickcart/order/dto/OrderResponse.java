package com.quickcart.order.dto;

import com.quickcart.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderResponse {
	private Long id;
	private OrderStatus status;
	private BigDecimal totalAmount;
	private Instant createdAt;
	private List<OrderItemResponse> items;

	public OrderResponse() {
	}

	public OrderResponse(Long id, OrderStatus status, BigDecimal totalAmount, Instant createdAt, List<OrderItemResponse> items) {
		this.id = id;
		this.status = status;
		this.totalAmount = totalAmount;
		this.createdAt = createdAt;
		this.items = items;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public List<OrderItemResponse> getItems() {
		return items;
	}

	public void setItems(List<OrderItemResponse> items) {
		this.items = items;
	}
}
