package com.ecommerce.controller;

import com.ecommerce.model.*;
import com.ecommerce.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;
    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;
    @Autowired private UserService userService;

    // ── View Cart ──────────────────────────────────────────────────────
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("cartTotal", cartService.getTotal(session));
        model.addAttribute("cartCount", cartService.getItemCount(session));
        return "user/cart";
    }

    // ── Add to Cart (from shop or product detail) ──────────────────────
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(defaultValue = "/shop") String redirectTo,
                            HttpSession session,
                            RedirectAttributes ra) {
        productService.findById(productId).ifPresentOrElse(
            product -> {
                if (!product.isActive() || product.getStockQuantity() == 0) {
                    ra.addFlashAttribute("error", product.getName() + " is out of stock.");
                } else if (quantity < 1 || quantity > product.getStockQuantity()) {
                    ra.addFlashAttribute("error", "Invalid quantity.");
                } else {
                    cartService.addToCart(session, product, quantity);
                    ra.addFlashAttribute("cartAdded", product.getName());
                    ra.addFlashAttribute("cartCount", cartService.getItemCount(session));
                }
            },
            () -> ra.addFlashAttribute("error", "Product not found.")
        );
        return "redirect:" + redirectTo;
    }

    // ── Buy Now (add to cart then go straight to checkout) ────────────
    @PostMapping("/buy-now/{productId}")
    public String buyNow(@PathVariable Long productId,
                         @RequestParam(defaultValue = "1") int quantity,
                         HttpSession session,
                         RedirectAttributes ra) {
        productService.findById(productId).ifPresentOrElse(
            product -> {
                if (!product.isActive() || product.getStockQuantity() == 0) {
                    ra.addFlashAttribute("error", product.getName() + " is out of stock.");
                } else {
                    cartService.addToCart(session, product, quantity);
                }
            },
            () -> ra.addFlashAttribute("error", "Product not found.")
        );
        return "redirect:/cart/checkout";
    }

    // ── Remove Item ────────────────────────────────────────────────────
    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        cartService.removeFromCart(session, productId);
        return "redirect:/cart";
    }

    // ── Update Quantity ────────────────────────────────────────────────
    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        cartService.updateQuantity(session, productId, quantity);
        return "redirect:/cart";
    }

    // ── Clear Cart ─────────────────────────────────────────────────────
    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart";
    }

    // ── Checkout Page ──────────────────────────────────────────────────
    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session,
                               @AuthenticationPrincipal UserDetails userDetails) {
        if (cartService.getCart(session).isEmpty()) return "redirect:/cart";

        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("cartTotal", cartService.getTotal(session));
        model.addAttribute("cartCount", cartService.getItemCount(session));

        // Pre-fill address from profile
        if (userDetails != null) {
            userService.findByEmail(userDetails.getUsername()).ifPresent(u ->
                model.addAttribute("defaultAddress", u.getAddress() != null ? u.getAddress() : "")
            );
        }
        return "user/checkout";
    }

    // ── Place Order ────────────────────────────────────────────────────
    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String shippingAddress,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Please enter a shipping address.");
            return "redirect:/cart/checkout";
        }
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Order order = orderService.createOrder(user, cartService.getCart(session), shippingAddress.trim());
            cartService.clearCart(session);
            ra.addFlashAttribute("success", "🎉 Order #" + order.getId() + " placed! Tracking: " + order.getTrackingNumber());
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart/checkout";
        }
    }
}
