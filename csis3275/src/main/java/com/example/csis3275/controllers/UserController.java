package com.example.csis3275.controllers;

import com.example.csis3275.entities.User;
import com.example.csis3275.entities.dto.UserDTO;
import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.services.AuthenticationService;
import com.example.csis3275.services.JwtService;
import com.example.csis3275.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/user")
@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserDTO loginUserDto, Model model) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            return "redirect:/user/" + authenticatedUser.getUsername();
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "error";
        }
    }

    @GetMapping("/{username}")
    public String getUserProfile(@PathVariable String username, Model model) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User with username '" + username + "' was not found.");
            return "error";
        }

        model.addAttribute("user", userOptional.get());
        return "profile";
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerUser", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute UserDTO userDTO, Model model) {
        String username = userDTO.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            model.addAttribute("errorMessage", "User with '" + username + "' has already been created.");
            return "error";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        userDTO.setPassword( encoder.encode(userDTO.getPassword()) );
        userRepository.save(userDTO.toUser());
        return "redirect:/";
    }


    @PostMapping("/delete")
    public String deleteUser(Model model, @RequestParam(name = "username", defaultValue = "") String username) {
        if (!username.isEmpty()) {
            userRepository.deleteByUsername(username);
        }
        return "redirect:/getUser";
    }


    @GetMapping("/update/{username}")
    public String showUpdateForm(@PathVariable String username, Model model) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found.");
            return "error";
        }
        model.addAttribute("user", userOpt.get());
        return "profile-update";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User updatedUser, Model model) {
        Optional<User> existingUserOpt = userRepository.findById(updatedUser.getId());
        if (existingUserOpt.isEmpty()) {
            model.addAttribute("error", "User not found.");
            return "error";
        }

        User user = existingUserOpt.get();

        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhone(updatedUser.getPhone());
        user.setCountry(updatedUser.getCountry());

        userRepository.save(user);
        return "redirect:/user/" + user.getUsername();
    }


}
