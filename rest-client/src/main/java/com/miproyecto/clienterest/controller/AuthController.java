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
import com.miproyecto.clienterest.dto.UserQuestionsDTO;
import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/")
public class AuthController {

    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping({"/", "/login"})
    public String loginGet(Model model) {

        model.addAttribute("loginDTO", new LoginDTO());

        return "auth/login";
    }


    @PostMapping("/login")
    public String loginPost(
            @Valid @ModelAttribute LoginDTO loginDTO,
            BindingResult result) {

        if (result.hasErrors()) {
            return "auth/login";
        }

        return "/layout/base-app";
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


        System.out.println("=== INICIO REGISTRO ===");
        System.out.println("Usuario recibido: " + usersDTO.getUsername());
        System.out.println("Email recibido: " + usersDTO.getEmail());


        if (result.hasErrors()) {

            System.out.println("ERROR: Fallan validaciones del formulario");
            System.out.println(result.getAllErrors());

            return "auth/register";
        }



        // Comprobar usuario existente
        System.out.println("Comprobando si existe username...");

        ResponseEntity<Boolean> userResponse =
                userService.existsByUsername(usersDTO.getUsername());


        System.out.println("Respuesta username: " 
                + userResponse.getStatusCode()
                + " - "
                + userResponse.getBody());


        if (userResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || Boolean.TRUE.equals(userResponse.getBody())) {


            System.out.println("SALIDA: Usuario ya existe o petición incorrecta");


            model.addAttribute(
                    "error",
                    "No se ha podido completar el registro"
            );


            return "auth/register";
        }



        // Comprobar email existente
        System.out.println("Comprobando si existe email...");


        ResponseEntity<Boolean> emailResponse =
                userService.existsByEmail(usersDTO.getEmail());


        System.out.println("Respuesta email: "
                + emailResponse.getStatusCode()
                + " - "
                + emailResponse.getBody());



        if (emailResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || Boolean.TRUE.equals(emailResponse.getBody())) {


            System.out.println("SALIDA: Email ya existe o petición incorrecta");


            model.addAttribute(
                    "error",
                    "No se ha podido completar el registro"
            );


            return "auth/register";
        }



        // Crear usuario
        System.out.println("Creando usuario en API...");


        ResponseEntity<UsersDTO> response =
                userService.create(usersDTO);



        System.out.println("Respuesta creación usuario: "
                + response.getStatusCode());



        if (response.getStatusCode() == HttpStatus.CREATED) {


            UsersDTO createdUser = response.getBody();


            System.out.println("USUARIO CREADO CORRECTAMENTE");
            System.out.println("ID generado: " + createdUser.getId());


            System.out.println("REDIRECCIONANDO A QUESTIONS");


            return "redirect:/register/questions?id="
                    + createdUser.getId();
        }



        System.out.println("SALIDA: Error creando usuario");
        System.out.println("Código recibido: " + response.getStatusCode());



        model.addAttribute(
                "error",
                "No se ha podido completar el registro"
        );


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

        System.out.println("=== POST REGISTER QUESTIONS ===");
        System.out.println("UserId: " + userQuestionsDTO.getUserId());
        System.out.println("Answers: " + userQuestionsDTO.getAnswers());

        if (result.hasErrors()) {
            System.out.println("ERROR: Fallan validaciones");
            System.out.println(result.getAllErrors());
            return "auth/register-2";
        }

        boolean allSaved = true;
        for (QuestionAnswerDTO answer : userQuestionsDTO.getAnswers()) {
            System.out.println("Guardando: questionId=" + answer.getQuestionId() + " answer=" + answer.getAnswer());

            ResponseEntity<?> response = userService.saveQuestion(
                    userQuestionsDTO.getUserId(),
                    answer.getQuestionId(),
                    answer.getAnswer()
            );

            System.out.println("Respuesta: " + response.getStatusCode());

            if (response.getStatusCode() != HttpStatus.CREATED) {
                allSaved = false;
                break;
            }
        }

        if (allSaved) {
            return "redirect:/login";
        }

        model.addAttribute("error", "No se pudieron guardar las respuestas");
        return "auth/register-2";
    }

}
