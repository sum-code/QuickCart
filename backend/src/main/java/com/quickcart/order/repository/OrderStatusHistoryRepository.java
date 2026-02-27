package com.quickcart.order.repository;

import com.quickcart.order.entity.OrderStatus;
import com.quickcart.order.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
	List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(Long orderId);
	Optional<OrderStatusHistory> findFirstByOrderIdAndStatusOrderByCreatedAtDesc(Long orderId, OrderStatus status);
}
