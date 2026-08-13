package com.miproyecto.clienterest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.client.RestClientResponseException;

import tools.jackson.databind.ObjectMapper;
import com.miproyecto.clienterest.dto.AuthResponseDTO;
import com.miproyecto.clienterest.dto.LoginDTO;
import com.miproyecto.clienterest.dto.QuestionAnswerDTO;
import com.miproyecto.clienterest.dto.QuestionDTO;
import com.miproyecto.clienterest.dto.UserQuestionsDTO;
import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.AuthService;
import com.miproyecto.clienterest.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public AuthController(AuthService authService, UserService userService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.userService = userService;
        this.objectMapper = objectMapper;
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

        try {
            ResponseEntity<AuthResponseDTO> response = authService.login(loginDTO.getUsername(), loginDTO.getPass());

            AuthResponseDTO body = response.getBody();

            session.setAttribute("jwt", body.getToken());
            session.setAttribute("userId", body.getUserId());
            session.setAttribute("username", body.getUsername());
            session.setAttribute("role", body.getRole());

            return "redirect:/menu/clock-in";

        } catch (RestClientResponseException e) {
            model.addAttribute("error", extractError(e));
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "No se ha podido iniciar sesión");
            return "auth/login";
        }
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

        if (!usersDTO.getConfirmPass().equals(usersDTO.getPass())) {
            model.addAttribute("error", "Las contraseñas no son iguales");
            return "auth/register";
        }

        ResponseEntity<Boolean> userResponse = userService.existsByUsername(usersDTO.getUsername());

        if (userResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || Boolean.TRUE.equals(userResponse.getBody())) {

            model.addAttribute("error", "No se ha podido completar el registro");
            return "auth/register";
        }

        ResponseEntity<Boolean> emailResponse = userService.existsByEmail(usersDTO.getEmail());

        if (emailResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || Boolean.TRUE.equals(emailResponse.getBody())) {

            model.addAttribute("error", "No se ha podido completar el registro");
            return "auth/register";
        }

        ResponseEntity<UsersDTO> response = userService.create(usersDTO);

        if (response.getStatusCode() == HttpStatus.CREATED) {

            UsersDTO createdUser = response.getBody();

            return "redirect:/register/questions?id=" + createdUser.getId();
        }

        model.addAttribute("error", "No se ha podido completar el registro");

        return "auth/register";
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
            List<QuestionDTO> questions = userService.findAllQuestions().getBody();
            model.addAttribute("questions", questions);
            return "auth/register-2";
        }

        boolean allSaved = true;
        for (QuestionAnswerDTO answer : userQuestionsDTO.getAnswers()) {

            ResponseEntity<?> response = userService.saveQuestion(
                    userQuestionsDTO.getUserId(),
                    answer.getQuestionId(),
                    answer.getAnswer());

            if (response.getStatusCode() != HttpStatus.CREATED) {
                allSaved = false;
                break;
            }
        }

        if (allSaved) {
            return "redirect:/login";
        }

        List<QuestionDTO> questions = userService.findAllQuestions().getBody();
        model.addAttribute("questions", questions);
        model.addAttribute("error", "No se pudieron guardar las respuestas");
        return "auth/register-2";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String extractError(RestClientResponseException e) {
        try {
            String body = e.getResponseBodyAsString();

            if (body != null && !body.isBlank()) {
                Map<?, ?> map = objectMapper.readValue(body, Map.class);
                Object message = map.get("error");

                if (message != null) {
                    return message.toString();
                }
            }
        } catch (Exception ignored) {
        }

        return "Usuario o contraseña incorrectos";
    }
}