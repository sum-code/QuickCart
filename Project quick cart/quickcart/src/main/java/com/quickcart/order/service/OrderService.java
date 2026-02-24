package com.quickcart.order.service;

import com.quickcart.order.dto.OrderResponse;
import com.quickcart.order.entity.OrderStatus;

import java.util.List;

public interface OrderService {
	OrderResponse placeOrderFromCart();
	List<OrderResponse> myOrders();
	OrderResponse myOrder(Long orderId);
	OrderResponse updateStatus(Long orderId, OrderStatus status);
	List<OrderResponse> allOrders();
}
