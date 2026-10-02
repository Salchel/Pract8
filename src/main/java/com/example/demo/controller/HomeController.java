package com.example.demo.controller;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.GetMapping;
@Controller public class HomeController {private final ProductService products;public HomeController(ProductService p){products=p;}@GetMapping("/") String home(Model m){m.addAttribute("products",products.findAll().stream().limit(6).toList());return "index";}@GetMapping("/access-denied")String denied(){return "access-denied";}@GetMapping("/login")String login(){return "auth/login";}}
