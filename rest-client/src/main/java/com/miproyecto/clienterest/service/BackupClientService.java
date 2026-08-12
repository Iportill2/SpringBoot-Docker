package com.miproyecto.clienterest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
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


    public String createBackup(String actor) {

        return restClient.post()
                .uri("/backups")
                .header("X-Actor", actor)
                .retrieve()
                .body(String.class);
    }
    public Boolean restoreBackup(String fileName, String actor) {

        return restClient.post()
                .uri("/backups/restore/{fileName}", fileName)
                .header("X-Actor", actor)
                .retrieve()
                .body(Boolean.class);
    }
    public Boolean deleteBackup(String fileName, String actor) {

        return restClient.delete()
                .uri("/backups/{fileName}", fileName)
                .header("X-Actor", actor)
                .retrieve()
                .body(Boolean.class);
    }
    public Resource downloadBackup(String fileName, String actor) {

        return restClient.get()
                .uri("/backups/{fileName}", fileName)
                .header("X-Actor", actor)
                .retrieve()
                .body(Resource.class);
    }

    public String getLog() {

        return restClient.get()
                .uri("/backups/log")
                .retrieve()
                .body(String.class);
    }
}