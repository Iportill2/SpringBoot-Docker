package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

	
    @GetMapping("/profile")
    public String profileGet(Model model) {
        
        return "app/profile";
    }
}
