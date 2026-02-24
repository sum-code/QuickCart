package com.quickcart.user.service;

import com.quickcart.auth.dto.UserResponse;
import com.quickcart.user.entity.AppUser;

import java.util.List;

public interface UserService {
	AppUser getCurrentUser();
	UserResponse me();
	List<UserResponse> listUsers();
}
