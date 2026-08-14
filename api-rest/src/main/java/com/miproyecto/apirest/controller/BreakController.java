package com.miproyecto.apirest.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/break")
public class BreakController {

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> start() {
        String now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        return ResponseEntity.ok(Map.of("time", now));
    }

    @PostMapping("/end")
    public ResponseEntity<Map<String, String>> end() {
        String now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        return ResponseEntity.ok(Map.of("time", now));
    }
}