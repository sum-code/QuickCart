package com.quickcart.user.service.impl;

import com.quickcart.auth.dto.UserResponse;
import com.quickcart.user.entity.AppRole;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.entity.Role;
import com.quickcart.user.repository.AppUserRepository;
import com.quickcart.user.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

	private final AppUserRepository userRepository;

	public UserServiceImpl(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public AppUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new BadCredentialsException("Not authenticated");
		}

		String email = resolveEmail(authentication);
		if (email == null || email.isBlank() || "anonymousUser".equalsIgnoreCase(email)) {
			throw new BadCredentialsException("Not authenticated");
		}
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new BadCredentialsException("User not found"));
	}

	private String resolveEmail(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails userDetails) {
			return userDetails.getUsername();
		}
		if (principal instanceof OAuth2User oauth2User) {
			String email = oauth2User.getAttribute("email");
			if (email != null) {
				return email;
			}
		}
		return authentication.getName();
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse me() {
		AppUser user = getCurrentUser();
		return toResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> listUsers() {
		return userRepository.findAll().stream().map(this::toResponse).toList();
	}

	private UserResponse toResponse(AppUser user) {
		Set<Role> roles = user.getRoles().stream().map(AppRole::getName).collect(Collectors.toSet());
		return new UserResponse(user.getId(), user.getEmail(), roles);
	}
}
