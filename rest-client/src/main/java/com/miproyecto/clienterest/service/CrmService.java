package com.miproyecto.clienterest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.dto.ClienteDTO;
import com.miproyecto.clienterest.dto.TareaDTO;

@Service
public class CrmService {

    private final RestClient restClient;

    public CrmService(@Qualifier("apiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<TareaDTO> findAllTareas() {
        return restClient.get()
                .uri("/api/tarea")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TareaDTO>>() {
                });
    }

    public List<TareaDTO> findTareasByResponsable(Integer responsableId) {
        return restClient.get()
                .uri("/api/tarea?responsableId={responsableId}", responsableId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<TareaDTO>>() {
                });
    }

    public List<TareaDTO> findTareasSinAsignar() {
        return restClient.get()
                .uri("/api/tarea?sinAsignar=true")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TareaDTO>>() {
                });
    }

    public List<ClienteDTO> findAllClientes() {
        return restClient.get()
                .uri("/api/cliente")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ClienteDTO>>() {
                });
    }

    public List<AdminUserDTO> findAllUsuarios() {
        return restClient.get()
                .uri("/api/user")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AdminUserDTO>>() {
                });
    }

    public TareaDTO crearTarea(TareaDTO tarea) {
        return restClient.post()
                .uri("/api/tarea")
                .body(tarea)
                .retrieve()
                .body(TareaDTO.class);
    }

    public TareaDTO actualizarTarea(Integer id, TareaDTO tarea) {
        return restClient.put()
                .uri("/api/tarea/{id}", id)
                .body(tarea)
                .retrieve()
                .body(TareaDTO.class);
    }

    public TareaDTO asignarTarea(Integer id, Integer userId) {
        return restClient.post()
                .uri("/api/tarea/{id}/asignar/{userId}", id, userId)
                .retrieve()
                .body(TareaDTO.class);
    }

    public TareaDTO actualizarHoras(Integer id, Double horas) {
        Map<String, Object> body = new HashMap<>();
        body.put("horasEmpleadas", horas);
        return restClient.put()
                .uri("/api/tarea/{id}/horas", id)
                .body(body)
                .retrieve()
                .body(TareaDTO.class);
    }

    public Boolean eliminarTarea(Integer id) {
        return restClient.delete()
                .uri("/api/tarea/{id}", id)
                .retrieve()
                .body(Boolean.class);
    }
}
