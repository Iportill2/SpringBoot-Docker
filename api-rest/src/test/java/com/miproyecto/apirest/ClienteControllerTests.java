package com.miproyecto.apirest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.apirest.model.Cliente;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.ClienteRepository;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;
import com.miproyecto.apirest.security.JwtService;

@ApiRestTest
class ClienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    @Autowired
    private JwtService jwtService;

    @Test
    void createRequiresAuthAndReturnsCliente() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/cliente")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Acme"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Acme"));
    }

    @Test
    void createWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Acme"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findAllReturnsClientes() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        clienteRepo.save(new Cliente(null, "Acme"));
        clienteRepo.save(new Cliente(null, "Globex"));

        mockMvc.perform(get("/api/cliente")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").isNotEmpty());
    }

    @Test
    void findByIdReturnsCliente() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Cliente cliente = clienteRepo.save(new Cliente(null, "Initech"));

        mockMvc.perform(get("/api/cliente/{id}", cliente.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Initech"));
    }

    @Test
    void updateClienteReturnsUpdated() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Cliente cliente = clienteRepo.save(new Cliente(null, "Viejo"));

        mockMvc.perform(put("/api/cliente/{id}", cliente.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Nuevo"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo"));
    }

    @Test
    void deleteClienteReturnsTrue() throws Exception {
        Users user = saveUser("testuser");
        String token = jwtService.generateToken(user);

        Cliente cliente = clienteRepo.save(new Cliente(null, "ParaBorrar"));

        mockMvc.perform(delete("/api/cliente/{id}", cliente.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
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
