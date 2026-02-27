package com.quickcart.security;

import com.quickcart.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {
	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	private final SecretKey secretKey;
	private final long expirationMinutes;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-minutes:60}") long expirationMinutes
	) {
		this.secretKey = buildKey(secret);
		this.expirationMinutes = expirationMinutes;
	}

	public String generateToken(String subjectEmail, Set<Role> roles) {
		Instant now = Instant.now();
		Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);

		return Jwts.builder()
				.subject(subjectEmail)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiresAt))
				.claim("roles", roles.stream().map(Enum::name).toList())
				.signWith(secretKey, Jwts.SIG.HS256)
				.compact();
	}

	public String extractSubject(String token) {
		return parseAllClaims(token).getSubject();
	}

	public Set<Role> extractRoles(String token) {
		Claims claims = parseAllClaims(token);
		Object rawRoles = claims.get("roles");
		if (!(rawRoles instanceof Collection<?> collection)) {
			return Set.of();
		}

		Set<Role> roles = new HashSet<>();
		for (Object raw : collection) {
			if (raw == null) {
				continue;
			}
			try {
				roles.add(Role.valueOf(String.valueOf(raw)));
			} catch (IllegalArgumentException ignore) {
				// Ignore unknown role values.
			}
		}
		return roles;
	}

	public boolean isTokenValid(String token) {
		try {
			parseAllClaims(token);
			return true;
		} catch (Exception ex) {
			logger.debug("Invalid JWT: {}", ex.getMessage());
			return false;
		}
	}

	private Claims parseAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private static SecretKey buildKey(String configuredSecret) {
		String secret = configuredSecret == null ? "" : configuredSecret.trim();
		if (secret.isEmpty()) {
			throw new IllegalStateException("app.jwt.secret is required");
		}

		// Accept either base64 or raw string. For HS256, key must be >= 256 bits.
		byte[] keyBytes;
		try {
			keyBytes = Decoders.BASE64.decode(secret);
		} catch (Exception ignore) {
			keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		}

		if (keyBytes.length < 32) {
			throw new IllegalStateException("app.jwt.secret must be at least 32 bytes (256-bit) for HS256");
		}

		return Keys.hmacShaKeyFor(keyBytes);
	}
}
