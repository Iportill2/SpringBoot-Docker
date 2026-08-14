package com.miproyecto.clienterest.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miproyecto.clienterest.dto.BreakDTO;
import com.miproyecto.clienterest.dto.TimeEntryDTO;
import com.miproyecto.clienterest.service.BreakClientService;
import com.miproyecto.clienterest.service.TimeEntryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu/clock-in")
public class WorkClockController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TimeEntryService timeEntryService;
    private final BreakClientService breakClientService;

    public WorkClockController(TimeEntryService timeEntryService, BreakClientService breakClientService) {
        this.timeEntryService = timeEntryService;
        this.breakClientService = breakClientService;
    }

    @GetMapping
    public String clockInGet(Model model, HttpSession session) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("startTime", session.getAttribute("startTime"));
        model.addAttribute("pauseTime", session.getAttribute("pauseTime"));
        model.addAttribute("resumeTime", session.getAttribute("resumeTime"));
        model.addAttribute("endTime", session.getAttribute("endTime"));
        model.addAttribute("breakOpen", session.getAttribute("breakOpen"));

        String lastEntryDate = (String) session.getAttribute("lastEntryDate");
        boolean finishedToday = lastEntryDate != null
                && lastEntryDate.equals(java.time.LocalDate.now().toString());
        model.addAttribute("finishedToday", finishedToday);

        return "app/clock-in";
    }

    @PostMapping("/start")
    public String start(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        TimeEntryDTO entry = timeEntryService.start(userId);

        session.setAttribute("timeEntryId", entry.getId());
        session.setAttribute("startTime", formatDate(entry.getStartTime()));
        session.setAttribute("breakOpen", false);
        session.removeAttribute("endTime");
        session.removeAttribute("pauseTime");
        session.removeAttribute("resumeTime");

        return "redirect:/menu/clock-in";
    }

    @PostMapping("/pause")
    public String pause(HttpSession session) {
        String pauseStart = breakClientService.start();
        session.setAttribute("pauseTime", formatDate(pauseStart));
        session.setAttribute("pauseStartRaw", pauseStart);
        session.setAttribute("breakOpen", true);

        return "redirect:/menu/clock-in";
    }

    @PostMapping("/resume")
    public String resume(HttpSession session) {
        String pauseStartRaw = (String) session.getAttribute("pauseStartRaw");
        String pauseEndRaw = breakClientService.end();

        if (pauseStartRaw != null) {
            LocalDateTime start = LocalDateTime.parse(pauseStartRaw);
            LocalDateTime end = LocalDateTime.parse(pauseEndRaw);
            long minutes = Duration.between(start, end).toMinutes();

            Integer totalPauseMinutes = (Integer) session.getAttribute("totalPauseMinutes");
            totalPauseMinutes = (totalPauseMinutes == null ? 0 : totalPauseMinutes) + (int) minutes;
            session.setAttribute("totalPauseMinutes", totalPauseMinutes);
        }

        session.setAttribute("resumeTime", formatDate(pauseEndRaw));
        session.removeAttribute("pauseStartRaw");
        session.setAttribute("breakOpen", false);

        return "redirect:/menu/clock-in";
    }

    @PostMapping("/stop")
    public String stop(HttpSession session) {
        Integer timeEntryId = (Integer) session.getAttribute("timeEntryId");
        Integer totalPauseMinutes = (Integer) session.getAttribute("totalPauseMinutes");
        int pauseMinutes = totalPauseMinutes == null ? 0 : totalPauseMinutes;

        session.setAttribute("breakOpen", false);

        TimeEntryDTO entry = timeEntryService.stop(timeEntryId, pauseMinutes);

        session.setAttribute("endTime", formatDate(entry.getEndTime()));
        session.setAttribute("lastEntryDate", entry.getDate());

        return "redirect:/menu/clock-in";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute("timeEntryId");
        session.removeAttribute("breakId");
        session.removeAttribute("startTime");
        session.removeAttribute("pauseTime");
        session.removeAttribute("resumeTime");
        session.removeAttribute("endTime");
        session.removeAttribute("breakOpen");
        return "redirect:/menu/clock-in";
    }

    private String formatDate(String rawDateTime) {
        if (rawDateTime == null) {
            return null;
        }
        LocalDateTime dt = LocalDateTime.parse(rawDateTime);
        return dt.format(FORMATTER);
    }
}