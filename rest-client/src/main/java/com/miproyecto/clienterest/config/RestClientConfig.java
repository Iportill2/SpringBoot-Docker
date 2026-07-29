package com.miproyecto.clienterest.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {


    @Value("${api.base-url}")
    private String apiUrl;


    @Value("${backup.base-url}")
    private String backupUrl;



    @Bean
    @Qualifier("apiRestClient")
    public RestClient apiRestClient() {

        return RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }



    @Bean
    @Qualifier("backupRestClient")
    public RestClient backupRestClient() {

        return RestClient.builder()
                .baseUrl(backupUrl)
                .build();
    }

}