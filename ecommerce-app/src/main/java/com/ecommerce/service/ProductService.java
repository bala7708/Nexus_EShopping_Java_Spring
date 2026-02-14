package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired private ProductRepository productRepository;

    public Product save(Product p) { return productRepository.save(p); }
    public Optional<Product> findById(Long id) { return productRepository.findById(id); }
    public List<Product> findAll() { return productRepository.findAll(); }
    public List<Product> findAllActive() { return productRepository.findByActiveTrue(); }
    public List<Product> findByCategory(String cat) { return productRepository.findByCategoryAndActiveTrue(cat); }
    public List<Product> searchProducts(String kw) { return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(kw); }
    public List<String> getAllCategories() { return productRepository.findDistinctCategories(); }
    public void deleteById(Long id) { productRepository.deleteById(id); }
    public long countActive() { return productRepository.countByActiveTrue(); }

    public void toggleActive(Long id) {
        productRepository.findById(id).ifPresent(p -> {
            p.setActive(!p.isActive());
            productRepository.save(p);
        });
    }

    public Product update(Long id, Product upd) {
        return productRepository.findById(id).map(p -> {
            p.setName(upd.getName());
            p.setDescription(upd.getDescription());
            p.setPrice(upd.getPrice());
            p.setStockQuantity(upd.getStockQuantity());
            p.setCategory(upd.getCategory());
            p.setImageUrl(upd.getImageUrl());
            return productRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
