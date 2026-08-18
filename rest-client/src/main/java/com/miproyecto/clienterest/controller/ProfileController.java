package com.miproyecto.clienterest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import com.miproyecto.clienterest.dto.SendQuestionDTO;
import com.miproyecto.clienterest.dto.UserQuestionPrintDTO;
import com.miproyecto.clienterest.dto.UserQuestionReadDTO;
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
        String jwt = (String) session.getAttribute("jwt");

        if (username == null || jwt == null) {
            return "redirect:/login";
        }

        // 1. Obtener datos del usuario
        ResponseEntity<UsersDTO> response = userService.findByUsername(username);
        UsersDTO user = response.getBody();

        // Declaración con el DTO correcto
        List<UserQuestionPrintDTO> userQuestions = new ArrayList<>();

        // 2. Si el usuario existe, solicitamos las preguntas
        if (user != null) {
            try {
                ResponseEntity<List<UserQuestionPrintDTO>> questionsResponse = userService
                        .findQuestionsByUserId(user.getId(), jwt);

                if (questionsResponse.getStatusCode().is2xxSuccessful() && questionsResponse.getBody() != null) {
                    userQuestions = questionsResponse.getBody();
                }
            } catch (Exception e) {
                System.err.println("Error al obtener preguntas: " + e.getMessage());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("userQuestions", userQuestions);

        return "app/profile";
    }

}