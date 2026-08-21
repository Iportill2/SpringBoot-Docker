package com.miproyecto.clienterest.service;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class BreakClientService {

    private final RestClient restClient;

public BreakClientService(@Qualifier("apiRestClient") RestClient restClient) {
    this.restClient = restClient;
}

    public String start() {
        Map<String, String> response = restClient.post()
                .uri("/api/break/start")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {});

        return response.get("time");
    }

    public String end() {
        Map<String, String> response = restClient.post()
                .uri("/api/break/end")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {});

        return response.get("time");
    }
}