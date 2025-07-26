package com.example.csis3275.controllers;

import com.example.csis3275.entities.Experience;
import com.example.csis3275.repositories.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GuestController {
    @Autowired
    ExperienceRepository experienceRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Experience> experiences = experienceRepository.findAll();
        model.addAttribute("experiences", experiences);
        return "index";  // resolves to templates/index.html
    }

}
