package com.example.csis3275.controllers;


import com.example.csis3275.entities.Experience;
import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.Order;
import com.example.csis3275.entities.User;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/user/guide")
@Controller
@AllArgsConstructor
public class GuideController {
    @Autowired
    ExperienceRepository experienceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;
    @GetMapping("/{username}")
    public String getGuideHome(HttpServletRequest request, @PathVariable String username, Model model) {
        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User with username '" + username + "' was not found.");
            return "error";
        }

        User user = userOptional.get();

        model.addAttribute("user", user);

        List<Order> orders = orderRepository.findOrdersByUserId(user.getId());

        model.addAttribute("orders", orders);

        List<Experience> experiences = experienceRepository.findAll();
        List<Experience> myExperiences = experienceRepository.findByUserId(user.getId());

        model.addAttribute("experiences", experiences);
        model.addAttribute("myExperiences", myExperiences);
        return "guide-home";
    }

    @GetMapping("/{username}/create-experience")
    public String GetCreateExperience(HttpServletRequest request, @PathVariable String username, Model model) {
        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User with username '" + username + "' was not found.");
            return "error";
        }

        User user = userOptional.get();
        Experience experience = new Experience();


        model.addAttribute("experience", experience);
        model.addAttribute("user", user);
        return "create-experience";
    }
    @PostMapping("/{username}/create-experience")
    public String PostCreateExperience(HttpServletRequest request, @ModelAttribute Experience experience, @PathVariable String username, Model model) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            experience.setUser(user);
        }

        List<ExperienceInstance> experienceInstances = experience.getInstances();
        for(ExperienceInstance experienceInstance : experienceInstances) {
            experienceInstance.setExperience(experience);
        }

        experienceRepository.save(experience);
        System.out.println("EXPERIENCE INSTANCES: ");
        return "redirect:/user/guide/" + username;
    }


    @GetMapping("/{username}/{experienceId}/update-experience")
    public String UpdateExperience(HttpServletRequest request, @PathVariable String username, @PathVariable String experienceId, Model model) {
        Optional<Experience> experience = experienceRepository.findById(Long.parseLong(experienceId));
        Optional<User> user = userRepository.findByUsername(username);

        if(experience.isPresent()) {
            model.addAttribute("experience", experience.get());
        }

        if(user.isPresent()) {
            model.addAttribute("user", user.get());
        }

        return "update-experience";
    }

    @PostMapping("/{username}/{experienceId}/update-experience")
    public String PostUpdateExperience(HttpServletRequest request, @ModelAttribute Experience experience, @PathVariable String experienceId, @PathVariable String username, Model model) {
        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User with username '" + username + "' was not found.");
            return "error";
        }

        Optional<Experience> existingExperienceOpt = experienceRepository.findById(Long.parseLong(experienceId));
        if (existingExperienceOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Experience not found.");
            return "error";
        }

        Experience existingExperience = existingExperienceOpt.get();
        User user = userOptional.get();

        existingExperience.setTitle(experience.getTitle());
        existingExperience.setDescription(experience.getDescription());
        existingExperience.setShortDescription(experience.getShortDescription());
        existingExperience.setCountry(experience.getCountry());
        existingExperience.setCity(experience.getCity());
        existingExperience.setLocation(experience.getLocation());
        existingExperience.setMaxParticipants(experience.getMaxParticipants());
        existingExperience.setDuration(experience.getDuration());
        existingExperience.setPrice(experience.getPrice());
        existingExperience.setUser(user);

        if (experience.getInstances() != null) {
            existingExperience.getInstances().clear();

            for (ExperienceInstance instance : experience.getInstances()) {
                instance.setExperience(existingExperience);
                if (instance.getPrice() == 0.0) {
                    instance.setPrice(existingExperience.getPrice());
                }
                existingExperience.getInstances().add(instance);
            }
        }

        try {
            experienceRepository.saveAndFlush(existingExperience);
            return "redirect:/user/guide/" + username;
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update experience: " + e.getMessage());
            model.addAttribute("experience", existingExperience);
            model.addAttribute("user", user);
            return "update-experience";
        }
    }

    private boolean checkValidToken(HttpServletRequest request, String username) {
        String token = "";
        for (Cookie cookie : request.getCookies()) {
            if (("user-token-" + username).equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }

        if(token.isEmpty()) return false;

        try {
            jwtService.extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
