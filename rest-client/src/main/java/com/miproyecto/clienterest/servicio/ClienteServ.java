package com.miproyecto.clienterest.servicio;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.UsuarioDto;

@Service
public class ClienteServ {

    private final RestClient restClient;

    @Value("${api.base-url}")
    private String baseUrl;


    public ClienteServ(RestClient restClient) {
        this.restClient = restClient;
    }


    public List<UsuarioDto> obtenerUsuarios() {

        UsuarioDto[] usuarios = restClient
                .get()
                .uri(baseUrl + "/api/json")
                .retrieve()
                .body(UsuarioDto[].class);


        if (usuarios == null) {
            return List.of();
        }

        return Arrays.asList(usuarios);
    }
}