package com.quickcart.order.dto;

import com.quickcart.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {
	@NotNull
	private OrderStatus status;
	private String courierName;
	private String trackingNumber;
	private String adminNote;

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
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

	public String getAdminNote() {
		return adminNote;
	}

	public void setAdminNote(String adminNote) {
		this.adminNote = adminNote;
	}
}
