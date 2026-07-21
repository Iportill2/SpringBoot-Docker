package com.miproyecto.apirest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.service.UserService;



@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/json")
    public List<Users> findAll() {
        return userService.findAll();
    }
}
