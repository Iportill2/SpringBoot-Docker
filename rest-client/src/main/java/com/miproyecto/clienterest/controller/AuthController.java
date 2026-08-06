package com.miproyecto.clienterest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.miproyecto.clienterest.dto.LoginDTO;
import com.miproyecto.clienterest.dto.QuestionAnswerDTO;
import com.miproyecto.clienterest.dto.QuestionDTO;
import com.miproyecto.clienterest.dto.RoleDTO;
import com.miproyecto.clienterest.dto.UserQuestionsDTO;
import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.AuthService;
import com.miproyecto.clienterest.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping({ "/", "/login" })
    public String loginGet(Model model) {

        model.addAttribute("loginDTO", new LoginDTO());

        return "auth/login";
    }

    @PostMapping("/login")
    public String loginPost(@Valid @ModelAttribute LoginDTO loginDTO, BindingResult result, Model model,
            HttpSession session) {

        if (result.hasErrors()) {
            return "auth/login";
        }

        String[] error = new String[1];
        UsersDTO user = authService.login(loginDTO.getUsername(), loginDTO.getPass(), error);

        if (user == null) {
            model.addAttribute("error", error[0]);
            model.addAttribute("loginDTO", new LoginDTO());
            return "auth/login";
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("roleId", user.getRole().getId());

        return "redirect:/menu/clock-in";
    }

    /*
     * PRIMER PASO REGISTRO
     */

    @GetMapping("/register")
    public String registerGet(Model model) {

        model.addAttribute("usersDTO", new UsersDTO());

        return "auth/register";
    }

    @PostMapping("/register")
    public String registerPost(
            @Valid @ModelAttribute UsersDTO usersDTO,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        String[] error = new String[1];
        UsersDTO createdUser = authService.register(usersDTO, error);

        if (createdUser == null) {
            model.addAttribute("error", error[0]);
            model.addAttribute("usersDTO", new UsersDTO());
            return "auth/register";
        }

        return "redirect:/register/questions?id=" + createdUser.getId();
    }

    /*
     * SEGUNDO PASO REGISTRO
     */

    @GetMapping("/register/questions")
    public String registerQuestions(
            @RequestParam Integer id,
            Model model) {

        List<QuestionDTO> questions = userService.findAllQuestions().getBody();

        UserQuestionsDTO userQuestionsDTO = new UserQuestionsDTO();
        userQuestionsDTO.setUserId(id);

        List<QuestionAnswerDTO> answers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            answers.add(new QuestionAnswerDTO(null, ""));
        }
        userQuestionsDTO.setAnswers(answers);

        model.addAttribute("userQuestionsDTO", userQuestionsDTO);
        model.addAttribute("questions", questions);

        return "auth/register-2";
    }

    @PostMapping("/register/questions")
    public String registerQuestionsPost(
            @Valid @ModelAttribute UserQuestionsDTO userQuestionsDTO,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "auth/register-2";
        }

        boolean allSaved = authService.saveQuestions(userQuestionsDTO);

        if (allSaved) {
            return "redirect:/login";
        }

        model.addAttribute("error", "No se pudieron guardar las respuestas");
        return "auth/register-2";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
