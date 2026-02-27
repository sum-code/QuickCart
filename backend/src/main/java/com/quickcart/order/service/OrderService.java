package com.quickcart.order.service;

import com.quickcart.order.dto.OrderResponse;
import com.quickcart.order.dto.OrderTrackingResponseDTO;
import com.quickcart.order.dto.UpdateOrderStatusRequest;
import com.quickcart.order.entity.OrderStatus;

import java.util.List;

public interface OrderService {
	OrderResponse placeOrderFromCart();
	List<OrderResponse> myOrders();
	OrderResponse myOrder(Long orderId);
	OrderResponse updateStatus(Long orderId, OrderStatus status);
	List<OrderResponse> allOrders();
	OrderTrackingResponseDTO getOrderTracking(Long orderId);
	List<OrderTrackingResponseDTO> myTrackingOrders();
	OrderTrackingResponseDTO adminUpdateStatus(Long orderId, UpdateOrderStatusRequest request);
	List<OrderTrackingResponseDTO> allTrackingOrders();
	OrderTrackingResponseDTO requestReturn(Long orderId);
	byte[] generateInvoice(Long orderId);
}
