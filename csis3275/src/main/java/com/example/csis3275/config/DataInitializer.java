package com.example.csis3275.config;

import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        if (userRepository.count() == 0) {
            createInitialUsers();
        }
    }

    private void createInitialUsers() {
        User user1 = new User();
        user1.setUsername("sora");
        user1.setPasswordHash(passwordEncoder.encode("sora123"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("kairi");
        user2.setPasswordHash(passwordEncoder.encode("kairi456"));
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("riku");
        user3.setPasswordHash(passwordEncoder.encode("riku789"));
        userRepository.save(user3);
    }
}
