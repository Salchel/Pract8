package com.example.demo.repository;

import com.example.demo.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    // есть ли уже этот товар в заказе (в БД пара order_id + product_id уникальна)
    Optional<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);

    boolean existsByProductId(Long productId);
}