package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private CartService cartService;

    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails,
                           Model model, HttpSession session) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("orders", orderService.findOrdersByUser(user));
        model.addAttribute("cartCount", cartService.getItemCount(session));
        return "user/orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model, HttpSession session) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        return orderService.findById(id).map(order -> {
            boolean isOwner  = order.getUser().getId().equals(user.getId());
            boolean isAdmin  = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isOwner && !isAdmin) return "redirect:/orders";
            model.addAttribute("order", order);
            model.addAttribute("cartCount", cartService.getItemCount(session));
            return "user/order-detail";
        }).orElse("redirect:/orders");
    }
}
