package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название категории обязательно")
    @Size(max = 100, message = "Название не длиннее 100 символов")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 1000, message = "Описание не длиннее 1000 символов")
    @Column(columnDefinition = "TEXT")
    private String description;

    // связь 1:N (одна категория - много товаров)
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}