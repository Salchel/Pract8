package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 200, message = "Адрес не длиннее 200 символов")
    @Column(length = 200)
    private String address;

    // необязательное поле: пусто или от 10 до 15 цифр, можно с "+"
    @Pattern(regexp = "^$|^\\+?[0-9]{10,15}$", message = "Телефон: от 10 до 15 цифр, можно с + в начале")
    @Column(length = 20)
    private String phone;

    @NotNull(message = "Укажите бонусные баллы")
    @Min(value = 0, message = "Бонусы не могут быть отрицательными")
    @Column(name = "bonus_points", nullable = false)
    private Integer bonusPoints = 0;

    // связь 1:1 (владелец связи, здесь лежит внешний ключ user_id)
    @NotNull(message = "Выберите пользователя")
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}