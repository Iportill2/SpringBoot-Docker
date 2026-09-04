package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;

@ApiRestTest
class AuthHttpTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Test
    void loginWithValidCredentialsReturnsToken() throws Exception {
        Roles role = roleRepo.findById(1).orElseThrow();
        saveUser("testuser", "password123", role);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "testuser", "pass": "password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@test.com"))
                .andExpect(jsonPath("$.role").value("EMPLEADO"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        Roles role = roleRepo.findById(1).orElseThrow();
        saveUser("testuser", "password123", role);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "testuser", "pass": "incorrecta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Usuario o contraseña incorrectos"));
    }

    // Un usuario bloqueado es aquel cuyo rol es BLOQUEADO (id 4). El login
    // debe rechazarlo con 403 y el mensaje de bloqueo.
    @Test
    void loginWithBlockedUserReturns403() throws Exception {
        Roles role = roleRepo.findById(4).orElseThrow();
        saveUser("testuser", "password123", role);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "testuser", "pass": "password123"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Tu cuenta ha sido bloqueada"));
    }

    // No existe una rama de login separada para "baneado": el rol BLOQUEADO
    // (id 4) tambien se usa para usuarios baneados, devolviendo 403.
    @Test
    void loginWithBannedUserReturns403() throws Exception {
        Roles role = roleRepo.findById(4).orElseThrow();
        saveUser("testuser", "password123", role);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "testuser", "pass": "password123"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginWithPendingRoleReturns403() throws Exception {
        Roles role = roleRepo.findById(3).orElseThrow();
        saveUser("testuser", "password123", role);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "testuser", "pass": "password123"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Tu cuenta aún no ha sido aprobada"));
    }

    @Test
    void loginWithBlankFieldsReturns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "  ", "pass": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usuario y contraseña obligatorios"));
    }

    @Test
    void loginWithUnknownUserReturns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "noexiste", "pass": "password123"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private Users saveUser(String username, String pass, Roles role) {
        Users user = new Users();
        user.setUsername(username);
        user.setPass(pass);
        user.setEmail(username + "@test.com");
        user.setCode("code-" + username);
        user.setSalt("salt");
        user.setRole(role);
        return userRepo.save(user);
    }
}
