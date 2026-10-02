package com.example.demo.dto;
import jakarta.validation.constraints.*;
public record RegisterRequest(
 @NotBlank @Size(min=3,max=50) String username,
 @NotBlank @Email @Size(max=100) String email,
 @NotBlank @Size(max=150) String fullName,
 @NotBlank @Size(min=8,max=72) String password,
 @NotBlank String passwordConfirmation,
 @Size(max=200) String address,
 @Pattern(regexp="^$|^\\+?[0-9]{10,15}$",message="Телефон: 10–15 цифр") String phone) {}
