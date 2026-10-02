package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public OrderItemService(OrderItemRepository orderItemRepository,
                            OrderRepository orderRepository,
                            ProductRepository productRepository,
                            OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<OrderItem> findById(Long id) {
        return orderItemRepository.findById(id);
    }

    public Optional<OrderItem> findByOrderAndProduct(Long orderId, Long productId) {
        return orderItemRepository.findByOrderIdAndProductId(orderId, productId);
    }

    // Поиск (ПР4): число = id позиции или номер заказа, иначе по названию товара
    public List<OrderItem> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim();
        if (q.matches("\\d{1,18}")) {
            long number = Long.parseLong(q);
            return findAll().stream()
                    .filter(i -> i.getId() == number || i.getOrder().getId() == number)
                    .toList();
        }
        String lower = q.toLowerCase();
        return findAll().stream()
                .filter(i -> i.getProduct().getTitle().toLowerCase().contains(lower))
                .toList();
    }

    // Из формы приходят id заказа и товара (ПР5), количество и (при редактировании) id позиции.
    // Цену берём из товара сами: она фиксируется на момент покупки.
    @Transactional
    public OrderItem save(OrderItem form) {
        Order order = orderRepository.findById(form.getOrder().getId())
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
        Product product = productRepository.findById(form.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

        OrderItem item = form.getId() == null
                ? new OrderItem()
                : orderItemRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Позиция не найдена"));

        Long oldOrderId = item.getOrder() == null ? null : item.getOrder().getId();
        boolean productChanged = item.getProduct() == null
                || !item.getProduct().getId().equals(product.getId());

        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(form.getQuantity());
        if (productChanged) {
            item.setPriceAtPurchase(product.getPrice());
        }
        OrderItem saved = orderItemRepository.save(item);

        orderService.recalculateTotal(order.getId());
        if (oldOrderId != null && !oldOrderId.equals(order.getId())) {
            orderService.recalculateTotal(oldOrderId);   // позицию перенесли в другой заказ
        }
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        orderItemRepository.findById(id).ifPresent(item -> {
            Long orderId = item.getOrder().getId();
            orderItemRepository.delete(item);
            orderService.recalculateTotal(orderId);
        });
    }
}