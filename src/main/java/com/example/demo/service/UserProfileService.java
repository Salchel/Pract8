package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;

    public UserProfileService(UserProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public List<UserProfile> findAll() {
        return profileRepository.findAll(Sort.by("id"));
    }

    public Optional<UserProfile> findById(Long id) {
        return profileRepository.findById(id);
    }

    public Optional<UserProfile> findByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    // Поиск (ПР4): число = по id, иначе по логину, телефону или адресу
    public List<UserProfile> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim();
        if (q.matches("\\d{1,18}")) {
            return profileRepository.findById(Long.parseLong(q)).map(List::of).orElse(List.of());
        }
        String lower = q.toLowerCase();
        return findAll().stream()
                .filter(p -> contains(p.getUser().getUsername(), lower)
                        || contains(p.getPhone(), lower)
                        || contains(p.getAddress(), lower))
                .toList();
    }

    // Из формы приходит только id пользователя (ПР5), подставляем настоящего
    public UserProfile save(UserProfile profile) {
        User user = userRepository.findById(profile.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        profile.setUser(user);
        return profileRepository.save(profile);
    }

    public void delete(Long id) {
        profileRepository.deleteById(id);
    }

    private static boolean contains(String value, String lowerQuery) {
        return value != null && value.toLowerCase().contains(lowerQuery);
    }
}