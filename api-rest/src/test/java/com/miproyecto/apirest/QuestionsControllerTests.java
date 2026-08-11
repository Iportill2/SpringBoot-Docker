package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.apirest.model.Questions;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.QuestionsRepository;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;
import com.miproyecto.apirest.security.JwtService;

@ApiRestTest
class QuestionsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private QuestionsRepository questionRepo;

    @Autowired
    private JwtService jwtService;

    @Test
    void findAllIsPublicAndReturnsQuestions() throws Exception {
        questionRepo.save(new Questions(null, "¿Pregunta de prueba?"));

        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("¿Pregunta de prueba?"));
    }

    @Test
    void findByIdReturnsQuestion() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        mockMvc.perform(get("/api/questions/{id}", question.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("¿Pregunta?"));
    }

    @Test
    void createRequiresAuthAndReturns200() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text": "¿Nueva pregunta?"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("¿Nueva pregunta?"));
    }

    @Test
    void createWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text": "¿Nueva pregunta?"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateQuestionReturns200() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Questions question = questionRepo.save(new Questions(null, "¿Antigua?"));

        mockMvc.perform(put("/api/questions/{id}", question.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text": "¿Actualizada?"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("¿Actualizada?"));
    }

    @Test
    void deleteQuestionReturns200() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Questions question = questionRepo.save(new Questions(null, "¿Borrar?"));

        mockMvc.perform(delete("/api/questions/{id}", question.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    private Users saveUser(String username) {
        Roles role = roleRepo.findById(1).orElseThrow();
        Users user = new Users();
        user.setUsername(username);
        user.setPass("password123");
        user.setEmail(username + "@test.com");
        user.setCode("code-" + username);
        user.setFails(0);
        user.setBlocked(false);
        user.setBanned(false);
        user.setSalt("salt");
        user.setRole(role);
        return userRepo.save(user);
    }
}
