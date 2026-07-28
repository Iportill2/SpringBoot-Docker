package com.miproyecto.clienterest.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AdminUserDTO;

@Service
public class AdminService {
	
    private final RestClient restClient;

    public AdminService(RestClient restClient) {
        this.restClient = restClient;
    }

	public List<AdminUserDTO> findPendingUsers() {
	    ResponseEntity<List<AdminUserDTO>> response = restClient.get()
	            .uri("/api/user")
	            .retrieve()
	            .toEntity(new ParameterizedTypeReference<List<AdminUserDTO>>() {});

	    List<AdminUserDTO> allUsers = response.getBody();

	    return allUsers.stream()
	            .filter(u -> u.getRole() == null || u.getRole().getId() == 3)
	            .toList();
	}
}
