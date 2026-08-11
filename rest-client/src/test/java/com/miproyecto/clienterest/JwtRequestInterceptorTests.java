package com.miproyecto.clienterest;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.config.JwtRequestInterceptor;

class JwtRequestInterceptorTests {

    @AfterEach
    void cleanup() {
        RestClientTestSupport.clearSession();
    }

    @Test
    void addsBearerTokenWhenSessionHasJwt() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("mi-token");

        server.expect(requestTo("http://localhost:8080/api/user"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer mi-token"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.get().uri("/api/user").retrieve().toBodilessEntity();

        server.verify();
    }

    @Test
    void doesNotAddHeaderWhenNoSession() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        server.expect(requestTo("http://localhost:8080/api/user"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(headerDoesNotExist("Authorization"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.get().uri("/api/user").retrieve().toBodilessEntity();

        server.verify();
    }

    @Test
    void doesNotAddHeaderWhenSessionTokenIsBlank() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("   ");

        server.expect(requestTo("http://localhost:8080/api/user"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(headerDoesNotExist("Authorization"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.get().uri("/api/user").retrieve().toBodilessEntity();

        server.verify();
    }

    @Test
    void interceptorCanBeUsedStandalone() {
        JwtRequestInterceptor interceptor = new JwtRequestInterceptor();

        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = builder.requestInterceptor(interceptor).build();

        RestClientTestSupport.loginSession("token-x");

        server.expect(requestTo("http://localhost:8080/api/user"))
                .andExpect(header("Authorization", "Bearer token-x"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.get().uri("/api/user").retrieve().toBodilessEntity();

        server.verify();
    }
}
