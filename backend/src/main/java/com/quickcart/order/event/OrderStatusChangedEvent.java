package com.quickcart.order.event;

import com.quickcart.order.entity.OrderStatus;

public class OrderStatusChangedEvent {
	private final Long orderId;
	private final String customerEmail;
	private final OrderStatus newStatus;
	private final String note;
	private final String courierName;
	private final String trackingNumber;

	public OrderStatusChangedEvent(
			Long orderId,
			String customerEmail,
			OrderStatus newStatus,
			String note,
			String courierName,
			String trackingNumber
	) {
		this.orderId = orderId;
		this.customerEmail = customerEmail;
		this.newStatus = newStatus;
		this.note = note;
		this.courierName = courierName;
		this.trackingNumber = trackingNumber;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public OrderStatus getNewStatus() {
		return newStatus;
	}

	public String getNote() {
		return note;
	}

	public String getCourierName() {
		return courierName;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}
}
