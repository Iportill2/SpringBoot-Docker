package com.miproyecto.apirest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.model.Break;
import com.miproyecto.apirest.service.BreakService;

@RestController
@RequestMapping("/api/break")
public class BreakController {

    private final BreakService breakService;

    public BreakController(BreakService breakService) {
        this.breakService = breakService;
    }

    @PostMapping("/start/{timeEntryId}")
    public ResponseEntity<Break> start(@PathVariable Integer timeEntryId) {
        try {
            Break newBreak = breakService.startBreak(timeEntryId);
            return ResponseEntity.ok(newBreak);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/end/{breakId}")
    public ResponseEntity<Break> end(@PathVariable Integer breakId) {
        try {
            Break endedBreak = breakService.endBreak(breakId);
            return ResponseEntity.ok(endedBreak);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}