package com.miproyecto.clienterest.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.miproyecto.clienterest.dto.TimeEntryDTO;
import com.miproyecto.clienterest.service.TimeEntryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class CalendarController {
	
    private final TimeEntryService timeEntryService;

    public CalendarController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }


	@GetMapping("/calendar")
	public String calendarGet(
	        @RequestParam(required = false) Integer year,
	        @RequestParam(required = false) Integer month,
	        Model model,
	        HttpSession session) {

	    YearMonth current = (year != null && month != null)
	            ? YearMonth.of(year, month)
	            : YearMonth.now();

	    LocalDate firstDay = current.atDay(1);
	    int startOffset = firstDay.getDayOfWeek().getValue() - 1;

	    Integer userId = (Integer) session.getAttribute("userId");
	    List<TimeEntryDTO> entries = timeEntryService.findByMonth(userId, current.getYear(), current.getMonthValue());

	    Map<Integer, String> hoursByDay = new LinkedHashMap<>();
	    for (TimeEntryDTO entry : entries) {
	        if (entry.getTotalMinutesWorked() != null) {
	            int day = LocalDate.parse(entry.getDate()).getDayOfMonth();
	            int hours = entry.getTotalMinutesWorked() / 60;
	            int minutes = entry.getTotalMinutesWorked() % 60;
	            hoursByDay.put(day, hours + "h " + minutes + "m");
	        }
	    }

	    YearMonth previous = current.minusMonths(1);
	    YearMonth next = current.plusMonths(1);

	    model.addAttribute("monthLabel", current.getMonth() + " " + current.getYear());
	    model.addAttribute("daysInMonth", current.lengthOfMonth());
	    model.addAttribute("startOffset", startOffset);
	    model.addAttribute("hoursByDay", hoursByDay);
	    model.addAttribute("prevYear", previous.getYear());
	    model.addAttribute("prevMonth", previous.getMonthValue());
	    model.addAttribute("nextYear", next.getYear());
	    model.addAttribute("nextMonth", next.getMonthValue());

	    return "app/calendar";
	}
}