package com.miproyecto.clienterest.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.UsersDTO;

@Service
public class UserService {

    private final RestClient restClient;

    public UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<UsersDTO> create(UsersDTO user) {

    	System.out.println("ENVIANDO: " + user);
        return restClient.post()
                .uri("/api/user")
                .body(user)
                .retrieve()
                .toEntity(UsersDTO.class);
    }
}
