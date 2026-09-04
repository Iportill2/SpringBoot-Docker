package com.miproyecto.apirest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.miproyecto.apirest.controller.AuthController;
import com.miproyecto.apirest.dto.AuthResponse;
import com.miproyecto.apirest.dto.LoginRequest;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;
import com.miproyecto.apirest.security.JwtService;

/**
 * Pruebas del flujo JWT de bajo nivel, invocando directamente el servicio
 * y el controlador de autenticacion.
 *
 * <p>Usa {@link ApiRestTest} (un unico contexto compartido con el resto de
 * la suite). Los roles base los aporta {@code data-test.sql}, por lo que no
 * es necesario insertarlos aqui con {@code @Sql}.</p>
 */
@ApiRestTest
class JwtAuthFlowTests {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private AuthController authController;

    @Test
    void jwtTokenRoundTrip() {

        Roles approved = roleRepo.findById(1).get();
        Users user = saveUser("testuser", "password123", approved);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void loginReturnsTokenForValidCredentials() {

        Roles approved = roleRepo.findById(1).get();
        saveUser("testuser", "password123", approved);

        ResponseEntity<?> response = authController.login(
                new LoginRequest("testuser", "password123"));

        assertEquals(HttpStatus.OK, response.getStatusCode());

        AuthResponse body = (AuthResponse) response.getBody();

        assertNotNull(body);
        assertNotNull(body.token());
        assertEquals("testuser", body.username());
    }

    @Test
    void loginRejectsWrongPassword() {

        Roles approved = roleRepo.findById(1).get();
        saveUser("testuser", "password123", approved);

        ResponseEntity<?> response = authController.login(
                new LoginRequest("testuser", "incorrecta"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginRejectsNotApprovedRole() {

        Roles pending = roleRepo.findById(3).get();
        saveUser("testuser", "password123", pending);

        ResponseEntity<?> response = authController.login(
                new LoginRequest("testuser", "password123"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
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
