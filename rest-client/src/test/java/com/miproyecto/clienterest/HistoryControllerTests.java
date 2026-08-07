package com.miproyecto.clienterest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.clienterest.controller.HistoryController;

@WebMvcTest(controllers = HistoryController.class)
class HistoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void historyGetShowsHistoryView() throws Exception {
        mockMvc.perform(get("/history")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("app/history"));
    }
}
