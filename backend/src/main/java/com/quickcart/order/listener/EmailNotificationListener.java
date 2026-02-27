package com.quickcart.order.listener;

import com.quickcart.order.event.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
public class EmailNotificationListener {
	private static final Logger log = LoggerFactory.getLogger(EmailNotificationListener.class);

	@Async
	@EventListener
	public void onOrderStatusChanged(OrderStatusChangedEvent event) {
		String content = "[EMAIL-SIMULATION] to=" + event.getCustomerEmail() +
				", orderId=" + event.getOrderId() +
				", status=" + event.getNewStatus() +
				", note=" + (event.getNote() == null ? "" : event.getNote()) +
				", courier=" + (event.getCourierName() == null ? "" : event.getCourierName()) +
				", trackingNumber=" + (event.getTrackingNumber() == null ? "" : event.getTrackingNumber());
		log.info(content);
	}
}
