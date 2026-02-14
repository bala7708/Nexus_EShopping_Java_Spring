package com.ecommerce.config;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create Admin
        if (!userRepository.existsByEmail("admin@shop.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@shop.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setPhone("+1 555-0100");
            admin.setAddress("123 Admin St, Commerce City");
            userRepository.save(admin);
        }

        // Create sample user
        if (!userRepository.existsByEmail("user@shop.com")) {
            User user = new User();
            user.setFirstName("Jane");
            user.setLastName("Doe");
            user.setEmail("user@shop.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setPhone("+1 555-0200");
            user.setAddress("456 User Ave, Shoptown");
            userRepository.save(user);
        }

        // Create Sample Products
        if (productRepository.count() == 0) {
            String[][] products = {
                {"Wireless Noise-Cancelling Headphones", "Premium over-ear headphones with 30h battery and active noise cancellation for immersive sound.", "199.99", "Electronics", "25", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"},
                {"Mechanical Gaming Keyboard", "Full-size RGB keyboard with Cherry MX switches, N-key rollover, and tactile feedback.", "129.99", "Electronics", "40", "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400"},
                {"Minimalist Leather Watch", "Handcrafted genuine leather strap with sapphire crystal glass and Swiss movement.", "299.99", "Fashion", "15", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400"},
                {"Running Sneakers Pro", "Lightweight responsive cushioning for maximum performance. Breathable mesh upper.", "119.99", "Fashion", "60", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400"},
                {"Ceramic Pour-Over Coffee Set", "Hand-thrown ceramic dripper with matching carafe. For the coffee enthusiast.", "79.99", "Home & Kitchen", "30", "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400"},
                {"Yoga Mat Premium", "6mm thick non-slip eco-friendly yoga mat with alignment guides and carrying strap.", "59.99", "Sports", "50", "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400"},
                {"Stainless Steel Water Bottle", "32oz vacuum insulated bottle keeps drinks cold 24h, hot 12h. BPA-free.", "34.99", "Sports", "100", "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400"},
                {"Portable Bluetooth Speaker", "360° surround sound with 20h playtime, waterproof IPX7 rating. Perfect for outdoor.", "89.99", "Electronics", "35", "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400"},
                {"Linen Throw Blanket", "100% organic linen, pre-washed for ultra-soft texture. Ethically made.", "69.99", "Home & Kitchen", "45", "https://images.unsplash.com/photo-1580301762395-21ce84d00bc6?w=400"},
                {"Resistance Bands Set", "5-piece progressive resistance set with door anchor, handles, and ankle straps.", "29.99", "Sports", "80", "https://images.unsplash.com/photo-1598289431512-b97b0917afac?w=400"},
                {"Smart LED Desk Lamp", "Touch-controlled lamp with 5 color temperatures, USB-C charging port, and memory function.", "49.99", "Home & Kitchen", "55", "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400"},
                {"Leather Card Wallet", "Ultra-slim RFID-blocking wallet in full-grain leather. Holds 8 cards.", "45.99", "Fashion", "70", "https://images.unsplash.com/photo-1627123424574-724758594e93?w=400"}
            };

            for (String[] p : products) {
                Product product = new Product();
                product.setName(p[0]);
                product.setDescription(p[1]);
                product.setPrice(new BigDecimal(p[2]));
                product.setCategory(p[3]);
                product.setStockQuantity(Integer.parseInt(p[4]));
                product.setImageUrl(p[5]);
                productRepository.save(product);
            }
        }
    }
}
