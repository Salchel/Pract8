package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Пока минимальный: нужен для выпадающих списков в формах профилей и заказов.
// Регистрация, шифрование паролей и смена ролей добавятся на шаге Spring Security.
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll(Sort.by("username"));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}