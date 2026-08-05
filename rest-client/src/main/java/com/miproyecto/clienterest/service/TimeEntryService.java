package com.miproyecto.clienterest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.TimeEntryDTO;

@Service
public class TimeEntryService {

    private final RestClient restClient;

    public TimeEntryService(@Qualifier("apiRestClient") RestClient restClient) {
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
    
    public List<TimeEntryDTO> findByMonth(Integer userId, int year, int month) {
        return restClient.get()
                .uri("/api/time-entry/user/{userId}?year={year}&month={month}", userId, year, month)
                .retrieve()
                .body(new ParameterizedTypeReference<List<TimeEntryDTO>>() {});
    }
}