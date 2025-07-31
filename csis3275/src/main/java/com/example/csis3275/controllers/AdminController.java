package com.example.csis3275.controllers;

import com.example.csis3275.entities.Experience;
import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.Order;
import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.ExperienceRepository;
import com.example.csis3275.repositories.OrderRepository;
import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.services.JwtService;
import com.example.csis3275.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/admin")
@Controller
@AllArgsConstructor
public class AdminController {

    @Autowired
    ExperienceInstanceRepository experienceInstanceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("")
    public String showAdminPage(HttpServletRequest request,
                                @RequestParam(value = "orderKey", required = false) String orderKey,
                                @RequestParam(value = "userKey", required = false) String userKey,
                                @RequestParam(value = "experienceKey", required = false) String experienceKey,
                                Model model) {
        if (!checkAdmin(request)) {
            model.addAttribute("errorMessage", "Sorry, you are not Admin");
            return "error";
        }

        List<Order> orders;
        List<User> users;
        List<ExperienceInstance> experienceInstances;

        if (orderKey != null && !orderKey.isBlank()) {
            orders = orderRepository.searchByUsername(orderKey);
        } else {
            orders = orderRepository.findAll();
        }

        if (userKey != null && !userKey.isBlank()) {
            users = userRepository.searchByUsername(userKey);
        } else {
            users = userRepository.findAll();
        }

        if (experienceKey != null && !experienceKey.isBlank()) {
            experienceInstances = experienceInstanceRepository.findByTitleContainingIgnoreCase(experienceKey);
        } else {
            experienceInstances = experienceInstanceRepository.findAll();
        }

        model.addAttribute("users", users);
        model.addAttribute("experienceInstances", experienceInstances);
        model.addAttribute("orders", orders);
        return "admin";
    }


    private boolean checkAdmin(HttpServletRequest request) {
        String token = "";
        for (Cookie cookie : request.getCookies()) {
            if (("user-token-admin").equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }

        if(token.isEmpty()) return false;

        try {
            return jwtService.extractUsername(token).equals("admin") ;
        } catch (Exception e) {
            return false;
        }
    }
}
