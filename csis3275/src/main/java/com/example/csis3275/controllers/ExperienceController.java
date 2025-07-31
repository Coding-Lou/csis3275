package com.example.csis3275.controllers;

import com.example.csis3275.entities.*;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.ExperienceRepository;
import com.example.csis3275.repositories.OrderRepository;
import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/experience")
public class ExperienceController {

    @Autowired
    ExperienceRepository experienceRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExperienceInstanceRepository experienceInstanceRepository;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/search")
    public String experienceSearchPost(Model model, @RequestParam String search) {
        List<Experience> experiences = experienceRepository.findByTitleContainingIgnoreCase(search);
        model.addAttribute("experiences", experiences);
        return "experience-search";
    }

    @GetMapping("/detail/{id}")
    public String experienceDetailGet(HttpServletRequest request, @PathVariable int id, Model model) {
        Optional<Experience> experience = experienceRepository.findById((long) id);
        User user = getCurrentUser(request);
        if (user == null) {
            user = new User();
        }
        if (experience.isPresent()) {
            model.addAttribute("experience", experience.get());
            model.addAttribute("user", user);
            return "experience-detail";
        } else {
            return "experience-search";
        }
    }

    @PostMapping("/book")
    public String book(HttpServletRequest request, Model model, @RequestParam Long instanceId, int quantity) {
        String token = "";
        String username = "";
        try {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().startsWith("user-token")) {
                    token = cookie.getValue();
                }
            }
            username = jwtService.extractUsername(token);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "User not logged in");
            return "login";
        }

        User user = userRepository.findByUsername(username).get();

        Optional<ExperienceInstance> optionalExperienceInstance = experienceInstanceRepository.findById(instanceId);
        if (optionalExperienceInstance.isEmpty()) {
            model.addAttribute("errorMessage", "Experience Instance '" + instanceId + "' not found");
            return "error";
        }

        ExperienceInstance experienceInstance = optionalExperienceInstance.get();
        if (quantity <= 0 || quantity > experienceInstance.getAvailableSlots()) {
            model.addAttribute("errorMessage", "Request quantity exceeded '");
            return "error";
        }

        experienceInstanceRepository.updateQuantity(instanceId, -quantity);

        Order order = new Order();
        order.setUser(user);
        order.setExperienceInstance(experienceInstance);
        order.setExperienceId(instanceId);
        order.setQuantity(quantity);
        order.setTransactionValue(experienceInstance.getPrice() * quantity);
        order.setOrderStatus(OrderStatus.BOOKED);
        order.setCreatedAt(LocalDateTime.now().toString());
        order.setUpdatedAt(LocalDateTime.now().toString());
        orderRepository.save(order);

        return "redirect:/user/traveler/" + username;
    }

    private User getCurrentUser(HttpServletRequest request) {
        String token = "";
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().startsWith("user-token-")) {
                token = cookie.getValue();
            }
        }
        try {
            return userRepository.getUserByUsername(jwtService.extractUsername(token));
        } catch (Exception e) {
            return null;
        }

    }
}
