package com.miproyecto.clienterest.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.TimeEntryDTO;

@Service
public class TimeEntryService {

    private final RestClient restClient;

    public TimeEntryService(RestClient restClient) {
        this.restClient = restClient;
    }

    public TimeEntryDTO start(Integer userId) {
        return restClient.post()
                .uri("/api/time-entry/start/{userId}", userId)
                .retrieve()
                .body(TimeEntryDTO.class);
    }

    public TimeEntryDTO stop(Integer timeEntryId) {
        return restClient.post()
                .uri("/api/time-entry/stop/{timeEntryId}", timeEntryId)
                .retrieve()
                .body(TimeEntryDTO.class);
    }
}