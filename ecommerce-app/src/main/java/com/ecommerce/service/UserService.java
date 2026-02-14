package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new RuntimeException("Email already registered: " + user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }

    public User save(User user) { return userRepository.save(user); }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }
    public Optional<User> findById(Long id) { return userRepository.findById(id); }
    public List<User> findAll() { return userRepository.findAll(); }

    public void deleteById(Long id) { userRepository.deleteById(id); }

    public void toggleActive(Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setActive(!u.isActive());
            userRepository.save(u);
        });
    }

    public long countUsers() { return userRepository.countByRole(User.Role.USER); }

    public User updateUser(Long id, User updated) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(updated.getFirstName());
            user.setLastName(updated.getLastName());
            user.setPhone(updated.getPhone());
            user.setAddress(updated.getAddress());
            if (updated.getPassword() != null && !updated.getPassword().isEmpty())
                user.setPassword(passwordEncoder.encode(updated.getPassword()));
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
