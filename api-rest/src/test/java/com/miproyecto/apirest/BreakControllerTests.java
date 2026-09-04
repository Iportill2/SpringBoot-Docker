package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;
import com.miproyecto.apirest.security.JwtService;

/**
 * Pruebas del controlador de descansos (/api/break).
 *
 * <p>El {@link BreakController} actual expone dos endpoints que solo
 * requieren estar autenticados y devuelven la hora actual del servidor
 * JSON: {@code POST /api/break/start} y {@code POST /api/break/end}.
 * Cualquier usuario autenticado (aqui se usa EMPLEADO, rol 1) puede
 * usarlos.</p>
 */
@ApiRestTest
class BreakControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private JwtService jwtService;

    @Test
    void startBreakReturns200WithTime() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/break/start")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").isNotEmpty());
    }

    @Test
    void startBreakWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/break/start"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endBreakReturns200WithTime() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/break/end")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").isNotEmpty());
    }

    @Test
    void endBreakWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/break/end"))
                .andExpect(status().isUnauthorized());
    }

    // El endpoint devuelve la hora del servidor truncada a segundos, por lo
    // que debe coincidir (o ser aproximadamente igual) a LocalDateTime.now().
    @Test
    void startBreakTimeMatchesServerClock() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        String expected = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();

        mockMvc.perform(post("/api/break/start")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value(expected));
    }

    private Users saveUser(String username) {
        Roles role = roleRepo.findById(1).orElseThrow();
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
