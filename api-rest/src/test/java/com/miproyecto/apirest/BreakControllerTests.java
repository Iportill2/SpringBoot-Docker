package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.TimeEntry;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.TimeEntryRepository;
import com.miproyecto.apirest.repository.UsersRepository;
import com.miproyecto.apirest.security.JwtService;

@ApiRestTest
class BreakControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private TimeEntryRepository timeEntryRepo;

    @Autowired
    private JwtService jwtService;

    @Test
    void startBreakReturns200WithStartTime() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        TimeEntry entry = saveEntry(user);

        mockMvc.perform(post("/api/break/start/{timeEntryId}", entry.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.startTime").isNotEmpty());
    }

    @Test
    void startBreakWithUnknownEntryReturns400() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/break/start/{timeEntryId}", 99999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void endBreakReturns200WithEndTime() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        TimeEntry entry = saveEntry(user);

        int breakId = startBreakId(entry.getId(), token);

        mockMvc.perform(post("/api/break/end/{breakId}", breakId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endTime").isNotEmpty());
    }

    @Test
    void endBreakWithUnknownIdReturns400() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/break/end/{breakId}", 99999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    private int startBreakId(Integer timeEntryId, String token) throws Exception {
        String body = mockMvc.perform(post("/api/break/start/{timeEntryId}", timeEntryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(body, "$.id");
    }

    private TimeEntry saveEntry(Users user) {
        TimeEntry entry = new TimeEntry();
        entry.setUser(user);
        entry.setDate(LocalDate.now());
        entry.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return timeEntryRepo.save(entry);
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
