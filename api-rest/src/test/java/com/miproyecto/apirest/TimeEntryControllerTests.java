package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;
import com.miproyecto.apirest.security.JwtService;

@ApiRestTest
class TimeEntryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private JwtService jwtService;

    @Test
    void startEntryReturns200WithStartTime() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/time-entry/start/{userId}", user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.date").isNotEmpty());
    }

    @Test
    void startEntryWithUnknownUserReturns400() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/time-entry/start/{userId}", 99999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void stopEntryReturns200WithEndTime() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        int entryId = startEntryId(user.getId(), token);

        mockMvc.perform(post("/api/time-entry/stop/{timeEntryId}", entryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.totalMinutesWorked").exists());
    }

    @Test
    void stopUnknownEntryReturns400() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/time-entry/stop/{timeEntryId}", 99999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByMonthReturnsEntries() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        startEntryId(user.getId(), token);

        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/api/time-entry/user/{userId}", user.getId())
                        .param("year", String.valueOf(today.getYear()))
                        .param("month", String.valueOf(today.getMonthValue()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void findByMonthWithUnknownUserReturnsEmptyList() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/api/time-entry/user/{userId}", 99999)
                        .param("year", String.valueOf(today.getYear()))
                        .param("month", String.valueOf(today.getMonthValue()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    private int startEntryId(Integer userId, String token) throws Exception {
        String body = mockMvc.perform(post("/api/time-entry/start/{userId}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(body, "$.id");
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
