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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

	private final AppUserRepository userRepository;
	private final AppRoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final String adminRegistrationCode;

	public AuthServiceImpl(
			AppUserRepository userRepository,
			AppRoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			@Value("${app.admin.registration.code:}") String adminRegistrationCode
	) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.adminRegistrationCode = adminRegistrationCode == null ? "" : adminRegistrationCode.trim();
	}

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		Role requestedRole = request.getRole() == null ? Role.USER : request.getRole();
		Optional<AppUser> existingUserOptional = userRepository.findByEmail(request.getEmail());

		if (existingUserOptional.isPresent()) {
			if (requestedRole != Role.ADMIN) {
				throw new IllegalArgumentException("Email already registered");
			}

			validateAdminCode(request.getAdminCode());

			AppUser existingUser = existingUserOptional.get();
			Set<AppRole> updatedRoles = new HashSet<>(existingUser.getRoles());
			updatedRoles.add(getOrCreateRole(Role.ADMIN));
			existingUser.setRoles(updatedRoles);

			AppUser saved = userRepository.save(existingUser);
			return buildAuthResponse(saved);
		}

		if (requestedRole == Role.ADMIN) {
			validateAdminCode(request.getAdminCode());
		}

		Set<AppRole> roles = Set.of(getOrCreateRole(requestedRole));

		AppUser user = new AppUser();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(roles);

		AppUser saved = userRepository.save(user);
		return buildAuthResponse(saved);
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
		return buildAuthResponse(user);
	}

	private void validateAdminCode(String providedCodeRaw) {
		String providedCode = providedCodeRaw == null ? "" : providedCodeRaw.trim();
		if (adminRegistrationCode.isEmpty() || !adminRegistrationCode.equals(providedCode)) {
			throw new IllegalArgumentException("Invalid admin registration code");
		}
	}

	private AuthResponse buildAuthResponse(AppUser user) {
		Set<Role> roleNames = user.getRoles().stream().map(AppRole::getName).collect(java.util.stream.Collectors.toSet());
		String token = jwtService.generateToken(user.getEmail(), roleNames);
		return new AuthResponse(token, new UserResponse(user.getId(), user.getEmail(), roleNames));
	}

	private AppRole getOrCreateRole(Role role) {
		return roleRepository.findByName(role).orElseGet(() -> roleRepository.save(new AppRole(role)));
	}
}
