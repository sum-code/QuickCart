package com.quickcart.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.quickcart.user.repository.AppUserRepository;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			JwtAuthenticationFilter jwtAuthenticationFilter,
			OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
			GrantedAuthoritiesMapper oAuth2AuthoritiesMapper
	) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**", "/oauth2/**", "/login/**", "/api/v1/products/**").permitAll()
						.requestMatchers("/api/admin/**", "/api/v1/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/user/**", "/api/v1/orders/**").hasAnyRole("USER", "ADMIN")
						.anyRequest().authenticated()
				)
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(oAuth2AuthoritiesMapper))
						.successHandler(oAuth2LoginSuccessHandler)
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public GrantedAuthoritiesMapper oAuth2AuthoritiesMapper(AppUserRepository userRepository) {
		return (authorities) -> {
			Set<GrantedAuthority> mapped = new HashSet<>(authorities);
			String email = null;

			for (GrantedAuthority authority : authorities) {
				if (authority instanceof OidcUserAuthority oidcAuthority) {
					Object value = oidcAuthority.getAttributes().get("email");
					if (value != null) {
						email = String.valueOf(value);
						break;
					}
				}
				if (authority instanceof OAuth2UserAuthority oauth2Authority) {
					Object value = oauth2Authority.getAttributes().get("email");
					if (value != null) {
						email = String.valueOf(value);
						break;
					}
				}
			}

			if (email != null) {
				userRepository.findByEmail(email).ifPresent(user ->
						user.getRoles().forEach(role ->
								mapped.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
						)
				);
			}

			return mapped;
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}

