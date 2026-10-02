package com.example.demo.repository;

import com.example.demo.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    Optional<Manufacturer> findByName(String name);

    boolean existsByName(String name);

    // поиск по названию (ПР4): часть названия, без учёта регистра
    List<Manufacturer> findByNameContainingIgnoreCase(String name);
}