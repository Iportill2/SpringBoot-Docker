package com.miproyecto.apirest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.model.Cliente;
import com.miproyecto.apirest.service.ClienteService;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService clienteServ;

    public ClienteController(ClienteService clienteServ) {
        this.clienteServ = clienteServ;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> findAll() {
        List<Cliente> temp = clienteServ.findAll();
        if (temp == null || temp.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(temp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable Integer id) {
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().build();
        }
        Cliente temp = clienteServ.findById(id);
        if (temp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(temp);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cliente> create(@RequestBody Cliente cliente) {
        Cliente temp = clienteServ.create(cliente);
        if (temp == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(temp);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cliente> update(@PathVariable Integer id, @RequestBody Cliente cliente) {
        if (id == null || id < 1 || cliente == null) {
            return ResponseEntity.badRequest().build();
        }
        Cliente temp = clienteServ.update(id, cliente);
        if (temp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(temp);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().build();
        }
        Boolean deleted = clienteServ.delete(id);
        if (deleted == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deleted);
    }
}
