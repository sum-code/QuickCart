package com.quickcart.order.controller;

import com.quickcart.order.dto.OrderResponse;
import com.quickcart.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<OrderResponse> placeOrder() {
		return ResponseEntity.ok(orderService.placeOrderFromCart());
	}

	@GetMapping
	public ResponseEntity<List<OrderResponse>> myOrders() {
		return ResponseEntity.ok(orderService.myOrders());
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponse> myOrder(@PathVariable Long orderId) {
		return ResponseEntity.ok(orderService.myOrder(orderId));
	}
}
