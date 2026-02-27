package com.quickcart.order.controller;

import com.quickcart.order.dto.OrderTrackingResponseDTO;
import com.quickcart.order.dto.UpdateOrderStatusRequest;
import com.quickcart.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

	private final OrderService orderService;

	public AdminOrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public ResponseEntity<List<OrderTrackingResponseDTO>> allOrders() {
		return ResponseEntity.ok(orderService.allTrackingOrders());
	}

	@PutMapping("/{orderId}/status")
	public ResponseEntity<OrderTrackingResponseDTO> updateStatus(
			@PathVariable Long orderId,
			@Valid @RequestBody UpdateOrderStatusRequest request
	) {
		return ResponseEntity.ok(orderService.adminUpdateStatus(orderId, request));
	}
}
