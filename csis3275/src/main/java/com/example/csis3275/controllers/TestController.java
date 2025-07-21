package com.example.csis3275.controllers;

import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.services.JwtService;
import com.example.csis3275.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class TestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;
}
