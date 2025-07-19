package com.example.csis3275.controllers;

import com.example.csis3275.entities.Experience;
import com.example.csis3275.repositories.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/detail/{id}")
    public String experienceDetailGet(@PathVariable int id, Model model) {
        Optional<Experience> experience = experienceRepository.findById((long) id);

        if (experience.isPresent()) {
            model.addAttribute("experience", experience.get());
            return "experience-detail";
        } else {
            return "redirect:/experience/search";   
        }
    }    
}
