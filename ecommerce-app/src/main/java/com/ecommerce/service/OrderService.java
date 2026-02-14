package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;

    public Order createOrder(User user, List<CartItem> cartItems, String shippingAddress) {
        if (cartItems == null || cartItems.isEmpty())
            throw new RuntimeException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setTrackingNumber(generateTrackingNumber());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

            if (product.getStockQuantity() < cartItem.getQuantity())
                throw new RuntimeException("Insufficient stock for: " + product.getName()
                        + " (available: " + product.getStockQuantity() + ")");

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(product.getPrice());
            orderItems.add(item);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            total = total.add(item.getSubtotal());
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    public List<Order> findOrdersByUser(User user) { return orderRepository.findByUserOrderByCreatedAtDesc(user); }
    public Optional<Order> findById(Long id) { return orderRepository.findById(id); }
    public List<Order> findAll() { return orderRepository.findAllByOrderByCreatedAtDesc(); }

    public Order updateStatus(Long id, Order.OrderStatus status) {
        return orderRepository.findById(id).map(o -> {
            o.setStatus(status);
            return orderRepository.save(o);
        }).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal rev = orderRepository.getTotalRevenue();
        return rev != null ? rev : BigDecimal.ZERO;
    }

    public long countByStatus(Order.OrderStatus status) { return orderRepository.countByStatus(status); }
    public long countAll() { return orderRepository.count(); }

    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis() % 1000000 + "-" + (int)(Math.random() * 9000 + 1000);
    }
}
