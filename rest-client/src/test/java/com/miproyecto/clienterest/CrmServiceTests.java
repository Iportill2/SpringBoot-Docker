package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.ClienteDTO;
import com.miproyecto.clienterest.dto.TareaDTO;
import com.miproyecto.clienterest.service.ClienteService;
import com.miproyecto.clienterest.service.CrmService;

class CrmServiceTests {

    private static final String BASE = "http://localhost:8080";

    @AfterEach
    void cleanup() {
        RestClientTestSupport.clearSession();
    }

    @Test
    void findAllTareasGetsTareas() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/tarea"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-abc"))
                .andRespond(withSuccess("""
                        [{"id": 1, "titulo": "Tarea 1", "estado": "PENDIENTE", "prioridad": "ALTA"}]
                        """, MediaType.APPLICATION_JSON));

        CrmService service = new CrmService(client);

        List<TareaDTO> tareas = service.findAllTareas();

        assertEquals(1, tareas.size());
        assertEquals("Tarea 1", tareas.get(0).getTitulo());

        server.verify();
    }

    @Test
    void findTareasByResponsableUsesQueryParam() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/tarea?responsableId=7"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        CrmService service = new CrmService(client);

        List<TareaDTO> tareas = service.findTareasByResponsable(7);

        assertEquals(0, tareas.size());

        server.verify();
    }

    @Test
    void findAllClientesGetsClientes() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/cliente"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-abc"))
                .andRespond(withSuccess("""
                        [{"id": 1, "nombre": "Acme"}]
                        """, MediaType.APPLICATION_JSON));

        ClienteService service = new ClienteService(client);

        List<ClienteDTO> clientes = service.findAllClientes();

        assertEquals(1, clientes.size());
        assertEquals("Acme", clientes.get(0).getNombre());

        server.verify();
    }

    @Test
    void crearTareaPostsTarea() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/tarea"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"id": 1, "titulo": "Nueva", "estado": "PENDIENTE", "prioridad": "MEDIA"}
                        """, MediaType.APPLICATION_JSON));

        CrmService service = new CrmService(client);

        TareaDTO tarea = new TareaDTO();
        tarea.setTitulo("Nueva");
        tarea.setEstado("PENDIENTE");
        tarea.setPrioridad("MEDIA");

        TareaDTO creada = service.crearTarea(tarea);

        assertNotNull(creada.getId());
        assertEquals("Nueva", creada.getTitulo());

        server.verify();
    }

    @Test
    void actualizarTareaPutsTarea() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/tarea/5"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("""
                        {"id": 5, "titulo": "Editada", "estado": "COMPLETADA", "prioridad": "BAJA"}
                        """, MediaType.APPLICATION_JSON));

        CrmService service = new CrmService(client);

        TareaDTO tarea = new TareaDTO();
        tarea.setTitulo("Editada");
        tarea.setEstado("COMPLETADA");
        tarea.setPrioridad("BAJA");
        tarea.setFechaLimite(LocalDate.of(2026, 9, 1));

        TareaDTO actualizada = service.actualizarTarea(5, tarea);

        assertEquals("Editada", actualizada.getTitulo());
        assertEquals("COMPLETADA", actualizada.getEstado());

        server.verify();
    }

    @Test
    void eliminarTareaDeletesTarea() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/tarea/3"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        CrmService service = new CrmService(client);

        Boolean deleted = service.eliminarTarea(3);

        assertTrue(deleted);

        server.verify();
    }
}
