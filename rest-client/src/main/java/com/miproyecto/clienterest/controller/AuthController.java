package com.miproyecto.clienterest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miproyecto.clienterest.dto.LoginDTO;
import com.miproyecto.clienterest.dto.QuestionsDTO;
import com.miproyecto.clienterest.dto.RegisterDTO;
import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.UserService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/")
public class AuthController {

	private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }
	
	@GetMapping({"/", "/login"})
	public String loginGet(Model model) {
		
		model.addAttribute("loginDTO", new LoginDTO());

		return "auth/login";
	}
	
	@PostMapping("/login")
	public String loginPost(@Valid @ModelAttribute LoginDTO loginDTO, BindingResult result) {
	    
		
		//VALIDACION 
		if (result.hasErrors()) {
			return "auth/login";
		}
		
	    return "/layout/base-app";
	}
	
	@GetMapping("/register")
	public String registerGet(Model model) {
		
		model.addAttribute("usersDTO", new UsersDTO());

		return "auth/register";
	}
	
	@PostMapping("/register")
	public String registerPost(@Valid @ModelAttribute UsersDTO usersDTO, BindingResult result) {
	    
		
		
		ResponseEntity<?> temp = userService.create(usersDTO);
		
		
		
		if (result.hasErrors()) {
			return "auth/register";
		}
		
		
	    return "redirect:/register-2";
	}
	
	@GetMapping("/register-2")
	public String register2(Model model){
		
		model.addAttribute("questionDTO", new QuestionsDTO());

		return "auth/register-2";
	}
	@PostMapping("/register-2")
	public String registerPost2(@Valid @ModelAttribute QuestionsDTO questionsDTO, BindingResult result) {
	    
		
		
		if (result.hasErrors()) {
			return "auth/register-2";
		}
		
	    return "redirect:/login";
	}
	
	
}
