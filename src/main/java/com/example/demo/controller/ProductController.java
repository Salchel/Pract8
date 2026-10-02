package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ManufacturerService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
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
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ManufacturerService manufacturerService;

    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             ManufacturerService manufacturerService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.manufacturerService = manufacturerService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("products", productService.search(q));
        model.addAttribute("q", q);
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        addSelectLists(model);
        return "products/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден"));
        model.addAttribute("product", product);
        addSelectLists(model);
        return "products/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("product") Product product,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        // Внешние ключи (ПР5): пользователь выбирает запись из списка,
        // проверяем, что выбор сделан и запись существует
        if (!result.hasFieldErrors("category")) {
            Long categoryId = product.getCategory() == null ? null : product.getCategory().getId();
            if (categoryId == null || categoryService.findById(categoryId).isEmpty()) {
                result.rejectValue("category", "required", "Выберите категорию");
            }
        }
        if (!result.hasFieldErrors("manufacturer")) {
            Long manufacturerId = product.getManufacturer() == null ? null : product.getManufacturer().getId();
            if (manufacturerId == null || manufacturerService.findById(manufacturerId).isEmpty()) {
                result.rejectValue("manufacturer", "required", "Выберите производителя");
            }
        }

        if (result.hasErrors()) {
            addSelectLists(model);
            return "products/form";
        }

        boolean isNew = product.getId() == null;
        productService.save(product);
        redirectAttributes.addFlashAttribute("success",
                isNew ? "Товар добавлен" : "Изменения сохранены");
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Товар удалён");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    // Списки для выпадающих полей формы
    private void addSelectLists(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("manufacturers", manufacturerService.findAll());
    }
}