package com.quickcart.order.dto;

import com.quickcart.order.entity.OrderStatus;

import java.time.Instant;

public class OrderStatusHistoryDTO {
	private OrderStatus status;
	private String notes;
	private Instant createdAt;

	public OrderStatusHistoryDTO() {
	}

	public OrderStatusHistoryDTO(OrderStatus status, String notes, Instant createdAt) {
		this.status = status;
		this.notes = notes;
		this.createdAt = createdAt;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
