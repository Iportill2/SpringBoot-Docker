package com.miproyecto.clienterest;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.clienterest.controller.CalendarController;
import com.miproyecto.clienterest.dto.TimeEntryDTO;
import com.miproyecto.clienterest.service.TimeEntryService;

@WebMvcTest(controllers = CalendarController.class)
class CalendarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeEntryService timeEntryService;

    @Test
    void calendarGetWithoutParamsShowsCurrentMonth() throws Exception {
        when(timeEntryService.findByMonth(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(get("/menu/calendar")
                        .sessionAttr("userId", 5))
                .andExpect(status().isOk())
                .andExpect(view().name("app/calendar"))
                .andExpect(model().attributeExists("monthLabel"))
                .andExpect(model().attributeExists("daysInMonth"))
                .andExpect(model().attributeExists("startOffset"))
                .andExpect(model().attribute("hoursByDay", Map.of()))
                .andExpect(model().attributeExists("prevYear", "prevMonth", "nextYear", "nextMonth"));
    }

    @Test
    void calendarGetWithYearAndMonthBuildsHoursByDay() throws Exception {
        TimeEntryDTO entry = new TimeEntryDTO();
        entry.setDate("2026-08-05");
        entry.setTotalMinutesWorked(130);

        when(timeEntryService.findByMonth(eq(5), eq(2026), eq(8)))
                .thenReturn(List.of(entry));

        int expectedOffset = LocalDate.of(2026, 8, 1).getDayOfWeek().getValue() - 1;

        mockMvc.perform(get("/menu/calendar")
                        .sessionAttr("userId", 5)
                        .param("year", "2026")
                        .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(view().name("app/calendar"))
                .andExpect(model().attribute("monthLabel", "AUGUST 2026"))
                .andExpect(model().attribute("daysInMonth", 31))
                .andExpect(model().attribute("startOffset", expectedOffset))
                .andExpect(model().attribute("hoursByDay", Map.of(5, "2h 10m")))
                .andExpect(model().attribute("prevYear", 2026))
                .andExpect(model().attribute("prevMonth", 7))
                .andExpect(model().attribute("nextYear", 2026))
                .andExpect(model().attribute("nextMonth", 9));
    }

    @Test
    void calendarGetIgnoresEntriesWithoutTotalMinutesWorked() throws Exception {
        TimeEntryDTO withoutHours = new TimeEntryDTO();
        withoutHours.setDate("2026-08-10");
        withoutHours.setTotalMinutesWorked(null);

        TimeEntryDTO withHours = new TimeEntryDTO();
        withHours.setDate("2026-08-20");
        withHours.setTotalMinutesWorked(60);

        when(timeEntryService.findByMonth(eq(5), eq(2026), eq(8)))
                .thenReturn(List.of(withoutHours, withHours));

        mockMvc.perform(get("/menu/calendar")
                        .sessionAttr("userId", 5)
                        .param("year", "2026")
                        .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("hoursByDay", Map.of(20, "1h 0m")));
    }
}
