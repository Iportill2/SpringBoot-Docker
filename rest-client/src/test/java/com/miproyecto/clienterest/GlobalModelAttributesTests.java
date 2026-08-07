package com.miproyecto.clienterest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.clienterest.controller.MenuController;

@WebMvcTest(controllers = MenuController.class)
class GlobalModelAttributesTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addsUsernameFromSessionToEveryModel() throws Exception {
        mockMvc.perform(get("/menu")
                        .sessionAttr("userId", 1)
                        .sessionAttr("username", "pepe"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "pepe"));
    }
}
