package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    // корзина пользователя = его заказ со статусом NEW
    Optional<Order> findFirstByUserIdAndStatus(Long userId, OrderStatus status);

    boolean existsByUserId(Long userId);
}