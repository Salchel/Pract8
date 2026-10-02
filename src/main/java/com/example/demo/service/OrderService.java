package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.User;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    // Поиск (ПР4): число = по id заказа, иначе по логину пользователя
    public List<Order> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim();
        if (q.matches("\\d{1,18}")) {
            return orderRepository.findById(Long.parseLong(q)).map(List::of).orElse(List.of());
        }
        String lower = q.toLowerCase();
        return findAll().stream()
                .filter(o -> o.getUser().getUsername().toLowerCase().contains(lower))
                .toList();
    }

    // Из формы берём только пользователя и статус. Остальное (позиции, сумму)
    // не трогаем, иначе пустой список позиций из формы удалил бы их из заказа.
    @Transactional
    public Order save(Order form) {
        User user = userRepository.findById(form.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Order order = form.getId() == null
                ? new Order()
                : orderRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));

        order.setUser(user);
        order.setStatus(form.getStatus());
        Order saved = orderRepository.save(order);
        recalculateTotal(saved.getId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);   // позиции удалятся каскадом
    }

    // Сумма заказа = сумма (количество * цена на момент покупки) по всем позициям
    @Transactional
    public void recalculateTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItemRepository.findByOrderId(orderId)) {
            total = total.add(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}