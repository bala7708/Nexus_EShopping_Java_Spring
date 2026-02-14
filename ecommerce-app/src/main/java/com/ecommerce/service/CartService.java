package com.ecommerce.service;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final String CART_KEY = "CART";

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, Product product, int quantity) {
        List<CartItem> cart = getCart(session);
        Optional<CartItem> existing = cart.stream()
                .filter(i -> i.getProductId().equals(product.getId()))
                .findFirst();

        if (existing.isPresent()) {
            // Cap at stock quantity
            int newQty = Math.min(existing.get().getQuantity() + quantity, product.getStockQuantity());
            existing.get().setQuantity(newQty);
        } else {
            cart.add(new CartItem(
                    product.getId(), product.getName(),
                    product.getPrice(), quantity, product.getImageUrl()
            ));
        }
        session.setAttribute(CART_KEY, cart);
    }

    public void removeFromCart(HttpSession session, Long productId) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(i -> i.getProductId().equals(productId));
        session.setAttribute(CART_KEY, cart);
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        List<CartItem> cart = getCart(session);
        if (quantity <= 0) {
            cart.removeIf(i -> i.getProductId().equals(productId));
        } else {
            cart.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(quantity));
        }
        session.setAttribute(CART_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }

    public BigDecimal getTotal(HttpSession session) {
        return getCart(session).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount(HttpSession session) {
        return getCart(session).stream().mapToInt(CartItem::getQuantity).sum();
    }

    public int getLineCount(HttpSession session) {
        return getCart(session).size();
    }
}
