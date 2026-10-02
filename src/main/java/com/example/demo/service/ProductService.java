package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.Manufacturer;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ManufacturerRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ManufacturerRepository manufacturerRepository,
                          OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll(Sort.by("id"));
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // Поиск (ПР4): по части названия и, если введено число, ещё и по id
    public List<Product> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim();
        List<Product> result = new ArrayList<>(productRepository.findByTitleContainingIgnoreCase(q));
        if (q.matches("\\d{1,18}")) {
            productRepository.findById(Long.parseLong(q)).ifPresent(found -> {
                boolean alreadyIn = result.stream().anyMatch(p -> p.getId().equals(found.getId()));
                if (!alreadyIn) {
                    result.add(found);
                }
            });
        }
        return result;
    }

    // Из формы приходит только id категории и производителя (ПР5),
    // поэтому подставляем настоящие записи из БД
    public Product save(Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
        Manufacturer manufacturer = manufacturerRepository.findById(product.getManufacturer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Производитель не найден"));
        product.setCategory(category);
        product.setManufacturer(manufacturer);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        if (orderItemRepository.existsByProductId(id)) {
            throw new IllegalStateException("Нельзя удалить товар: он есть в заказах.");
        }
        productRepository.deleteById(id);
    }
}