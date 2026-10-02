package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @NotNull(message = "Укажите статус заказа")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.NEW;

    @NotNull(message = "Укажите сумму заказа")
    @DecimalMin(value = "0.00", message = "Сумма не может быть отрицательной")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // связь N:1 (много заказов - один пользователь)
    @NotNull(message = "Выберите пользователя")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // связь 1:N (один заказ - много позиций)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}