package com.miproyecto.clienterest.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.miproyecto.clienterest.dto.BreakDTO;
import com.miproyecto.clienterest.dto.TimeEntryDTO;
import com.miproyecto.clienterest.service.BreakClientService;
import com.miproyecto.clienterest.service.TimeEntryService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WorkClockController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TimeEntryService timeEntryService;
    private final BreakClientService breakClientService;

    public WorkClockController(TimeEntryService timeEntryService, BreakClientService breakClientService) {
        this.timeEntryService = timeEntryService;
        this.breakClientService = breakClientService;
    }

    @GetMapping("/clock-in")
    public String clockInGet(Model model, HttpSession session) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("horaInicio", session.getAttribute("horaInicio"));
        model.addAttribute("horaPausa", session.getAttribute("horaPausa"));
        model.addAttribute("horaFin", session.getAttribute("horaFin"));
        return "app/clock-in";
    }

    @PostMapping("/clock-in/start")
    public String start(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        TimeEntryDTO entry = timeEntryService.start(userId);

        session.setAttribute("timeEntryId", entry.getId());
        session.setAttribute("horaInicio", formatFecha(entry.getStartTime()));
        session.removeAttribute("horaFin");

        return "redirect:/clock-in";
    }

    @PostMapping("/clock-in/pause")
    public String pause(HttpSession session) {
        Integer timeEntryId = (Integer) session.getAttribute("timeEntryId");

        BreakDTO newBreak = breakClientService.start(timeEntryId);

        session.setAttribute("breakId", newBreak.getId());
        session.setAttribute("horaPausa", formatFecha(newBreak.getStartTime()));

        return "redirect:/clock-in";
    }

    @PostMapping("/clock-in/stop")
    public String stop(HttpSession session) {
        Integer breakId = (Integer) session.getAttribute("breakId");
        if (breakId != null) {
            breakClientService.end(breakId);
        }

        Integer timeEntryId = (Integer) session.getAttribute("timeEntryId");
        TimeEntryDTO entry = timeEntryService.stop(timeEntryId);

        session.setAttribute("horaFin", formatFecha(entry.getEndTime()));

        return "redirect:/clock-in";
    }

    @PostMapping("/clock-in/reset")
    public String reset(HttpSession session) {
        session.removeAttribute("timeEntryId");
        session.removeAttribute("breakId");
        session.removeAttribute("horaInicio");
        session.removeAttribute("horaPausa");
        session.removeAttribute("horaFin");
        return "redirect:/clock-in";
    }

    private String formatFecha(String rawDateTime) {
        if (rawDateTime == null) {
            return null;
        }
        LocalDateTime dt = LocalDateTime.parse(rawDateTime);
        return dt.format(FORMATTER);
    }
}