package com.miproyecto.clienterest.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.service.AdminService;

@Controller
@RequestMapping("/menu-admin")
public class AdminController {

    private final AdminService adminServ;

    public AdminController(AdminService adminServ) {
        this.adminServ = adminServ;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<AdminUserDTO> users = adminServ.findPendingUsers();
        model.addAttribute("users", users);
        return "app/menu-admin";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Integer id) {
        
    	adminServ.approve(id);

        return "redirect:/menu-admin";
    }

    @PostMapping("/block/{id}")
    public String block(@PathVariable Integer id) {
        
    	adminServ.block(id);
    	
        return "redirect:/menu-admin";
    }

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
    
		adminServ.delete(id);

    return "redirect:/menu-admin";
	}
}