package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.miproyecto.apirest.security.JwtService;

@ApiRestTest
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private JwtService jwtService;

    @Test
    void createUserIsPublicAndReturns201() throws Exception {
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "nuevo", "pass": "pass123", "email": "nuevo@test.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("nuevo"))
                .andExpect(jsonPath("$.role.name").value("PENDIENTE"));

        mockMvc.perform(get("/api/user/name/exist/nuevo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void findAllReturnsUsersWithToken() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void findByIdReturnsUser() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/user/{id}", user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void findByIdWithUnknownIdReturns404() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/user/{id}", 99999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdWithInvalidIdReturns400() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/user/{id}", 0)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByEmailAndUsernameReturnUser() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/user/email/{email}", "testuser@test.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        mockMvc.perform(get("/api/user/name/{username}", "testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@test.com"));
    }

    @Test
    void blockedAndBannedEndpointsReturnBooleans() throws Exception {
        Roles role = roleRepo.findById(1).orElseThrow();
        Users normal = saveUser("testuser");
        Users blocked = saveUser("blockeduser");
        blocked.setBlocked(true);
        userRepo.save(blocked);

        String token = jwtService.generateToken(normal);

        mockMvc.perform(get("/api/user/blocked/{username}", "testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        mockMvc.perform(get("/api/user/blocked/{username}", "blockeduser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/user/banned/{username}", "testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void patchApproveRoleUpdatesUser() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(patch("/api/user/{id}", user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": {"id": 2}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void patchUpdateFieldsModifiesUser() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(patch("/api/user/{id}", user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fails": 3, "blocked": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void patchWithUnknownIdReturns404() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(patch("/api/user/{id}", 99999)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"blocked": true}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserRemovesIt() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(delete("/api/user/{id}", user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/user/{id}", user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteWithUnknownIdReturns404() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(delete("/api/user/{id}", 99999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
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
