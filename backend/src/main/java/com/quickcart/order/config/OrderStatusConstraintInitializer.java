package com.quickcart.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class OrderStatusConstraintInitializer implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(OrderStatusConstraintInitializer.class);

	private final JdbcTemplate jdbcTemplate;
	private final DataSource dataSource;

	public OrderStatusConstraintInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
		this.jdbcTemplate = jdbcTemplate;
		this.dataSource = dataSource;
	}

	@Override
	public void run(String... args) {
		try {
			String databaseName;
			try (var connection = dataSource.getConnection()) {
				databaseName = connection.getMetaData().getDatabaseProductName();
			}
			if (databaseName == null || !databaseName.toLowerCase().contains("postgres")) {
				return;
			}

			jdbcTemplate.execute("ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check");

			jdbcTemplate.update("UPDATE orders SET status = 'PLACED' WHERE status = 'PENDING'");
			jdbcTemplate.update("UPDATE orders SET status = 'PROCESSING' WHERE status = 'PAID'");

			jdbcTemplate.execute("""
					ALTER TABLE orders
					ADD CONSTRAINT orders_status_check CHECK (
						status IN (
							'PLACED',
							'PROCESSING',
							'SHIPPED',
							'DELIVERED',
							'RETURN_REQUESTED',
							'RETURN_APPROVED',
							'REFUNDED',
							'CANCELLED'
						)
					)
					""");

			log.info("Order status constraint migration applied successfully.");
		} catch (Exception ex) {
			log.warn("Order status constraint migration skipped: {}", ex.getMessage());
		}
	}
}
