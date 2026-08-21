package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.apirest.model.Cliente;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Tarea;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.ClienteRepository;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.TareaRepository;
import com.miproyecto.apirest.repository.UsersRepository;
import com.miproyecto.apirest.security.JwtService;

@ApiRestTest
class TareaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaRepository tareaRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private JwtService jwtService;

    @Test
    void createRequiresAuthAndReturnsTarea() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Cliente cliente = clienteRepo.save(new Cliente(null, "Acme"));

        mockMvc.perform(post("/api/tarea")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Revisar contrato",
                                  "descripcion": "Revisar cláusulas",
                                  "cliente": {"id": %d},
                                  "responsable": {"id": %d},
                                  "estado": "PENDIENTE",
                                  "prioridad": "ALTA",
                                  "fechaLimite": "2026-09-01",
                                  "horasEmpleadas": 2.5
                                }
                                """.formatted(cliente.getId(), user.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Revisar contrato"))
                .andExpect(jsonPath("$.cliente.nombre").value("Acme"))
                .andExpect(jsonPath("$.responsable.username").value("testuser"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void createWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/tarea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": "Tarea", "estado": "PENDIENTE", "prioridad": "MEDIA"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithUnknownResponsableReturns400() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/tarea")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Tarea",
                                  "responsable": {"id": 99999},
                                  "estado": "PENDIENTE",
                                  "prioridad": "MEDIA"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllReturnsTareas() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        saveTarea(user, "Tarea 1");
        saveTarea(user, "Tarea 2");

        mockMvc.perform(get("/api/tarea")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").isNotEmpty());
    }

    @Test
    void findAllByResponsableReturnsOnlyAssigned() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        saveTarea(user, "Solo mía");

        mockMvc.perform(get("/api/tarea")
                        .param("responsableId", user.getId().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Solo mía"))
                .andExpect(jsonPath("$[0].responsable.username").value("testuser"));
    }

    @Test
    void findByIdReturnsTarea() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Tarea tarea = saveTarea(user, "Detalle");

        mockMvc.perform(get("/api/tarea/{id}", tarea.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Detalle"));
    }

    @Test
    void updateTareaReturnsUpdated() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Tarea tarea = saveTarea(user, "Antes");

        mockMvc.perform(put("/api/tarea/{id}", tarea.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Después",
                                  "estado": "COMPLETADA",
                                  "prioridad": "BAJA",
                                  "horasEmpleadas": 4
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Después"))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void deleteTareaReturnsTrue() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Tarea tarea = saveTarea(user, "ParaBorrar");

        mockMvc.perform(delete("/api/tarea/{id}", tarea.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    private Tarea saveTarea(Users user, String titulo) {
        Tarea tarea = new Tarea();
        tarea.setTitulo(titulo);
        tarea.setEstado(Tarea.Estado.PENDIENTE);
        tarea.setPrioridad(Tarea.Prioridad.MEDIA);
        tarea.setResponsable(user);
        tarea.setFechaLimite(LocalDate.now());
        return tareaRepo.save(tarea);
    }

    private Users saveUser(String username) {
        Roles role = roleRepo.findById(1).orElseThrow();
        Users user = new Users();
        user.setUsername(username);
        user.setPass("password123");
        user.setEmail(username + "@test.com");
        user.setCode("code-" + username);
        user.setSalt("salt");
        user.setRole(role);
        return userRepo.save(user);
    }
}
