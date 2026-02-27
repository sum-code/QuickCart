package com.quickcart.auth.controller;

import com.quickcart.auth.dto.AuthResponse;
import com.quickcart.auth.dto.LoginRequest;
import com.quickcart.auth.dto.RegisterRequest;
import com.quickcart.auth.service.AuthService;
import com.quickcart.security.JwtService;
import jakarta.validation.Valid;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	public AuthController(AuthService authService, JwtService jwtService) {
		this.authService = authService;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
		var session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();

		Cookie cookie = new Cookie("JSESSIONID", "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		Map<String, Object> body = new HashMap<>();
		body.put("loggedOut", true);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/debug-token")
	public ResponseEntity<Map<String, Object>> debugToken(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestParam(value = "token", required = false) String tokenParam
	) {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		} else if (tokenParam != null && !tokenParam.isBlank()) {
			token = tokenParam.trim();
		}

		Map<String, Object> body = new HashMap<>();
		body.put("hasToken", token != null);
		if (token == null) {
			return ResponseEntity.ok(body);
		}

		boolean valid = jwtService.isTokenValid(token);
		body.put("valid", valid);
		if (valid) {
			body.put("subject", jwtService.extractSubject(token));
			body.put("roles", jwtService.extractRoles(token));
		}
		return ResponseEntity.ok(body);
	}

	@GetMapping("/debug-context")
	public ResponseEntity<Map<String, Object>> debugContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> body = new HashMap<>();
		if (authentication == null) {
			body.put("authenticated", false);
			return ResponseEntity.ok(body);
		}

		body.put("authenticated", authentication.isAuthenticated());
		body.put("name", authentication.getName());
		body.put(
				"authorities",
				authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
		);
		return ResponseEntity.ok(body);
	}
}
