package com.miproyecto.clienterest.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AuthResponseDTO;
import com.miproyecto.clienterest.dto.LoginDTO;

@Service
public class AuthService {

    private final RestClient restClient;

    public AuthService(@Qualifier("apiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<AuthResponseDTO> login(String username, String pass) {

        LoginDTO body = new LoginDTO(username, pass);

        return restClient.post()
                .uri("/api/auth/login")
                .body(body)
                .retrieve()
                .toEntity(AuthResponseDTO.class);
    }
}
