package com.miproyecto.clienterest.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

	@GetMapping("/calendar")
	public String calendarGet(
	        @RequestParam(required = false) Integer year,
	        @RequestParam(required = false) Integer month,
	        Model model) {

	    YearMonth current = (year != null && month != null)
	            ? YearMonth.of(year, month)
	            : YearMonth.now();

	    LocalDate firstDay = current.atDay(1);
	    int startOffset = firstDay.getDayOfWeek().getValue() - 1;

	    Map<Integer, String> hoursByDay = new LinkedHashMap<>();
	    hoursByDay.put(1, "7h 45m");
	    hoursByDay.put(2, "8h 00m");
	    hoursByDay.put(3, "7h 30m");
	    hoursByDay.put(6, "8h 15m");
	    hoursByDay.put(7, "7h 50m");
	    hoursByDay.put(8, "8h 00m");
	    hoursByDay.put(22, "5h 20m");

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