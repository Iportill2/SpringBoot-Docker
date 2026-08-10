package com.miproyecto.clienterest.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        model.addAttribute("username", session.getAttribute("username"));

        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        model.addAttribute("isAdmin", isAdmin);
    }
}