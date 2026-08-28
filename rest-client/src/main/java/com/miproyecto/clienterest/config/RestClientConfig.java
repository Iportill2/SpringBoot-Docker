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

    @Value("${backup.username:}")
    private String backupUsername;

    @Value("${backup.password:}")
    private String backupPassword;



    @Bean
    @Qualifier("apiRestClient")
    public RestClient apiRestClient(JwtRequestInterceptor jwtRequestInterceptor) {

        return RestClient.builder()
                .baseUrl(apiUrl)
                .requestInterceptor(jwtRequestInterceptor)
                .build();
    }



    @Bean
    @Qualifier("backupRestClient")
    public RestClient backupRestClient() {

        return RestClient.builder()
                .baseUrl(backupUrl)
                .defaultHeaders(headers ->
                        headers.setBasicAuth(backupUsername, backupPassword))
                .build();
    }

}