package com.quickcart.admin.controller;

import com.quickcart.auth.dto.UserResponse;
import com.quickcart.order.dto.OrderResponse;
import com.quickcart.order.dto.UpdateOrderStatusRequest;
import com.quickcart.order.service.OrderService;
import com.quickcart.product.dto.ProductCreateRequest;
import com.quickcart.product.dto.ProductResponse;
import com.quickcart.product.dto.ProductUpdateRequest;
import com.quickcart.product.service.ProductService;
import com.quickcart.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final ProductService productService;
	private final OrderService orderService;
	private final UserService userService;

	public AdminController(ProductService productService, OrderService orderService, UserService userService) {
		this.productService = productService;
		this.orderService = orderService;
		this.userService = userService;
	}

	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("admin-ok");
	}

	@PostMapping("/products")
	public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
		return ResponseEntity.ok(productService.create(request));
	}

	@PutMapping("/products/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
		return ResponseEntity.ok(productService.update(id, request));
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
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
