package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название товара обязательно")
    @Size(max = 150, message = "Название не длиннее 150 символов")
    @Column(nullable = false, length = 150)
    private String title;

    @Size(max = 2000, message = "Описание не длиннее 2000 символов")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Укажите цену")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Digits(integer = 8, fraction = 2, message = "Цена: до 8 цифр до запятой и 2 после")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Укажите количество на складе")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "requires_prescription", nullable = false)
    private boolean requiresPrescription;

    @Size(max = 500, message = "Ссылка на картинку не длиннее 500 символов")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // связь N:1 (много товаров - одна категория)
    @NotNull(message = "Выберите категорию")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // связь N:1 (много товаров - один производитель)
    @NotNull(message = "Выберите производителя")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;
}