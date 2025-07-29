package com.example.csis3275.controllers;

import com.example.csis3275.entities.Experience;
import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.ExperienceRepository;
import com.example.csis3275.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.List;

@Controller
public class GuestController {
    @Autowired
    ExperienceRepository experienceRepository;
    @Autowired
    JwtService jwtService;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request, HttpSession session) {

        String username = checkValidToken(request);
        if (username.isEmpty()) {
            List<Experience> experiences = experienceRepository.findAll();
            model.addAttribute("experiences", experiences);
            return "index";
        } else {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith("user-role")) {
                    System.out.println(cookie.getValue());
                    String sessionRole = cookie.getValue();
                    return "redirect:/user/" + sessionRole + "/" + username;
                }
            }

        }

        return "index";
    }

    private String checkValidToken(HttpServletRequest request) {
        String token = "";
        try {
            if (request.getCookies().length == 0) return "";

            for (Cookie cookie : request.getCookies()) {
                if(cookie.getName().contains("user-token")) {
                    token = cookie.getValue();
                    System.out.println(token);
                    System.out.println(jwtService.extractUsername(token));
                    return jwtService.extractUsername(token);
                }

            }

            return jwtService.extractUsername(token);
        } catch (Exception e) {
            return "";
        }
    }
}
