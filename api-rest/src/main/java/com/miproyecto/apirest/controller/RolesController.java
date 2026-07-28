package com.miproyecto.apirest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.service.RolesService;

@RestController
@RequestMapping("/api/roles")
public class RolesController {

    private final RolesService roleServ;

    public RolesController(RolesService rolesService) {
        this.roleServ = rolesService;
    }

    @GetMapping
    public ResponseEntity<List<Roles>> findAll() {

        List<Roles> temp = roleServ.findAll();

        if (temp == null || temp.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(temp);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Roles> findById(@PathVariable Integer id) {

        if (id == null)
            return ResponseEntity.badRequest().build();

        Optional<Roles> temp = roleServ.findById(id);

        if (temp.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(temp.get());
    }


    @PostMapping
    public ResponseEntity<Roles> create(@RequestBody Roles role) {

        if (role == null)
            return ResponseEntity.badRequest().build();

        Roles temp = roleServ.create(role);

        return ResponseEntity.ok(temp);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {

        if (id == null)
            return ResponseEntity.badRequest().build();

        Boolean temp = roleServ.delete(id);

        return ResponseEntity.ok(temp);
    }


    @GetMapping("/name/{name}")
    public ResponseEntity<Roles> findByName(@PathVariable String name) {

        if (name == null || name.isBlank())
            return ResponseEntity.badRequest().build();

        Roles temp = roleServ.findByName(name);

        if (temp == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(temp);
    }
}