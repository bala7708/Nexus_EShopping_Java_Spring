package com.ecommerce.controller;

import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ShopController {

    @Autowired private ProductService productService;
    @Autowired private CartService cartService;

    @GetMapping({"/", "/shop"})
    public String shop(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String search,
                       Model model, HttpSession session) {

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("products", productService.searchProducts(search.trim()));
            model.addAttribute("searchQuery", search.trim());
        } else if (category != null && !category.isEmpty()) {
            model.addAttribute("products", productService.findByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("products", productService.findAllActive());
        }

        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("cartCount", cartService.getItemCount(session));
        return "user/shop";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        return productService.findById(id).map(product -> {
            model.addAttribute("product", product);
            model.addAttribute("cartCount", cartService.getItemCount(session));
            // Related products (same category, exclude current)
            model.addAttribute("related", productService.findByCategory(product.getCategory())
                    .stream().filter(p -> !p.getId().equals(id)).limit(4).toList());
            return "user/product-detail";
        }).orElse("redirect:/shop");
    }
}
