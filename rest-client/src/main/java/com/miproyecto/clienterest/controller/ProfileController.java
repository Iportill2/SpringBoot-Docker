package com.miproyecto.clienterest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profileGet(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        ResponseEntity<UsersDTO> response = userService.findByUsername(username);
        UsersDTO user = response.getBody();

        model.addAttribute("user", user);

        return "app/profile";
    }
}