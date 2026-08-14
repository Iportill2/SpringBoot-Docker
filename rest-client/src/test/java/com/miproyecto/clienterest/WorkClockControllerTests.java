package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.clienterest.controller.WorkClockController;
import com.miproyecto.clienterest.dto.TimeEntryDTO;
import com.miproyecto.clienterest.service.BreakClientService;
import com.miproyecto.clienterest.service.TimeEntryService;

@WebMvcTest(controllers = WorkClockController.class)
class WorkClockControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeEntryService timeEntryService;

    @MockitoBean
    private BreakClientService breakClientService;

    @Test
    void clockInGetShowsClockInViewWithSessionAttributes() throws Exception {
        mockMvc.perform(get("/menu/clock-in")
                .sessionAttr("userId", 5)
                .sessionAttr("username", "testuser")
                .sessionAttr("startTime", "07/08/2026 09:00:00")
                .sessionAttr("pauseTime", "07/08/2026 10:00:00")
                .sessionAttr("resumeTime", "07/08/2026 10:15:00")
                .sessionAttr("endTime", "07/08/2026 14:00:00")
                .sessionAttr("breakOpen", true))
                .andExpect(status().isOk())
                .andExpect(view().name("app/clock-in"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("startTime", "07/08/2026 09:00:00"))
                .andExpect(model().attribute("pauseTime", "07/08/2026 10:00:00"))
                .andExpect(model().attribute("resumeTime", "07/08/2026 10:15:00"))
                .andExpect(model().attribute("endTime", "07/08/2026 14:00:00"))
                .andExpect(model().attribute("breakOpen", true));
    }

    @Test
    void startStoresEntryInSessionAndRedirects() throws Exception {
        TimeEntryDTO entry = new TimeEntryDTO();
        entry.setId(10);
        entry.setStartTime("2026-08-07T09:00:00");

        when(timeEntryService.start(5)).thenReturn(entry);

        mockMvc.perform(post("/menu/clock-in/start")
                .sessionAttr("userId", 5))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/clock-in"))
                .andExpect(result -> {
                    var session = result.getRequest().getSession();
                    assertTrue(session.getAttribute("timeEntryId").equals(10));
                    assertTrue(session.getAttribute("startTime").equals("07/08/2026 09:00:00"));
                    assertFalse(Boolean.TRUE.equals(session.getAttribute("breakOpen")));
                    assertTrue(session.getAttribute("endTime") == null);
                });
    }

    @Test
    void pauseStoresPauseTimeInSessionAndRedirects() throws Exception {
        when(breakClientService.start()).thenReturn("2026-08-07T10:00:00");

        mockMvc.perform(post("/menu/clock-in/pause")
                .sessionAttr("userId", 5)
                .sessionAttr("timeEntryId", 10))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/clock-in"))
                .andExpect(result -> {
                    var session = result.getRequest().getSession();
                    assertTrue(session.getAttribute("pauseStartRaw").equals("2026-08-07T10:00:00"));
                    assertTrue(session.getAttribute("pauseTime").equals("07/08/2026 10:00:00"));
                    assertTrue(Boolean.TRUE.equals(session.getAttribute("breakOpen")));
                });
    }

    @Test
    void resumeWithoutBreakIdOnlyClearsSession() throws Exception {
        mockMvc.perform(post("/menu/clock-in/resume")
                .sessionAttr("userId", 5))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/clock-in"))
                .andExpect(result -> assertFalse(Boolean.TRUE.equals(
                        result.getRequest().getSession().getAttribute("breakOpen"))));
    }

    @Test
    void stopEndsTimeEntryUsingAccumulatedPauseMinutesThenRedirects() throws Exception {
        TimeEntryDTO entry = new TimeEntryDTO();
        entry.setId(10);
        entry.setEndTime("2026-08-07T14:00:00");
        entry.setDate("2026-08-07");

        when(timeEntryService.stop(10, 30)).thenReturn(entry);

        mockMvc.perform(post("/menu/clock-in/stop")
                .sessionAttr("userId", 5)
                .sessionAttr("timeEntryId", 10)
                .sessionAttr("totalPauseMinutes", 30))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/clock-in"))
                .andExpect(result -> {
                    var session = result.getRequest().getSession();
                    assertTrue(session.getAttribute("endTime").equals("07/08/2026 14:00:00"));
                });

        verify(timeEntryService).stop(10, 30);
    }

    @Test
    void resetClearsAllSessionAttributes() throws Exception {
        mockMvc.perform(post("/menu/clock-in/reset")
                .sessionAttr("userId", 5)
                .sessionAttr("timeEntryId", 10)
                .sessionAttr("breakId", 7)
                .sessionAttr("startTime", "07/08/2026 09:00:00")
                .sessionAttr("breakOpen", true))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/clock-in"))
                .andExpect(result -> {
                    var session = result.getRequest().getSession();
                    assertTrue(session.getAttribute("timeEntryId") == null);
                    assertTrue(session.getAttribute("breakId") == null);
                    assertTrue(session.getAttribute("startTime") == null);
                    assertTrue(session.getAttribute("pauseTime") == null);
                    assertTrue(session.getAttribute("resumeTime") == null);
                    assertTrue(session.getAttribute("endTime") == null);
                    assertTrue(session.getAttribute("breakOpen") == null);
                });
    }
}
