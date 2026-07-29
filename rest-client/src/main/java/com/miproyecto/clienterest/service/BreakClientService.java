package com.miproyecto.clienterest.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.BreakDTO;

@Service
public class BreakClientService {

    private final RestClient restClient;

    public BreakClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    public BreakDTO start(Integer timeEntryId) {
        return restClient.post()
                .uri("/api/break/start/{timeEntryId}", timeEntryId)
                .retrieve()
                .body(BreakDTO.class);
    }

    public BreakDTO end(Integer breakId) {
        return restClient.post()
                .uri("/api/break/end/{breakId}", breakId)
                .retrieve()
                .body(BreakDTO.class);
    }
}