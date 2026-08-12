package com.miproyecto.backup.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.backup.model.BackupInfo;
import com.miproyecto.backup.service.BackupService;

@RestController
@RequestMapping("/backups")
public class BackupController {

    private final BackupService service;

    public BackupController(BackupService service) {
        this.service = service;
    }

    @PostMapping
    public String createBackup() {
        String file = service.createBackup();
        service.cleanupOldBackups();
        return file;
    }
    @GetMapping
    public List<BackupInfo> listBackups()
    {return service.listBackups();}
    
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable String fileName) 
    {
        return ResponseEntity.ok(service.downloadBackup(fileName));
    }
    @PostMapping("/restore/{fileName}")
    public Boolean restoreBackup(@PathVariable String fileName) {

        return service.restoreBackup(fileName);

    }
    @DeleteMapping("/{fileName}")
    public Boolean deleteBackup(@PathVariable String fileName) {

        return service.deleteBackup(fileName);

    }
    @PostMapping("/cleanup")
    public int cleanupBackups() {
        return service.cleanupOldBackups();
    }
}