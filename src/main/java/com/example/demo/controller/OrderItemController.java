package com.example.demo.controller;

import com.example.demo.model.OrderItem;
import com.example.demo.service.OrderItemService;
import com.example.demo.service.OrderService;
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
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;
    private final ProductService productService;

    public OrderItemController(OrderItemService orderItemService,
                               OrderService orderService,
                               ProductService productService) {
        this.orderItemService = orderItemService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", orderItemService.search(q));
        model.addAttribute("q", q);
        return "order-items/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("item", new OrderItem());
        addSelectLists(model);
        return "order-items/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        OrderItem item = orderItemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Позиция не найдена"));
        model.addAttribute("item", item);
        addSelectLists(model);
        return "order-items/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("item") OrderItem item,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        Long orderId = null;
        Long productId = null;

        if (!result.hasFieldErrors("order")) {
            orderId = item.getOrder() == null ? null : item.getOrder().getId();
            if (orderId == null || orderService.findById(orderId).isEmpty()) {
                result.rejectValue("order", "required", "Выберите заказ");
                orderId = null;
            }
        }
        if (!result.hasFieldErrors("product")) {
            productId = item.getProduct() == null ? null : item.getProduct().getId();
            if (productId == null || productService.findById(productId).isEmpty()) {
                result.rejectValue("product", "required", "Выберите товар");
                productId = null;
            }
        }

        // в БД пара (заказ, товар) уникальна: один товар = одна позиция в заказе
        if (orderId != null && productId != null) {
            orderItemService.findByOrderAndProduct(orderId, productId).ifPresent(existing -> {
                if (!existing.getId().equals(item.getId())) {
                    result.rejectValue("product", "duplicate",
                            "Этот товар уже есть в заказе, измените количество в существующей позиции");
                }
            });
        }

        if (result.hasErrors()) {
            addSelectLists(model);
            return "order-items/form";
        }

        boolean isNew = item.getId() == null;
        orderItemService.save(item);
        redirectAttributes.addFlashAttribute("success",
                isNew ? "Позиция добавлена" : "Изменения сохранены");
        return "redirect:/order-items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderItemService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Позиция удалена");
        return "redirect:/order-items";
    }

    private void addSelectLists(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("products", productService.findAll());
    }
}