package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // здесь хранится BCrypt-хэш (60 символов), поэтому длину пароля
    // (@Size) проверяем не тут, а в форме регистрации / DTO
    @com.fasterxml.jackson.annotation.JsonIgnore
    @NotBlank(message = "Пароль обязателен")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    @Size(max = 100, message = "Email не длиннее 100 символов")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "ФИО обязательно")
    @Size(max = 150, message = "ФИО не длиннее 150 символов")
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    // связь N:1 (много пользователей - одна роль)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // связь 1:1 (обратная сторона, внешний ключ лежит в user_profiles)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;
}