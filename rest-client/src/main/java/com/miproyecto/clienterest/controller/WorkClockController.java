package com.miproyecto.clienterest.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class WorkClockController {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	
	@GetMapping("/clock-in")
	public String clockInGet(Model model, HttpSession session) {
	    model.addAttribute("username", "Username");
	    model.addAttribute("horaInicio", session.getAttribute("horaInicio"));
	    model.addAttribute("horaPausa", session.getAttribute("horaPausa"));
	    model.addAttribute("horaFin", session.getAttribute("horaFin"));
	    return "app/clock-in";
	}

	@PostMapping("/clock-in/start")
	public String start(HttpSession session) {
	    session.setAttribute("horaInicio", LocalDateTime.now().format(FORMATTER));
	    session.removeAttribute("horaFin");
	    return "redirect:/clock-in";
	}

	@PostMapping("/clock-in/pause")
	public String pause(HttpSession session) {
	    session.setAttribute("horaPausa", LocalDateTime.now().format(FORMATTER));
	    return "redirect:/clock-in";
	}

	@PostMapping("/clock-in/stop")
	public String stop(HttpSession session) {
	    session.setAttribute("horaFin", LocalDateTime.now().format(FORMATTER));
	    return "redirect:/clock-in";
	}
	
	@PostMapping("/clock-in/reset")
	public String reset(HttpSession session) {
	    session.removeAttribute("horaInicio");
	    session.removeAttribute("horaPausa");
	    session.removeAttribute("horaFin");
	    return "redirect:/clock-in";
	}

}
