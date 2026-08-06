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

        Integer roleId = (Integer) session.getAttribute("roleId");
        boolean isAdmin = roleId != null && roleId == 1;
        model.addAttribute("isAdmin", isAdmin);
    }
}