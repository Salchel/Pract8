package com.example.demo.service;

import com.example.demo.model.Manufacturer;
import com.example.demo.repository.ManufacturerRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ProductRepository productRepository;

    public ManufacturerService(ManufacturerRepository manufacturerRepository, ProductRepository productRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.productRepository = productRepository;
    }

    public List<Manufacturer> findAll() {
        return manufacturerRepository.findAll(Sort.by("id"));
    }

    public Optional<Manufacturer> findById(Long id) {
        return manufacturerRepository.findById(id);
    }

    public Optional<Manufacturer> findByName(String name) {
        return manufacturerRepository.findByName(name);
    }

    // Поиск (ПР4): по части названия и, если введено число, ещё и по id
    public List<Manufacturer> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim();
        List<Manufacturer> result = new ArrayList<>(manufacturerRepository.findByNameContainingIgnoreCase(q));
        if (q.matches("\\d{1,18}")) {
            manufacturerRepository.findById(Long.parseLong(q)).ifPresent(found -> {
                boolean alreadyIn = result.stream().anyMatch(m -> m.getId().equals(found.getId()));
                if (!alreadyIn) {
                    result.add(found);
                }
            });
        }
        return result;
    }

    public Manufacturer save(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    public void delete(Long id) {
        if (productRepository.existsByManufacturerId(id)) {
            throw new IllegalStateException("Нельзя удалить производителя: у него есть товары. Сначала удалите или переместите их.");
        }
        manufacturerRepository.deleteById(id);
    }
}