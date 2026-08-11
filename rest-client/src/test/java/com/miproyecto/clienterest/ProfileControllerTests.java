package com.miproyecto.clienterest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.clienterest.controller.ProfileController;
import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.UserService;

@WebMvcTest(controllers = ProfileController.class)
class ProfileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void profileGetLoadsUserFromSessionAndShowsView() throws Exception {
        UsersDTO user = new UsersDTO();
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("testuser@test.com");

        when(userService.findByUsername("testuser")).thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(get("/profile")
                        .sessionAttr("userId", 1)
                        .sessionAttr("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("app/profile"))
                .andExpect(model().attribute("user", user));
    }
}
