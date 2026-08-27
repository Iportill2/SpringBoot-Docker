package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.service.AdminService;

class AdminServiceTests {

    private static final String BASE = "http://localhost:8080";

    @AfterEach
    void cleanup() {
        RestClientTestSupport.clearSession();
    }

    @Test
    void findPendingUsersFiltersRoleThreeOrNull() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-admin");

        server.expect(requestTo(BASE + "/api/user"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-admin"))
                .andRespond(withSuccess("""
                        [
                          {"id": 1, "username": "aprobado", "role": {"id": 1, "name": "EMPLEADO"}},
                          {"id": 2, "username": "pendiente", "role": {"id": 3, "name": "PENDIENTE"}},
                          {"id": 3, "username": "sinrol", "role": null}
                        ]
                        """, MediaType.APPLICATION_JSON));

        AdminService service = new AdminService(client);

        List<AdminUserDTO> pending = service.findPendingUsers();

        assertEquals(2, pending.size());
        assertEquals("pendiente", pending.get(0).getUsername());
        assertEquals("sinrol", pending.get(1).getUsername());

        server.verify();
    }

    @Test
    void approvePatchesRoleToAdmin() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-admin");

        server.expect(requestTo(BASE + "/api/user/2"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("""
                        {"role": {"id": 1}}
                        """))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        AdminService service = new AdminService(client);

        assertTrue(service.approve(2));

        server.verify();
    }

    @Test
    void blockPatchesRoleToFour() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-admin");

        server.expect(requestTo(BASE + "/api/user/4"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("""
                        {"role": {"id": 4}}
                        """))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        AdminService service = new AdminService(client);

        assertTrue(service.block(4));

        server.verify();
    }

    @Test
    void reactivatePatchesRoleToOne() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-admin");

        server.expect(requestTo(BASE + "/api/user/4"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("""
                        {"role": {"id": 1}}
                        """))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        AdminService service = new AdminService(client);

        assertTrue(service.reactivate(4));

        server.verify();
    }

    @Test
    void deleteRemovesUserQuestionsAndUser() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-admin");

        server.expect(requestTo(BASE + "/api/userquestion/user/2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id": 10, "answer": "a"}, {"id": 11, "answer": "b"}]
                        """, MediaType.APPLICATION_JSON));

        server.expect(requestTo(BASE + "/api/userquestion/10"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        server.expect(requestTo(BASE + "/api/userquestion/11"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        server.expect(requestTo(BASE + "/api/user/2"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        AdminService service = new AdminService(client);

        assertTrue(service.delete(2));

        server.verify();
    }
}
