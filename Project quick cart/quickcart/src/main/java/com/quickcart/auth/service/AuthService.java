package com.quickcart.auth.service;

import com.quickcart.auth.dto.AuthResponse;
import com.quickcart.auth.dto.LoginRequest;
import com.quickcart.auth.dto.RegisterRequest;

public interface AuthService {
	AuthResponse register(RegisterRequest request);
	AuthResponse login(LoginRequest request);
}
