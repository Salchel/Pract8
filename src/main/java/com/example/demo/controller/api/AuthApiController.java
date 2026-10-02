package com.example.demo.controller.api;

import com.example.demo.dto.*;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Авторизация", description = "Регистрация и получение JWT для Swagger и API")
public class AuthApiController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService users;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService, UserService users) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.users = users;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Зарегистрировать покупателя")
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest request) {
        var user = users.register(request);
        return Map.of("id", user.getId(), "username", user.getUsername());
    }

    @PostMapping("/login")
    @Operation(summary = "Войти и получить JWT")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return new TokenResponse(jwtService.generate(authentication), "Bearer", jwtService.getExpiration());
    }
}
