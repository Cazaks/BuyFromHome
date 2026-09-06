package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByOrderIdAndUser_UserId(Long orderId, Long userId);
}
