package com.example.csis3275.controllers;

import com.example.csis3275.entities.User;
import com.example.csis3275.entities.dto.UserDTO;
import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/user")
@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/getUser")
    public String getUser(Model model, @RequestParam(name = "username", defaultValue = "") String username) {
        List<User> users;
        if (username.isEmpty()) {
            users = userService.getAll();
        } else {
            Optional<User> user = userService.getByUsername(username);
            users = user.map(List::of).orElse(List.of());
        }

        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerUser", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute UserDTO userDTO) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        userDTO.setPassword( encoder.encode(userDTO.getPassword()) );
        userRepository.save(userDTO.toUser());
        return "redirect:/getUser";
    }

    @PostMapping("/delete")
    public String deleteUser(Model model, @RequestParam(name = "username", defaultValue = "") String username) {
        if (!username.isEmpty()) {
            userRepository.deleteByUsername(username);
        }
        return "redirect:/getUser";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        Optional<User> existingUser = userService.getByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            user.setId(existingUser.get().getId());
            userService.save(user);
        }
        return "redirect:/getUser";
    }

}
