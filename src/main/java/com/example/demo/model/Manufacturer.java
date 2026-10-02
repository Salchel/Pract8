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
@Table(name = "manufacturers")
@Getter
@Setter
@NoArgsConstructor
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название производителя обязательно")
    @Size(max = 100, message = "Название не длиннее 100 символов")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 100, message = "Страна не длиннее 100 символов")
    @Column(length = 100)
    private String country;

    // связь 1:N (один производитель - много товаров)
    @OneToMany(mappedBy = "manufacturer")
    private List<Product> products = new ArrayList<>();
}