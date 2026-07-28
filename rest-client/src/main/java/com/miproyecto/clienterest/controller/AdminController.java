package com.miproyecto.clienterest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.service.AdminService;

@Controller
@RequestMapping("/menu-admin")
public class AdminController {

    private final RestClient restClient;
    private final AdminService adminServ;

    public AdminController(RestClient restClient, AdminService adminServ) {
        this.restClient = restClient;
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
        restClient.patch()
                .uri("/api/user/{id}", id)
                .body(Map.of("role", Map.of("id", 2)))
                .retrieve()
                .toBodilessEntity();

        return "redirect:/menu-admin";
    }

    @PostMapping("/block/{id}")
    public String block(@PathVariable Integer id) {
        restClient.put()
                .uri("/api/user/{id}", id)
                .body(Map.of("blocked", true))
                .retrieve()
                .toBodilessEntity();

        return "redirect:/menu-admin";
    }

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
    restClient.delete()
            .uri("/api/user/{id}", id)
            .retrieve()
            .toBodilessEntity();

    return "redirect:/menu-admin";
}
}