package com.miproyecto.clienterest.controller;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AdminUserDTO;

@Controller
@RequestMapping("/menu-admin")
public class AdminController {

    private final RestClient restClient;

    public AdminController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<AdminUserDTO> users = restClient.get()
        .uri("/api/user")
        .retrieve()
        .body(new ParameterizedTypeReference<List<AdminUserDTO>>() {});
        model.addAttribute("users", users);
        return "app/menu-admin";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Integer id) {
        AdminUserDTO user = restClient.get()
                .uri("/api/user/{id}", id)
                .retrieve()
                .body(AdminUserDTO.class);

        user.getRole().setId(2);
        user.getRole().setName("user");
        user.setBlocked(false);

        restClient.put()
                .uri("/api/user/{id}", id)
                .body(user)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/menu-admin";
    }

    @PostMapping("/block/{id}")
    public String block(@PathVariable Integer id) {
        AdminUserDTO user = restClient.get()
                .uri("/api/user/{id}", id)
                .retrieve()
                .body(AdminUserDTO.class);

        user.setBlocked(true);

        restClient.put()
                .uri("/api/user/{id}", id)
                .body(user)
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