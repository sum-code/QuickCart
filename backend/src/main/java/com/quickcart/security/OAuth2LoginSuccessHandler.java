package com.quickcart.security;

import com.quickcart.auth.dto.UserResponse;
import com.quickcart.user.entity.AppRole;
import com.quickcart.user.entity.AppUser;
import com.quickcart.user.entity.Role;
import com.quickcart.user.repository.AppRoleRepository;
import com.quickcart.user.repository.AppUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final AppUserRepository userRepository;
	private final AppRoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final String frontendRedirectUri;

	public OAuth2LoginSuccessHandler(
			AppUserRepository userRepository,
			AppRoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			@Value("${app.oauth2.redirect-uri}") String frontendRedirectUri
	) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.frontendRedirectUri = frontendRedirectUri;
	}

	@Override
	@Transactional
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");

		if (email == null || email.isBlank()) {
			getRedirectStrategy().sendRedirect(request, response, frontendRedirectUri + "?error=oauth_email_missing");
			return;
		}

		AppUser appUser = userRepository.findByEmail(email).orElseGet(() -> createNewUser(email));
		Set<Role> roleNames = appUser.getRoles().stream().map(AppRole::getName).collect(Collectors.toSet());
		String token = jwtService.generateToken(appUser.getEmail(), roleNames);
		UserResponse user = new UserResponse(appUser.getId(), appUser.getEmail(), roleNames);

		String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
				.queryParam("token", token)
				.queryParam("email", user.getEmail())
				.queryParam("roles", String.join(",", roleNames.stream().map(Enum::name).toList()))
				.build(true)
				.toUriString();

		clearAuthenticationAttributes(request);
		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}

	private AppUser createNewUser(String email) {
		AppRole userRole = roleRepository.findByName(Role.USER)
				.orElseGet(() -> roleRepository.save(new AppRole(Role.USER)));

		AppUser user = new AppUser();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
		user.setRoles(Set.of(userRole));
		return userRepository.save(user);
	}
}
