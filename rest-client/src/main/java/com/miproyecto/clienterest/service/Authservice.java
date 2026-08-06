package com.miproyecto.clienterest.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miproyecto.clienterest.dto.QuestionAnswerDTO;
import com.miproyecto.clienterest.dto.RoleDTO;
import com.miproyecto.clienterest.dto.UserQuestionsDTO;
import com.miproyecto.clienterest.dto.UsersDTO;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Intenta autenticar al usuario.
     * Devuelve el UsersDTO si el login es correcto.
     * Devuelve null si algo falla, y rellena errorMessage[0] con el motivo.
     */
    public UsersDTO login(String username, String pass, String[] errorMessage) {

        ResponseEntity<UsersDTO> response;
        try {
            response = userService.findByUsername(username);
        } catch (Exception e) {
            errorMessage[0] = "Usuario o contraseña incorrectos";
            return null;
        }

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            errorMessage[0] = "Usuario o contraseña incorrectos";
            return null;
        }

        UsersDTO user = response.getBody();

        if (user.getPass() == null || !user.getPass().equals(pass)) {
            errorMessage[0] = "Usuario o contraseña incorrectos";
            return null;
        }

        RoleDTO role = user.getRole();
        if (role == null || (role.getId() != 1 && role.getId() != 2)) {
            errorMessage[0] = "Tu cuenta aún no ha sido aprobada";
            return null;
        }

        return user;
    }

    /**
     * Paso 1 del registro: comprueba duplicados y crea el usuario.
     * Devuelve el UsersDTO creado si todo va bien.
     * Devuelve null si algo falla, y rellena errorMessage[0] con el motivo.
     */
    public UsersDTO register(UsersDTO usersDTO, String[] errorMessage) {

        ResponseEntity<Boolean> userResponse = userService.existsByUsername(usersDTO.getUsername());
        if (userResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || Boolean.TRUE.equals(userResponse.getBody())) {
            errorMessage[0] = "El usuario ya existe";
            return null;
        }

        ResponseEntity<Boolean> emailResponse = userService.existsByEmail(usersDTO.getEmail());
        if (emailResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || Boolean.TRUE.equals(emailResponse.getBody())) {
            errorMessage[0] = "El email ya está registrado";
            return null;
        }

        ResponseEntity<UsersDTO> response = userService.create(usersDTO);

        if (response.getStatusCode() != HttpStatus.CREATED || response.getBody() == null) {
            errorMessage[0] = "No se ha podido completar el registro";
            return null;
        }

        return response.getBody();
    }

    /**
     * Paso 2 del registro: guarda las respuestas a las preguntas de seguridad.
     * Devuelve true si todas se guardaron correctamente.
     */
    public boolean saveQuestions(UserQuestionsDTO userQuestionsDTO) {

        for (QuestionAnswerDTO answer : userQuestionsDTO.getAnswers()) {
            ResponseEntity<?> response = userService.saveQuestion(
                    userQuestionsDTO.getUserId(),
                    answer.getQuestionId(),
                    answer.getAnswer()
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                return false;
            }
        }

        return true;
    }
}