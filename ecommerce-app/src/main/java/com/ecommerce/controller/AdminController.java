package com.ecommerce.controller;

import com.ecommerce.model.*;
import com.ecommerce.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ProductService productService;
    @Autowired private UserService userService;
    @Autowired private OrderService orderService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.countActive());
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalOrders", orderService.countAll());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        model.addAttribute("pendingOrders", orderService.countByStatus(Order.OrderStatus.PENDING));
        model.addAttribute("recentOrders", orderService.findAll().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ── Products ──────────────────────────────────────────────────────
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes ra) {
        productService.save(product);
        ra.addFlashAttribute("success", "Product \"" + product.getName() + "\" created!");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        return productService.findById(id).map(p -> {
            model.addAttribute("product", p);
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product-form";
        }).orElse("redirect:/admin/products");
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product p, RedirectAttributes ra) {
        productService.update(id, p);
        ra.addFlashAttribute("success", "Product updated successfully.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.deleteById(id);
        ra.addFlashAttribute("success", "Product deleted.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/toggle/{id}")
    public String toggleProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.toggleActive(id);
        ra.addFlashAttribute("success", "Product visibility updated.");
        return "redirect:/admin/products";
    }

    // ── Users ─────────────────────────────────────────────────────────
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes ra) {
        try {
            userService.register(user);
            ra.addFlashAttribute("success", "User " + user.getEmail() + " created.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        return userService.findById(id).map(u -> {
            model.addAttribute("user", u);
            return "admin/user-form";
        }).orElse("redirect:/admin/users");
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes ra) {
        userService.updateUser(id, user);
        ra.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteById(id);
        ra.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleActive(id);
        ra.addFlashAttribute("success", "User status updated.");
        return "redirect:/admin/users";
    }

    // ── Orders ────────────────────────────────────────────────────────
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        return orderService.findById(id).map(order -> {
            model.addAttribute("order", order);
            model.addAttribute("statuses", Order.OrderStatus.values());
            return "admin/order-detail";
        }).orElse("redirect:/admin/orders");
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam Order.OrderStatus status,
                                    RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Order status updated to " + status);
        return "redirect:/admin/orders/" + id;
    }
}
