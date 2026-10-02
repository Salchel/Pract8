package com.example.demo.controller;
import com.example.demo.model.Category;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/categories")
public class CategoryController {
 private final CategoryService service;
 public CategoryController(CategoryService service){this.service=service;}
 @GetMapping public String list(@RequestParam(required=false) String q, Model m){m.addAttribute("categories",service.search(q));m.addAttribute("q",q);return "categories/list";}
 @GetMapping("/new") public String create(Model m){m.addAttribute("category",new Category());return "categories/form";}
 @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){m.addAttribute("category",service.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)));return "categories/form";}
 @PostMapping public String save(@Valid @ModelAttribute Category category, BindingResult result, RedirectAttributes ra){
  if(category.getName()!=null) service.findByName(category.getName()).ifPresent(x->{if(!x.getId().equals(category.getId()))result.rejectValue("name","duplicate","Категория уже существует");});
  if(result.hasErrors())return "categories/form"; boolean fresh=category.getId()==null;service.save(category);ra.addFlashAttribute("success",fresh?"Категория добавлена":"Изменения сохранены");return "redirect:/categories";
 }
 @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes ra){try{service.delete(id);ra.addFlashAttribute("success","Категория удалена");}catch(IllegalStateException e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/categories";}
}
