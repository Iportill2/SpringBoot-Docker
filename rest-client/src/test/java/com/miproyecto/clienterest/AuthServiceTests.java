package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AuthResponseDTO;
import com.miproyecto.clienterest.service.AuthService;

class AuthServiceTests {

    @AfterEach
    void cleanup() {
        RestClientTestSupport.clearSession();
    }

    @Test
    void loginPostsCredentialsAndReturnsToken() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        server.expect(requestTo("http://localhost:8080/api/auth/login"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"username": "testuser", "pass": "password123"}
                        """))
                .andRespond(withSuccess("""
                        {
                          "token": "jwt-nuevo",
                          "userId": 1,
                          "username": "testuser",
                          "email": "testuser@test.com",
                          "role": "EMPLEADO"
                        }
                        """, MediaType.APPLICATION_JSON));

        AuthService service = new AuthService(client);

        ResponseEntity<AuthResponseDTO> response =
                service.login("testuser", "password123");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("jwt-nuevo", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(1, response.getBody().getUserId());

        server.verify();
    }
}
