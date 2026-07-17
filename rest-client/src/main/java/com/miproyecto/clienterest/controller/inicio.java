package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class inicio {

	@GetMapping("/")
    public String base() {
        return "login";
    }
	@GetMapping("/info")
	@ResponseBody
	public String info(HttpServletRequest request) {

	    return """
	        remoteAddr = %s

	        X-Real-IP = %s

	        X-Forwarded-For = %s
	        """.formatted(
	            request.getRemoteAddr(),
	            request.getHeader("X-Real-IP"),
	            request.getHeader("X-Forwarded-For")
	    );
	}
}
