package com.example.csis3275.controllers;

import com.example.csis3275.entities.*;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.ExperienceRepository;
import com.example.csis3275.repositories.OrderRepository;
import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("/orders")
@Controller
@AllArgsConstructor
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExperienceInstanceRepository experienceInstanceRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/{username}")
    public String getOrders(HttpServletRequest request, @PathVariable String username, Model model) {
        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<User> userOptional  = userRepository.findByUsername(username);
        if (userOptional .isEmpty()) {
            model.addAttribute("errorMessage", "User not found");
            return "error";
        }
        User user = userOptional.get();
        List<Order> orders = orderRepository.findOrdersByUserId(user.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("username", username);
        return "redirect:/user/traveler/" + username;
    }

    @PostMapping("/{username}/cancel")
    public String cancelOrder(HttpServletRequest request,  Model model, @PathVariable String username, @RequestParam Long orderId) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            model.addAttribute("errorMessage", "Order not found");
            return "error";
        }

        Order order = optionalOrder.get();
        User user =  getCurrentUser(request,username);
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            model.addAttribute("errorMessage", "Order is already cancelled.");
            return "redirect:/user/traveler/" + user.getUsername();
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(String.valueOf(LocalDateTime.now()));
        orderRepository.save(order);

        Long experienceInstanceId = order.getExperienceInstance().getId();
        if (experienceInstanceId != null) {
            experienceInstanceRepository.updateQuantity(experienceInstanceId, order.getQuantity());
        }

        model.addAttribute("errorMessage", "Order cancelled successfully.");
        return "redirect:/user/traveler/" + order.getUser().getUsername();
    }

    @GetMapping("/{username}/details")
    public String details(HttpServletRequest request, @PathVariable String username, @RequestParam("orderId") Long orderId, Model model) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            model.addAttribute("errorMessage", "Order not found");
            return "error";
        }

        Order order = optionalOrder.get();

        model.addAttribute("user", getCurrentUser(request,username));
        model.addAttribute("experienceInstance", order.getExperienceInstance());
        model.addAttribute("order", order);

        return "order-details";
    }

    @PostMapping("/{username}/pay")
    public String pay(HttpServletRequest request, @PathVariable String username, @RequestParam Long orderId, @RequestParam String paymentType, Model model) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            model.addAttribute("errorMessage", "Order not found");
            return "error";
        }

        Order order = optionalOrder.get();

        if ("PAID".equals(order.getOrderStatus().toString())) {
            return "redirect:/user/traveler/" + username;
        }

        orderRepository.updateOrderStatus(orderId , paymentType);
        return "redirect:/user/traveler/" + username;
    }

    private boolean checkValidToken(HttpServletRequest request, String username) {
        String token = "";
        for (Cookie cookie : request.getCookies()) {
            if (("user-token-" + username).equals(cookie.getName())) {
                token = cookie.getValue();
            }
            if (cookie.getName().equals("user-role-admin"))
                return true;
        }

        if(token.isEmpty()) return false;

        try {
            User user = userRepository.getUserByUsername(username);
            if (user.isAdmin()) return true;
            jwtService.extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private User getCurrentUser(HttpServletRequest request, String username) {
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