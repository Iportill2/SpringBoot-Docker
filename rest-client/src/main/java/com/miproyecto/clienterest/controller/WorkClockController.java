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
        model.addAttribute("startTime", session.getAttribute("startTime"));
        model.addAttribute("pauseTime", session.getAttribute("pauseTime"));
        model.addAttribute("resumeTime", session.getAttribute("resumeTime"));
        model.addAttribute("endTime", session.getAttribute("endTime"));
        model.addAttribute("breakOpen", session.getAttribute("breakOpen"));
        return "app/clock-in";
    }

    @PostMapping("/clock-in/start")
    public String start(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        TimeEntryDTO entry = timeEntryService.start(userId);

        session.setAttribute("timeEntryId", entry.getId());
        session.setAttribute("startTime", formatDate(entry.getStartTime()));
        session.setAttribute("breakOpen", false);
        session.removeAttribute("endTime");
        session.removeAttribute("pauseTime");
        session.removeAttribute("resumeTime");
        

        return "redirect:/clock-in";
    }

    @PostMapping("/clock-in/pause")
    public String pause(HttpSession session) {
        Integer timeEntryId = (Integer) session.getAttribute("timeEntryId");

        BreakDTO newBreak = breakClientService.start(timeEntryId);

        session.setAttribute("breakId", newBreak.getId());
        session.setAttribute("pauseTime", formatDate(newBreak.getStartTime()));
        session.setAttribute("breakOpen", true);

        return "redirect:/clock-in";
    }

    @PostMapping("/clock-in/resume")
    public String resume(HttpSession session) {
        Integer breakId = (Integer) session.getAttribute("breakId");
        if (breakId != null) {
            BreakDTO endedBreak = breakClientService.end(breakId);
            session.setAttribute("resumeTime", formatDate(endedBreak.getEndTime()));
        }

        session.removeAttribute("breakId");
        session.setAttribute("breakOpen", false);

        return "redirect:/clock-in";
    }

    @PostMapping("/clock-in/stop")
    public String stop(HttpSession session) {
        Integer breakId = (Integer) session.getAttribute("breakId");

        if (breakId != null) {
            breakClientService.end(breakId);
            session.removeAttribute("breakId");
        }

        Integer timeEntryId = (Integer) session.getAttribute("timeEntryId");
        TimeEntryDTO entry = timeEntryService.stop(timeEntryId);

        session.setAttribute("endTime", formatDate(entry.getEndTime()));

        return "redirect:/clock-in";
    }

    @PostMapping("/clock-in/reset")
    public String reset(HttpSession session) {
        session.removeAttribute("timeEntryId");
        session.removeAttribute("breakId");
        session.removeAttribute("startTime");
        session.removeAttribute("pauseTime");
        session.removeAttribute("resumeTime");
        session.removeAttribute("endTime");
        session.removeAttribute("breakOpen");
        return "redirect:/clock-in";
    }

    private String formatDate(String rawDateTime) {
        if (rawDateTime == null) {
            return null;
        }
        LocalDateTime dt = LocalDateTime.parse(rawDateTime);
        return dt.format(FORMATTER);
    }
}