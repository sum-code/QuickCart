package com.quickcart.admin.controller;

import com.quickcart.auth.dto.UserResponse;
import com.quickcart.order.dto.OrderResponse;
import com.quickcart.order.dto.UpdateOrderStatusRequest;
import com.quickcart.order.service.OrderService;
import com.quickcart.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final OrderService orderService;
	private final UserService userService;

	public AdminController(OrderService orderService, UserService userService) {
		this.orderService = orderService;
		this.userService = userService;
	}

	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("admin-ok");
	}

	@GetMapping("/orders")
	public ResponseEntity<List<OrderResponse>> allOrders() {
		return ResponseEntity.ok(orderService.allOrders());
	}

	@PatchMapping("/orders/{orderId}/status")
	public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody UpdateOrderStatusRequest request) {
		return ResponseEntity.ok(orderService.updateStatus(orderId, request.getStatus()));
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserResponse>> users() {
		return ResponseEntity.ok(userService.listUsers());
	}
}
