package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/menu")
public class MenuController {

	
	@GetMapping("")
	public String getMenu() {
		return "layout/base-app";
	}
	
}
