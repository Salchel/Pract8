package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final ProductRepository products;
    public CategoryService(CategoryRepository repository, ProductRepository products) { this.repository=repository; this.products=products; }
    public List<Category> findAll(){ return repository.findAll(Sort.by("id")); }
    public Optional<Category> findById(Long id){ return repository.findById(id); }
    public Optional<Category> findByName(String name){ return repository.findByName(name); }
    public List<Category> search(String query){
        if(query==null||query.isBlank()) return findAll();
        String q=query.trim(); List<Category> result=new ArrayList<>(repository.findByNameContainingIgnoreCase(q));
        if(q.matches("\\d{1,18}")) repository.findById(Long.parseLong(q)).ifPresent(c->{if(result.stream().noneMatch(x->x.getId().equals(c.getId()))) result.add(c);});
        return result;
    }
    public Category save(Category value){ return repository.save(value); }
    public void delete(Long id){ if(products.existsByCategoryId(id)) throw new IllegalStateException("Нельзя удалить категорию: в ней есть товары."); repository.deleteById(id); }
}
