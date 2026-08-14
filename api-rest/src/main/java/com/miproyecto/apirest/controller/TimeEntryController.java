package com.miproyecto.apirest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.model.TimeEntry;
import com.miproyecto.apirest.service.TimeEntryService;


@RestController
@RequestMapping("/api/time-entry")
public class TimeEntryController {

	private final TimeEntryService timeEntryServ;

    public TimeEntryController(TimeEntryService timeEntryServ) {
        this.timeEntryServ = timeEntryServ;
    }
    
    @PostMapping("/start/{userId}")
    public ResponseEntity<TimeEntry> start(@PathVariable Integer userId) {
        try {
            TimeEntry entry = timeEntryServ.startEntry(userId);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/stop/{timeEntryId}")
    public ResponseEntity<TimeEntry> stop(
            @PathVariable Integer timeEntryId,
            @RequestParam(defaultValue = "0") int pauseMinutes) {
        try {
            TimeEntry entry = timeEntryServ.stopEntry(timeEntryId, pauseMinutes);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TimeEntry>> findByMonth(
            @PathVariable Integer userId,
            @RequestParam int year,
            @RequestParam int month) {
        List<TimeEntry> entries = timeEntryServ.findByUserAndMonth(userId, year, month);
        return ResponseEntity.ok(entries);
    }

}