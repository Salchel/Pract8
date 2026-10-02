package com.example.demo.controller;

import com.example.demo.model.UserProfile;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profiles")
public class UserProfileController {

    private final UserProfileService profileService;
    private final UserService userService;

    public UserProfileController(UserProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("profiles", profileService.search(q));
        model.addAttribute("q", q);
        return "profiles/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("profile", new UserProfile());
        model.addAttribute("users", userService.findAll());
        return "profiles/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        UserProfile profile = profileService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Профиль не найден"));
        model.addAttribute("profile", profile);
        model.addAttribute("users", userService.findAll());
        return "profiles/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("profile") UserProfile profile,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        if (!result.hasFieldErrors("user")) {
            Long userId = profile.getUser() == null ? null : profile.getUser().getId();
            if (userId == null || userService.findById(userId).isEmpty()) {
                result.rejectValue("user", "required", "Выберите пользователя");
            } else {
                // связь 1:1: у одного пользователя может быть только один профиль
                profileService.findByUserId(userId).ifPresent(existing -> {
                    if (!existing.getId().equals(profile.getId())) {
                        result.rejectValue("user", "duplicate", "У этого пользователя уже есть профиль");
                    }
                });
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "profiles/form";
        }

        boolean isNew = profile.getId() == null;
        profileService.save(profile);
        redirectAttributes.addFlashAttribute("success",
                isNew ? "Профиль добавлен" : "Изменения сохранены");
        return "redirect:/profiles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        profileService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Профиль удалён");
        return "redirect:/profiles";
    }
}