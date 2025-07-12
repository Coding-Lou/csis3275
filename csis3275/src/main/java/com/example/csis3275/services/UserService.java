package com.example.csis3275.services;

import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean checkUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User save(User user) {
        return userRepository.save(user); // fixed: now returns the saved User
    }

    public void deleteByUsername(String username) {
        if (checkUserExists(username)) {
            userRepository.deleteByUsername(username);
        }
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username); // fixed return type
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
