package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.service.BreakClientService;

class BreakClientServiceTests {

    private static final String BASE = "http://localhost:8080";

    @AfterEach
    void cleanup() {
        RestClientTestSupport.clearSession();
    }

    @Test
    void startPostsStartUri() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/break/start"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer token-abc"))
                .andRespond(withSuccess("""
                        {"time": "2026-08-07T12:00:00"}
                        """, MediaType.APPLICATION_JSON));

        BreakClientService service = new BreakClientService(client);

        String result = service.start();

        assertEquals("2026-08-07T12:00:00", result);

        server.verify();
    }

    @Test
    void endPostsEndUri() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/break/end"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"time": "2026-08-07T12:30:00"}
                        """, MediaType.APPLICATION_JSON));

        BreakClientService service = new BreakClientService(client);

        String result = service.end();

        assertEquals("2026-08-07T12:30:00", result);

        server.verify();
    }
}