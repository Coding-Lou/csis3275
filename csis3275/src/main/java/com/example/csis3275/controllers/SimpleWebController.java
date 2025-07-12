package com.example.csis3275.controllers;

import com.example.csis3275.entities.User;
import com.example.csis3275.entities.dto.RegisterUser;
import com.example.csis3275.services.AuthenticationService;
import com.example.csis3275.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web")
public class SimpleWebController {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @GetMapping("/login")
    public String loginForm() {
        return "simple-login";
    }
    
    @PostMapping("/login")
    public String login(@ModelAttribute RegisterUser loginUserDto, Model model) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            
            String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                    authenticatedUser.getUsername(),
                    authenticatedUser.getPasswordHash(),
                    java.util.Collections.emptyList()
            ));
            
            return "redirect:/web/message?token=" + jwtToken;
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "simple-login";
        }
    }
    
    @GetMapping("/message")
    public String messagePage(@RequestParam(required = false) String token, Model model) {
        if (token == null || !isValidToken(token)) {
            return "redirect:/web/login?error=invalid";
        }
        
        String username = jwtService.extractUsername(token);
        model.addAttribute("username", username);
        model.addAttribute("token", token);
        return "simple-message";
    }
    
    @PostMapping("/action")
    public String performAction(@RequestParam String token, @RequestParam String action, Model model) {
        if (isValidToken(token)) {
            String username = jwtService.extractUsername(token);
            model.addAttribute("username", username);
            model.addAttribute("token", token);
            model.addAttribute("actionResult", "Action '" + action + "' completed successfully!");
            return "simple-message";
        } else {
            return "redirect:/web/login?error=invalid";
        }
    }
    
    private boolean isValidToken(String token) {
        try {
            jwtService.extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
