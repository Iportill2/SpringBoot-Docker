package com.miproyecto.apirest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<TimeEntry> stop(@PathVariable Integer timeEntryId) {
        try {
            TimeEntry entry = timeEntryServ.stopEntry(timeEntryId);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    

}
