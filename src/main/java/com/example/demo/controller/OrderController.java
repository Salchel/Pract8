package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("orders", orderService.search(q));
        model.addAttribute("q", q);
        return "orders/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("order", new Order());
        addSelectLists(model);
        return "orders/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));
        model.addAttribute("order", order);
        addSelectLists(model);
        return "orders/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("order") Order order,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        if (!result.hasFieldErrors("user")) {
            Long userId = order.getUser() == null ? null : order.getUser().getId();
            if (userId == null || userService.findById(userId).isEmpty()) {
                result.rejectValue("user", "required", "Выберите пользователя");
            }
        }

        if (result.hasErrors()) {
            addSelectLists(model);
            return "orders/form";
        }

        boolean isNew = order.getId() == null;
        orderService.save(order);
        redirectAttributes.addFlashAttribute("success",
                isNew ? "Заказ создан" : "Изменения сохранены");
        return "redirect:/orders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Заказ удалён");
        return "redirect:/orders";
    }

    private void addSelectLists(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("statuses", OrderStatus.values());
    }
}