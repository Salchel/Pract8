package com.example.demo.controller;

import com.example.demo.model.Manufacturer;
import com.example.demo.service.ManufacturerService;
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
@RequestMapping("/manufacturers")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("manufacturers", manufacturerService.search(q));
        model.addAttribute("q", q);
        return "manufacturers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("manufacturer", new Manufacturer());
        return "manufacturers/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Manufacturer manufacturer = manufacturerService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Производитель не найден"));
        model.addAttribute("manufacturer", manufacturer);
        return "manufacturers/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("manufacturer") Manufacturer manufacturer,
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {

        if (manufacturer.getName() != null) {
            manufacturerService.findByName(manufacturer.getName()).ifPresent(existing -> {
                if (!existing.getId().equals(manufacturer.getId())) {
                    result.rejectValue("name", "duplicate", "Производитель с таким названием уже существует");
                }
            });
        }

        if (result.hasErrors()) {
            return "manufacturers/form";
        }

        boolean isNew = manufacturer.getId() == null;
        manufacturerService.save(manufacturer);
        redirectAttributes.addFlashAttribute("success",
                isNew ? "Производитель добавлен" : "Изменения сохранены");
        return "redirect:/manufacturers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            manufacturerService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Производитель удалён");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manufacturers";
    }
}