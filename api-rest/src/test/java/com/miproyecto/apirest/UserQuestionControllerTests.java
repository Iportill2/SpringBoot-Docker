package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class UserQuestionControllerTests {

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
    void createFromDtoIsPublicAndReturns201() throws Exception {
        Users user = saveUser("testuser");
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        mockMvc.perform(post("/api/userquestion/from-dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": %d, "questionId": %d, "answer": "respuesta"}
                                """.formatted(user.getId(), question.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.answer").value("respuesta"));
    }

    @Test
    void createFromDtoWithUnknownUserReturns400() throws Exception {
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        mockMvc.perform(post("/api/userquestion/from-dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": 99999, "questionId": %d, "answer": "respuesta"}
                                """.formatted(question.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReturns200() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        mockMvc.perform(post("/api/userquestion")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"user": {"id": %d}, "question": {"id": %d}, "answer": "mi respuesta"}
                                """.formatted(user.getId(), question.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("mi respuesta"));
    }

    // Un usuario no-admin NO puede fijar las preguntas de seguridad de otra
    // cuenta (IDOR / account takeover). El endpoint exige que el usuario
    // objetivo sea el propio (o un ADMIN).
    @Test
    void createForAnotherUserByNonAdminIsForbidden() throws Exception {
        Users attacker = saveUserWithRole("attacker", 1);
        Users victim = saveUserWithRole("victim", 1);
        String token = jwtService.generateToken(attacker);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        mockMvc.perform(post("/api/userquestion")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"user": {"id": %d}, "question": {"id": %d}, "answer": "clave"}
                                """.formatted(victim.getId(), question.getId())))
                .andExpect(status().isForbidden());
    }

    // Sin autenticacion, el endpoint POST /api/userquestion queda fuera de la
    // regla permitAll() y de vuelve 401 (unauthenticated).
    @Test
    void createWithoutAuthIsUnauthorized() throws Exception {
        Users user = saveUser("testuser");
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        mockMvc.perform(post("/api/userquestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"user": {"id": %d}, "question": {"id": %d}, "answer": "mi respuesta"}
                                """.formatted(user.getId(), question.getId())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findAllRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/userquestion"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByUserReturnsQuestions() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        saveUserQuestion(user.getId(), question.getId(), token);

        mockMvc.perform(get("/api/userquestion/user/{id}", user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].answer").value("respuesta"));
    }

    @Test
    void checkAnswerReturnsTrueForCorrect() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        saveUserQuestion(user.getId(), question.getId(), token);

        mockMvc.perform(post("/api/userquestion/check")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": %d, "questionId": %d, "answer": "respuesta"}
                                """.formatted(user.getId(), question.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void checkAnswerReturnsFalseForWrong() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        saveUserQuestion(user.getId(), question.getId(), token);

        mockMvc.perform(post("/api/userquestion/check")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": %d, "questionId": %d, "answer": "otra cosa"}
                                """.formatted(user.getId(), question.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void checkAnswerWithUnknownUserReturns404() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        mockMvc.perform(post("/api/userquestion/check")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": 99999, "questionId": %d, "answer": "respuesta"}
                                """.formatted(question.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdReturns200() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        int uqId = saveUserQuestion(user.getId(), question.getId(), token);

        mockMvc.perform(get("/api/userquestion/{id}", uqId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("respuesta"));
    }

    @Test
    void deleteReturns200() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);
        Questions question = questionRepo.save(new Questions(null, "¿Pregunta?"));

        int uqId = saveUserQuestion(user.getId(), question.getId(), token);

        mockMvc.perform(delete("/api/userquestion/{id}", uqId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    private int saveUserQuestion(Integer userId, Integer questionId, String token) throws Exception {
        String body = mockMvc.perform(post("/api/userquestion/from-dto")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": %d, "questionId": %d, "answer": "respuesta"}
                                """.formatted(userId, questionId)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return com.jayway.jsonpath.JsonPath.read(body, "$.id");
    }

    // Algunos endpoints (DELETE /{id} -> hasRole('ADMIN')) y la validacion
    // isAdminOrSelf requieren rol ADMIN para consultar/borrar de otros
    // usuarios. Se usa el rol 2 (ADMIN) para acceder a todo el controlador.
    private Users saveUser(String username) {
        return saveUserWithRole(username, 2);
    }

    private Users saveUserWithRole(String username, Integer roleId) {
        Roles role = roleRepo.findById(roleId).orElseThrow();
        Users user = new Users();
        user.setUsername(username);
        user.setPass("password123");
        user.setEmail(username + "@test.com");
        user.setCode("code-" + username);
        user.setSalt("salt");
        user.setRole(role);
        return userRepo.save(user);
    }
}
