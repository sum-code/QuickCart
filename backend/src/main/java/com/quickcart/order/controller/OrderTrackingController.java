package com.quickcart.order.controller;

import com.quickcart.order.dto.OrderTrackingResponseDTO;
import com.quickcart.order.service.OrderService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderTrackingController {

	private final OrderService orderService;

	public OrderTrackingController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public ResponseEntity<List<OrderTrackingResponseDTO>> myTrackingOrders() {
		return ResponseEntity.ok(orderService.myTrackingOrders());
	}

	@GetMapping("/{orderId}/tracking")
	public ResponseEntity<OrderTrackingResponseDTO> tracking(@PathVariable Long orderId) {
		return ResponseEntity.ok(orderService.getOrderTracking(orderId));
	}

	@PostMapping("/{orderId}/return")
	public ResponseEntity<OrderTrackingResponseDTO> requestReturn(@PathVariable Long orderId) {
		return ResponseEntity.ok(orderService.requestReturn(orderId));
	}

	@GetMapping(value = "/{orderId}/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> invoice(@PathVariable Long orderId) {
		byte[] payload = orderService.generateInvoice(orderId);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("invoice-" + orderId + ".pdf").build().toString())
				.contentType(MediaType.APPLICATION_PDF)
				.body(payload);
	}
}
