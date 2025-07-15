package com.example.csis3275.controllers;

import com.example.csis3275.entities.Experience;
import com.example.csis3275.repositories.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/experience")
public class ExperienceController {

    @Autowired
    ExperienceRepository experienceRepository;

    @GetMapping("/search")
    public String experienceSearch(Model model) {
        List<Experience> experiences = experienceRepository.findAll();
        model.addAttribute("experiences", experiences);
        return "experience-search";
    }

    @PostMapping("/search")
    public String experienceSearchPost(Model model, @RequestParam String search) {
        List<Experience> experiences = experienceRepository.findByTitleContainingIgnoreCase(search);
        model.addAttribute("experiences", experiences);
        return "experience-search";
    }
}
