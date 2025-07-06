package com.example.csis3275.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class exampleController {
    @GetMapping(path = "/")
    public String example(Model model) {
        return "example";
    }
}
