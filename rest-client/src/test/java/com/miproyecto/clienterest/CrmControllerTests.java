package com.miproyecto.clienterest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.clienterest.controller.CrmController;
import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.dto.ClienteDTO;
import com.miproyecto.clienterest.dto.TareaDTO;
import com.miproyecto.clienterest.service.CrmService;
import com.miproyecto.clienterest.service.ClienteService;

@WebMvcTest(controllers = CrmController.class)
class CrmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrmService crmService;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    void crmGetAsAdminShowsAllTareas() throws Exception {
        TareaDTO tarea = new TareaDTO();
        tarea.setId(1);
        tarea.setTitulo("Tarea admin");

        when(crmService.findAllTareas()).thenReturn(List.of(tarea));
        when(crmService.findAllUsuarios()).thenReturn(List.of());
        when(clienteService.findAllClientes()).thenReturn(List.of());

        mockMvc.perform(get("/menu/crm")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("app/crm"))
                .andExpect(model().attribute("tareas", List.of(tarea)))
                .andExpect(model().attributeExists("clientes"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("isAdmin", true));
    }

    @Test
    void crmGetAsEmployeeShowsOnlyOwnTareas() throws Exception {
        when(crmService.findTareasByResponsable(7)).thenReturn(List.of());
        when(clienteService.findAllClientes()).thenReturn(List.of());

        mockMvc.perform(get("/menu/crm")
                        .sessionAttr("userId", 7)
                        .sessionAttr("role", "EMPLEADO"))
                .andExpect(status().isOk())
                .andExpect(view().name("app/crm"))
                .andExpect(model().attribute("isAdmin", false));

        verify(crmService).findTareasByResponsable(7);
    }

    @Test
    void crmGetWithoutRoleShowsCrmView() throws Exception {
        when(crmService.findTareasByResponsable(1)).thenReturn(List.of());
        when(crmService.findTareasSinAsignar()).thenReturn(List.of());
        when(clienteService.findAllClientes()).thenReturn(List.of());

        mockMvc.perform(get("/menu/crm")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("app/crm"))
                .andExpect(model().attribute("isAdmin", false));

        verify(crmService).findTareasByResponsable(1);
    }

    @Test
    void crearRedirectsAndCallsService() throws Exception {
        mockMvc.perform(post("/menu/crm/crear")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN")
                        .param("titulo", "Nueva")
                        .param("cliente.id", "3")
                        .param("responsable.id", "4")
                        .param("estado", "PENDIENTE")
                        .param("prioridad", "ALTA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/crm"))
                .andExpect(flash().attribute("message", "Tarea creada correctamente"));

        verify(crmService).crearTarea(any(TareaDTO.class));
    }

    @Test
    void editarRedirectsAndCallsService() throws Exception {
        TareaDTO existing = new TareaDTO();
        existing.setId(5);
        existing.setTitulo("Original");
        existing.setEstado("PENDIENTE");
        existing.setPrioridad("MEDIA");

        when(crmService.findTareaById(5)).thenReturn(existing);

        mockMvc.perform(post("/menu/crm/editar/5")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN")
                        .param("titulo", "Editada")
                        .param("estado", "COMPLETADA")
                        .param("prioridad", "BAJA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/crm"))
                .andExpect(flash().attribute("message", "Tarea actualizada"));

        verify(crmService).actualizarTarea(eq(5), any(TareaDTO.class));
    }

    @Test
    void eliminarRedirectsAndCallsService() throws Exception {
        mockMvc.perform(post("/menu/crm/eliminar/9")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/crm"))
                .andExpect(flash().attribute("message", "Tarea eliminada correctamente"));

        verify(crmService).eliminarTarea(9);
    }
}
