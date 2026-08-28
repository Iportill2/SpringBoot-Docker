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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.dto.HorasRequest;
import com.miproyecto.apirest.model.Tarea;
import com.miproyecto.apirest.service.TareaService;

@RestController
@RequestMapping("/api/tarea")
public class TareaController {

    private final TareaService tareaServ;

    public TareaController(TareaService tareaServ) {
        this.tareaServ = tareaServ;
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> findAll(
            @RequestParam(required = false) Integer responsableId,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) Tarea.Estado estado,
            @RequestParam(required = false) Boolean sinAsignar) {

        List<Tarea> temp;
        if (Boolean.TRUE.equals(sinAsignar)) {
            temp = tareaServ.findByResponsableIsNull();
        } else if (responsableId != null) {
            temp = tareaServ.findByResponsableId(responsableId);
        } else if (clienteId != null) {
            temp = tareaServ.findByClienteId(clienteId);
        } else if (estado != null) {
            temp = tareaServ.findByEstado(estado);
        } else {
            temp = tareaServ.findAll();
        }

        if (temp == null || temp.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(temp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> findById(@PathVariable Integer id) {
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().build();
        }
        Tarea temp = tareaServ.findById(id);
        if (temp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(temp);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tarea> create(@RequestBody Tarea tarea) {
        Tarea temp = tareaServ.create(tarea);
        if (temp == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(temp);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tarea> update(@PathVariable Integer id, @RequestBody Tarea tarea) {
        if (id == null || id < 1 || tarea == null) {
            return ResponseEntity.badRequest().build();
        }
        Tarea temp = tareaServ.update(id, tarea);
        if (temp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(temp);
    }

    @PostMapping("/{id}/asignar/{userId}")
    public ResponseEntity<Tarea> asignar(@PathVariable Integer id, @PathVariable Integer userId) {
        if (id == null || id < 1 || userId == null || userId < 1) {
            return ResponseEntity.badRequest().build();
        }
        Tarea temp = tareaServ.asignarResponsable(id, userId);
        if (temp == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(temp);
    }

    @PutMapping("/{id}/horas")
    public ResponseEntity<Tarea> actualizarHoras(@PathVariable Integer id, @RequestBody HorasRequest request) {
        if (id == null || id < 1 || request == null) {
            return ResponseEntity.badRequest().build();
        }
        Tarea temp = tareaServ.actualizarHoras(id, request.horasEmpleadas());
        if (temp == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(temp);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().build();
        }
        Boolean deleted = tareaServ.delete(id);
        if (deleted == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deleted);
    }
}
