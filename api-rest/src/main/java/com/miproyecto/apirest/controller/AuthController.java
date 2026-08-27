package com.miproyecto.apirest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.dto.AuthResponse;
import com.miproyecto.apirest.dto.LoginRequest;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.security.JwtService;
import com.miproyecto.apirest.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.username();
        String pass = loginRequest.pass();

        if (username == null || username.isBlank() || pass == null || pass.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Usuario y contraseña obligatorios"));
        }

        Users user = userService.findByUsername(username);

        if (user == null || user.getPass() == null || !user.getPass().equals(pass)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        }

        if (user.getRole() != null && user.getRole().getId() == 4) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Tu cuenta ha sido bloqueada"));
        }

        if (user.getRole() == null || (user.getRole().getId() != 1 && user.getRole().getId() != 2)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Tu cuenta aún no ha sido aprobada"));
        }

        String token = jwtService.generateToken(user);

        AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName());

        return ResponseEntity.ok(response);
    }
}
