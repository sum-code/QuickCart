package com.quickcart.order.dto;

import com.quickcart.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderTrackingResponseDTO {
	private Long id;
	private String userEmail;
	private OrderStatus status;
	private BigDecimal totalAmount;
	private Instant createdAt;
	private String courierName;
	private String trackingNumber;
	private List<OrderItemResponse> items;
	private List<OrderStatusHistoryDTO> history;

	public OrderTrackingResponseDTO() {
	}

	public OrderTrackingResponseDTO(
			Long id,
			String userEmail,
			OrderStatus status,
			BigDecimal totalAmount,
			Instant createdAt,
			String courierName,
			String trackingNumber,
			List<OrderItemResponse> items,
			List<OrderStatusHistoryDTO> history
	) {
		this.id = id;
		this.userEmail = userEmail;
		this.status = status;
		this.totalAmount = totalAmount;
		this.createdAt = createdAt;
		this.courierName = courierName;
		this.trackingNumber = trackingNumber;
		this.items = items;
		this.history = history;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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

	public String getCourierName() {
		return courierName;
	}

	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public List<OrderItemResponse> getItems() {
		return items;
	}

	public void setItems(List<OrderItemResponse> items) {
		this.items = items;
	}

	public List<OrderStatusHistoryDTO> getHistory() {
		return history;
	}

	public void setHistory(List<OrderStatusHistoryDTO> history) {
		this.history = history;
	}
}
