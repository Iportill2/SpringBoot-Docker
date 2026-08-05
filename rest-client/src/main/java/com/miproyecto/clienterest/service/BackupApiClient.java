package com.miproyecto.clienterest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.model.BackupInfo;


@Service
public class BackupApiClient {


    private final RestClient restClient;


    public BackupApiClient(
            @Qualifier("backupRestClient") RestClient restClient) {

        this.restClient = restClient;
    }
    



    public String createBackup() {

        return restClient.post()
                .uri("/backups")
                .retrieve()
                .body(String.class);
    }



    public List<BackupInfo> listBackups() {

        BackupInfo[] response =
                restClient.get()
                .uri("/backups")
                .retrieve()
                .body(BackupInfo[].class);


        return Arrays.asList(response);
    }

}