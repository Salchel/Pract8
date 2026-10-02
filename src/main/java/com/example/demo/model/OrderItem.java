package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // связь N:1 с заказом
    @NotNull(message = "Выберите заказ")
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // связь N:1 с товаром
    @NotNull(message = "Выберите товар")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Укажите количество")
    @Min(value = 1, message = "Количество должно быть не меньше 1")
    @Column(nullable = false)
    private Integer quantity;

    // цена на момент покупки, чтобы история не менялась при смене цены товара
    @NotNull(message = "Укажите цену")
    @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной")
    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase = BigDecimal.ZERO;
}