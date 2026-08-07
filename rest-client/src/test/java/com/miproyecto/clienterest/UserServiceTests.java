package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.QuestionDTO;
import com.miproyecto.clienterest.dto.UsersDTO;
import com.miproyecto.clienterest.service.UserService;

class UserServiceTests {

    private static final String BASE = "http://localhost:8080";

    @AfterEach
    void cleanup() {
        RestClientTestSupport.clearSession();
    }

    @Test
    void createPostsUserAndReturnsCreated() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer token-abc"))
                .andExpect(content().json("""
                        {"username": "nuevo", "pass": "pass123", "email": "nuevo@test.com"}
                        """))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"id": 1, "username": "nuevo", "pass": "pass123",
                                 "email": "nuevo@test.com", "role": null}
                                """));

        UserService service = new UserService(client);

        UsersDTO dto = new UsersDTO();
        dto.setUsername("nuevo");
        dto.setPass("pass123");
        dto.setEmail("nuevo@test.com");

        ResponseEntity<UsersDTO> response = service.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("nuevo", response.getBody().getUsername());

        server.verify();
    }

    @Test
    void findAllGetsUsersWithBearerToken() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-abc"))
                .andRespond(withSuccess("""
                        [{"id": 1, "username": "testuser", "email": "testuser@test.com", "role": null}]
                        """, MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        ResponseEntity<List<UsersDTO>> response = service.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());

        server.verify();
    }

    @Test
    void findByIdCallsCorrectUri() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user/7"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id": 7, "username": "testuser", "email": "testuser@test.com", "role": null}
                        """, MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        ResponseEntity<UsersDTO> response = service.findById(7);

        assertEquals(7, response.getBody().getId());

        server.verify();
    }

    @Test
    void findByUsernameCallsCorrectUri() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user/name/testuser"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id": 1, "username": "testuser", "email": "testuser@test.com", "role": null}
                        """, MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        ResponseEntity<UsersDTO> response = service.findByUsername("testuser");

        assertEquals("testuser", response.getBody().getUsername());

        server.verify();
    }

    @Test
    void existsByUsernameReturnsBoolean() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user/name/exist/testuser"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        ResponseEntity<Boolean> response = service.existsByUsername("testuser");

        assertTrue(Boolean.TRUE.equals(response.getBody()));

        server.verify();
    }

    @Test
    void isBlockedReturnsBoolean() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user/blocked/testuser"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("false", MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        ResponseEntity<Boolean> response = service.isBlocked("testuser");

        assertTrue(Boolean.FALSE.equals(response.getBody()));

        server.verify();
    }

    @Test
    void updatePutsUser() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user/1"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("""
                        {"id": 1, "username": "modificado", "email": "testuser@test.com", "role": null}
                        """, MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        UsersDTO dto = new UsersDTO();
        dto.setUsername("modificado");

        ResponseEntity<UsersDTO> response = service.update(1, dto);

        assertEquals("modificado", response.getBody().getUsername());

        server.verify();
    }

    @Test
    void deleteSendsDeleteRequest() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/user/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        ResponseEntity<Boolean> response = service.delete(1);

        assertTrue(Boolean.TRUE.equals(response.getBody()));

        server.verify();
    }

    @Test
    void saveQuestionPostsFromDto() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/userquestion/from-dto"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"userId": 1, "questionId": 2, "answer": "mi respuesta"}
                        """))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"id": 9, "answer": "mi respuesta"}
                                """));

        UserService service = new UserService(client);

        ResponseEntity<?> response = service.saveQuestion(1, 2, "mi respuesta");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        server.verify();
    }

    @Test
    void findAllQuestionsGetsPublicQuestions() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/questions"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id": 1, "text": "¿Pregunta?"}]
                        """, MediaType.APPLICATION_JSON));

        UserService service = new UserService(client);

        ResponseEntity<List<QuestionDTO>> response =
                service.findAllQuestions();

        assertEquals(1, response.getBody().size());
        assertEquals("¿Pregunta?", response.getBody().get(0).getText());

        server.verify();
    }
}
