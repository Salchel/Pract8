package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // поиск по названию (ПР4): часть названия, без учёта регистра
    List<Product> findByTitleContainingIgnoreCase(String title);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByManufacturerId(Long manufacturerId);

    // нужны, чтобы показать понятную ошибку вместо падения на RESTRICT при удалении
    boolean existsByCategoryId(Long categoryId);

    boolean existsByManufacturerId(Long manufacturerId);
}