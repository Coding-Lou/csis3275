package com.example.csis3275.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuestController {
    @GetMapping("/")
    public String home() {
        return "index";  // resolves to templates/index.html
    }
}
