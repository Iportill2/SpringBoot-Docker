package com.miproyecto.clienterest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.ClienteDTO;

@Service
public class ClienteService {

    private final RestClient restClient;

    public ClienteService(@Qualifier("apiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ClienteDTO> findAllClientes() {
        return restClient.get()
                .uri("/api/cliente")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ClienteDTO>>() {
                });
    }

    public ClienteDTO create(ClienteDTO clienteDTO) {
        return restClient.post()
                .uri("api/cliente")
                .body(clienteDTO)
                .retrieve()
                .body(new ParameterizedTypeReference<ClienteDTO>() {
                });
    }

    public ClienteDTO update(Integer id, ClienteDTO clienteDTO) {
    return restClient.put()
            .uri("/api/cliente/{id}", id)
            .body(clienteDTO)
            .retrieve()
            .body(new ParameterizedTypeReference<ClienteDTO>() {
            });
    }

    public boolean deleteCliente(Integer id) {
        restClient.delete()
                .uri("/api/cliente/{id}", id)
                .retrieve()
                .toBodilessEntity();
        return true;
    }

}
