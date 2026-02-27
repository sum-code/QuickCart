package com.quickcart.admin.service.impl;

import com.quickcart.admin.service.AdminDevToolsService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminDevToolsServiceImpl implements AdminDevToolsService {

	private static final String MATCH_USERS_CLAUSE = "email like 'wishdebug%' or email like 'admin_test_%'";

	private final JdbcTemplate jdbcTemplate;

	public AdminDevToolsServiceImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public Map<String, Integer> cleanupTestUsers() {
		Map<String, Integer> result = new LinkedHashMap<>();

		result.put("wishlists", jdbcTemplate.update("""
				delete from wishlists where user_id in (
					select id from users where %s
				)
				""".formatted(MATCH_USERS_CLAUSE)));

		result.put("cartItems", jdbcTemplate.update("""
				delete from cart_items where user_id in (
					select id from users where %s
				)
				""".formatted(MATCH_USERS_CLAUSE)));

		result.put("reviews", jdbcTemplate.update("""
				delete from reviews where user_id in (
					select id from users where %s
				)
				""".formatted(MATCH_USERS_CLAUSE)));

		result.put("orderItems", jdbcTemplate.update("""
				delete from order_items where order_id in (
					select id from orders where user_id in (
						select id from users where %s
					)
				)
				""".formatted(MATCH_USERS_CLAUSE)));

		result.put("orders", jdbcTemplate.update("""
				delete from orders where user_id in (
					select id from users where %s
				)
				""".formatted(MATCH_USERS_CLAUSE)));

		result.put("userRoles", jdbcTemplate.update("""
				delete from app_user_roles where user_id in (
					select id from users where %s
				)
				""".formatted(MATCH_USERS_CLAUSE)));

		result.put("users", jdbcTemplate.update("""
				delete from users where %s
				""".formatted(MATCH_USERS_CLAUSE)));

		return result;
	}
}
