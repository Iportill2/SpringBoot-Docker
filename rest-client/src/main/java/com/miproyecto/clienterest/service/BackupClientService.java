package com.miproyecto.clienterest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.model.BackupInfo;


@Service
public class BackupClientService {


    private final RestClient restClient;


    public BackupClientService(
            @Qualifier("backupRestClient") RestClient restClient) {

        this.restClient = restClient;
    }


    public List<BackupInfo> listBackups() {

        return restClient.get()
                .uri("/backups")
                .retrieve()
                .body(new ParameterizedTypeReference<List<BackupInfo>>() {});
    }


    public String createBackup() {

        return restClient.post()
                .uri("/backups")
                .retrieve()
                .body(String.class);
    }
    public Boolean restoreBackup(String fileName) {

        return restClient.post()
                .uri("/backups/restore/{file}", fileName)
                .retrieve()
                .body(Boolean.class);
    }
}