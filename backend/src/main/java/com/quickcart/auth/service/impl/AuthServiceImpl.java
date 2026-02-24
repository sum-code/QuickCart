package com.quickcart.auth.service.impl;

import com.quickcart.auth.dto.AuthResponse;
import com.quickcart.auth.dto.LoginRequest;
import com.quickcart.auth.dto.RegisterRequest;
import com.quickcart.auth.dto.UserResponse;
import com.quickcart.auth.service.AuthService;
import com.quickcart.security.JwtService;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.entity.AppRole;
import com.quickcart.user.entity.Role;
import com.quickcart.user.repository.AppUserRepository;
import com.quickcart.user.repository.AppRoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

	private final AppUserRepository userRepository;
	private final AppRoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthServiceImpl(
			AppUserRepository userRepository,
			AppRoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService
	) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already registered");
		}

		Set<Role> roleNames = request.getRoles();
		if (roleNames == null || roleNames.isEmpty()) {
			roleNames = Set.of(Role.USER);
		} else {
			roleNames = new HashSet<>(roleNames);
		}
		Set<AppRole> roles = roleNames.stream().map(this::getOrCreateRole).collect(java.util.stream.Collectors.toSet());

		AppUser user = new AppUser();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(roles);

		AppUser saved = userRepository.save(user);
		Set<Role> savedRoleNames = saved.getRoles().stream().map(AppRole::getName).collect(java.util.stream.Collectors.toSet());
		String token = jwtService.generateToken(saved.getEmail(), savedRoleNames);
		return new AuthResponse(token, new UserResponse(saved.getId(), saved.getEmail(), savedRoleNames));
	}

	@Override
	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);
		if (!authentication.isAuthenticated()) {
			throw new BadCredentialsException("Invalid credentials");
		}

		AppUser user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

		Set<Role> roleNames = user.getRoles().stream().map(AppRole::getName).collect(java.util.stream.Collectors.toSet());
		String token = jwtService.generateToken(user.getEmail(), roleNames);
		return new AuthResponse(token, new UserResponse(user.getId(), user.getEmail(), roleNames));
	}

	private AppRole getOrCreateRole(Role role) {
		return roleRepository.findByName(role).orElseGet(() -> roleRepository.save(new AppRole(role)));
	}
}
