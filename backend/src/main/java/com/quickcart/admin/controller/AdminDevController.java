package com.quickcart.admin.controller;

import com.quickcart.admin.service.AdminDevToolsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dev")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDevController {

	private final AdminDevToolsService adminDevToolsService;

	@Value("${app.dev.cleanup.enabled:false}")
	private boolean cleanupEnabled;

	public AdminDevController(AdminDevToolsService adminDevToolsService) {
		this.adminDevToolsService = adminDevToolsService;
	}

	@PostMapping("/cleanup-test-users")
	public ResponseEntity<Map<String, Integer>> cleanupTestUsers() {
		if (!cleanupEnabled) {
			throw new IllegalStateException("Dev cleanup is disabled. Enable app.dev.cleanup.enabled=true to use this endpoint.");
		}

		return ResponseEntity.ok(adminDevToolsService.cleanupTestUsers());
	}
}
