package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminController {

	@GetMapping("/menu-admin")
	public String getAdminPage() {
		
		return "app/menu-admin";
	}
	
}
