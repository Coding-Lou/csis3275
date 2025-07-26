package com.example.csis3275.controllers;

import com.example.csis3275.entities.Order;
import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.OrderRepository;
import com.example.csis3275.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/order")
@Controller
@AllArgsConstructor
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExperienceInstanceRepository experienceInstanceRepository;

    @GetMapping(path =  {"/" })
    public String getOrders(Model model,@RequestParam(name = "keyword",defaultValue = "") String keyword) {

        List<Order> orders;
        if (keyword.isEmpty()) {
            orders = orderRepository.findAll();
        } else {
            try {
                Long key = Long.parseLong(keyword);
                orders = orderRepository.findOrdersByOrderId(key);
            } catch (NumberFormatException e) {
                orders = orderRepository.findAll();
                model.addAttribute("searchError", "Invalid keyword. Please enter a valid Order ID.");
            }
        }
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/delete")
    public String delete(Long id) {
        orderRepository.deleteById(id);
        return "redirect:/order";
    }

    @PostMapping(path="/create")
    public String create(
            @ModelAttribute("bookOrder") Order order,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (order.getOrderId() == null) {
            redirectAttributes.addFlashAttribute("message", "Unable to create new book order.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/order";
        }

        if (order.getUser() == null) {
            order.setUser(new User());}
        if (order.getExperienceInstance() == null) {
            order.setExperienceInstance(new ExperienceInstance());
        }

        if (bindingResult.hasErrors()) {
            return "error";
        }

        Order existingOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        if (existingOrder == null) {
            redirectAttributes.addFlashAttribute("message", "record not found");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/order";
        }
        order.setCreatedAt(existingOrder.getCreatedAt());

        User userToSet = null;
        if (order.getUser().getId() != null) {
            userToSet = userRepository.findById(order.getUser().getId()).orElse(null);
        } else {
            bindingResult.rejectValue("user.id", "error.bookOrder","User id not found");
        }
        if (userToSet == null && order.getUser().getId() != null) {
            bindingResult.rejectValue("user.id", "error.bookOrder","User id invalid");
        }

        ExperienceInstance experienceInstanceToSet = null;
        if (order.getExperienceInstance().getId() != null) {
            experienceInstanceToSet = experienceInstanceRepository.findById(order.getExperienceInstance().getId()).orElse(null);
        } else {
            bindingResult.rejectValue("experienceInstance.id", "error.bookOrder","ExperienceInstance id not found");
        }
        if (experienceInstanceToSet == null && order.getExperienceInstance().getId() != null) { // 如果ID不为空但未找到体验实例
            bindingResult.rejectValue("experienceInstance.id", "error.bookOrder", "invalid experienceInstance id。");
        }

        if (bindingResult.hasErrors()) {
            if (order.getUser().getId() != null) {
                order.setUser(new User());}
            if (order.getExperienceInstance() == null) {
                order.setExperienceInstance(new ExperienceInstance());}
            return "order-update";
        }

        order.setUser(userToSet);
        order.setExperienceInstance(experienceInstanceToSet);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("message","Updated Book Order successfully.");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/order";

    }

    @GetMapping("/update")
    public String update(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes){
        Order order = orderRepository.findById(id).orElse(null);
        if(order ==null) {
            redirectAttributes.addFlashAttribute("message", "Book Order with id "+id+" not found");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/order";
        }
        model.addAttribute("bookOrder", order);
        return "order-update";
    }

}