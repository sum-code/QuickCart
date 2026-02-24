package com.quickcart.user.controller;

import com.quickcart.auth.dto.UserResponse;
import com.quickcart.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponse> me() {
		return ResponseEntity.ok(userService.me());
	}

	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("user-ok");
	}
}
