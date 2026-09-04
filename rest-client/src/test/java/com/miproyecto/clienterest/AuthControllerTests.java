package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientResponseException;

import com.miproyecto.clienterest.controller.AuthController;
import com.miproyecto.clienterest.dto.AuthResponseDTO;
import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.AuthService;
import com.miproyecto.clienterest.service.UserService;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @Test
    void loginGetShowsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("loginDTO"));
    }

    @Test
    void loginPostSuccessStoresSessionAndRedirects() throws Exception {
        AuthResponseDTO auth = new AuthResponseDTO(
                "jwt-token", 1, "testuser", "testuser@test.com", "EMPLEADO");

        when(authService.login("testuser", "password123"))
                .thenReturn(ResponseEntity.ok(auth));

        mockMvc.perform(post("/login")
                        .param("username", "testuser")
                        .param("pass", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/crm"))
                .andExpect(result ->
                        result.getRequest().getSession().getAttribute("jwt").equals("jwt-token"))
                .andExpect(result ->
                        result.getRequest().getSession().getAttribute("userId").equals(1))
                .andExpect(result ->
                        result.getRequest().getSession().getAttribute("username").equals("testuser"));
    }

    @Test
    void loginPostWithApiErrorShowsErrorMessage() throws Exception {
        RestClientResponseException ex = new RestClientResponseException(
                "error", 401, "Unauthorized", HttpHeaders.EMPTY,
                "{\"error\": \"Usuario o contraseña incorrectos\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);

        when(authService.login("testuser", "password123")).thenThrow(ex);

        mockMvc.perform(post("/login")
                        .param("username", "testuser")
                        .param("pass", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("error", "Usuario o contraseña incorrectos"));
    }

    @Test
    void registerGetShowsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("usersDTO"));
    }

    @Test
    void registerPostRedirectsToQuestions() throws Exception {
        when(userService.existsByUsername("nuevo")).thenReturn(ResponseEntity.ok(false));
        when(userService.existsByEmail("nuevo@test.com")).thenReturn(ResponseEntity.ok(false));

        UsersDTO created = new UsersDTO();
        created.setId(5);
        created.setUsername("nuevo");
        when(userService.create(org.mockito.ArgumentMatchers.any(UsersDTO.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(created));

        mockMvc.perform(post("/register")
                        .param("username", "nuevo")
                        .param("pass", "password123")
                        .param("confirmPass", "password123")
                        .param("email", "nuevo@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/questions?id=5"));
    }

    @Test
    void registerQuestionsGetShowsSecondStep() throws Exception {
        com.miproyecto.clienterest.dto.QuestionDTO q = new com.miproyecto.clienterest.dto.QuestionDTO();
        q.setId(1);
        q.setText("¿Pregunta?");

        when(userService.findAllQuestions())
                .thenReturn(ResponseEntity.ok(java.util.List.of(q)));

        mockMvc.perform(get("/register/questions").param("id", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register-2"))
                .andExpect(model().attributeExists("userQuestionsDTO"))
                .andExpect(model().attributeExists("questions"));
    }

    @Test
    void logoutInvalidatesSessionAndRedirects() throws Exception {
        mockMvc.perform(post("/logout")
                        .sessionAttr("userId", 1)
                        .sessionAttr("username", "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(result ->
                        assertTrue(result.getRequest().getSession(false) == null));
    }

    @Test
    void rootPathShowsLoginView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void registerQuestionsPostRedirectsToLogin() throws Exception {
        when(userService.saveQuestion(
                org.mockito.ArgumentMatchers.eq(5),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq("respuesta")))
                .thenAnswer(invocation ->
                        ResponseEntity.status(HttpStatus.CREATED).body("ok"));

        mockMvc.perform(post("/register/questions")
                        .param("userId", "5")
                        .param("answers[0].questionId", "1")
                        .param("answers[0].answer", "respuesta")
                        .param("answers[1].questionId", "2")
                        .param("answers[1].answer", "respuesta")
                        .param("answers[2].questionId", "3")
                        .param("answers[2].answer", "respuesta"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
