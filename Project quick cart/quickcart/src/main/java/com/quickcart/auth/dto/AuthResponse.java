package com.quickcart.auth.dto;

public class AuthResponse {
	private String tokenType = "Bearer";
	private String accessToken;
	private UserResponse user;

	public AuthResponse() {
	}

	public AuthResponse(String accessToken, UserResponse user) {
		this.accessToken = accessToken;
		this.user = user;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public UserResponse getUser() {
		return user;
	}

	public void setUser(UserResponse user) {
		this.user = user;
	}
}
