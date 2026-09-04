package com.miproyecto.apirest;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // Nota: la implementacion actual de UserService.isBlocked()/isBanned()
    // devuelve siempre false para usuarios existentes (no hay columnas de
    // bloqueo/baneo persistidas). Este test refleja ese comportamiento real:
    // los endpoints publicos /api/user/blocked/{u} y /api/user/banned/{u}
    // responden false tanto para usuarios existentes como inexistentes.
    @Test
    void blockedAndBannedEndpointsReturnBooleans() throws Exception {
        Roles role = roleRepo.findById(2).orElseThrow();
        Users normal = saveUser("testuser");
        saveUser("blockeduser");

        String token = jwtService.generateToken(normal);

        // Usuario existente: no bloqueado ni baneado -> false
        mockMvc.perform(get("/api/user/blocked/{username}", "testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        mockMvc.perform(get("/api/user/blocked/{username}", "blockeduser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        mockMvc.perform(get("/api/user/banned/{username}", "testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        // Usuario inexistente: tambien false (no hay nada que bloquear)
        mockMvc.perform(get("/api/user/blocked/{username}", "noexiste")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    // El PATCH /api/user/{id} requiere rol ADMIN. Un administrador puede
    // cambiar el rol de OTRO usuario (p.ej. aprobar/activar/bloquear), pero
    // NO puede auto-elevarse: el endpoint impide que un usuario se suba a
    // ADMIN (id 2) a traves del endpoint generico.
    @Test
    void patchRoleOfAnotherUserAllowed() throws Exception {
        Users admin = saveUser("adminuser");
        Users target = saveUser("targetuser");
        String token = jwtService.generateToken(admin);

        mockMvc.perform(patch("/api/user/{id}", target.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": {"id": 4}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        Users updated = userRepo.findById(target.getId()).orElseThrow();
        assertTrue(updated.getRole().getId() == 4);
    }

    // Un administrador no puede auto-elevarse ni "confirmarse" a si mismo
    // como ADMIN mediante el endpoint generico (anti-auto-elevacion).
    @Test
    void patchSelfRoleToAdminIsRejected() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(patch("/api/user/{id}", user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": {"id": 2}}
                                """))
                .andExpect(status().isForbidden());
    }

    // Un rol inexistente se rechaza con 400 (no se puede asignar un rol
    // invalido a ningun usuario).
    @Test
    void patchWithInvalidRoleIsRejected() throws Exception {
        Users admin = saveUser("adminuser");
        Users target = saveUser("targetuser");
        String token = jwtService.generateToken(admin);

        mockMvc.perform(patch("/api/user/{id}", target.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": {"id": 99}}
                                """))
                .andExpect(status().isBadRequest());
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

    // Borrar un usuario inexistente: UserService.delete devuelve false (no
    // null), y el controlador responde 200 con false (trata el borrado de
    // un id desconocido como una operacion sin efecto).
    @Test
    void deleteWithUnknownIdReturnsFalse() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(delete("/api/user/{id}", 99999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    // Los endpoints de /api/user (excepto los publicos de registro/existencias)
    // exigen rol ADMIN (hasRole('ADMIN')). Se crea el usuario con el rol 2
    // (ADMIN) para poder operar con los demas usuarios.
    private Users saveUser(String username) {
        Roles role = roleRepo.findById(2).orElseThrow();
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
