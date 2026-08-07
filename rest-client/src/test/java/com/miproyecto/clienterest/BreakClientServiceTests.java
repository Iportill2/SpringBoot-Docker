package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import com.miproyecto.clienterest.dto.BreakDTO;
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

        server.expect(requestTo(BASE + "/api/break/start/3"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer token-abc"))
                .andRespond(withSuccess("""
                        {"id": 1, "startTime": "2026-08-07T12:00:00", "endTime": null}
                        """, MediaType.APPLICATION_JSON));

        BreakClientService service = new BreakClientService(client);

        BreakDTO result = service.start(3);

        assertEquals(1, result.getId());
        assertNotNull(result.getStartTime());

        server.verify();
    }

    @Test
    void endPostsEndUri() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/break/end/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"id": 1, "startTime": "2026-08-07T12:00:00", "endTime": "2026-08-07T12:30:00"}
                        """, MediaType.APPLICATION_JSON));

        BreakClientService service = new BreakClientService(client);

        BreakDTO result = service.end(1);

        assertNotNull(result.getEndTime());

        server.verify();
    }
}
