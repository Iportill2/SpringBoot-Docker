package com.miproyecto.backup.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.backup.config.BackupActorContext;
import com.miproyecto.backup.model.BackupInfo;
import com.miproyecto.backup.service.BackupAuditLog;
import com.miproyecto.backup.service.BackupService;

@RestController
@RequestMapping("/backups")
public class BackupController {

    private final BackupService service;
    private final BackupActorContext actorContext;
    private final BackupAuditLog auditLog;

    public BackupController(
            BackupService service,
            BackupActorContext actorContext,
            BackupAuditLog auditLog) {
        this.service = service;
        this.actorContext = actorContext;
        this.auditLog = auditLog;
    }

    @PostMapping
    public String createBackup(
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        try {
            actorContext.setActor(actor);
            String file = service.createBackup();
            service.cleanupOldBackups();
            return file;
        } finally {
            actorContext.clear();
        }
    }

    @GetMapping
    public List<BackupInfo> listBackups() {
        return service.listBackups();
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadBackup(
            @PathVariable String fileName,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        try {
            actorContext.setActor(actor);
            return ResponseEntity.ok(service.downloadBackup(fileName));
        } finally {
            actorContext.clear();
        }
    }

    @PostMapping("/restore/{fileName}")
    public Boolean restoreBackup(
            @PathVariable String fileName,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        try {
            actorContext.setActor(actor);
            return service.restoreBackup(fileName);
        } finally {
            actorContext.clear();
        }
    }

    @DeleteMapping("/{fileName}")
    public Boolean deleteBackup(
            @PathVariable String fileName,
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        try {
            actorContext.setActor(actor);
            return service.deleteBackup(fileName);
        } finally {
            actorContext.clear();
        }
    }

    @PostMapping("/cleanup")
    public int cleanupBackups(
            @RequestHeader(value = "X-Actor", required = false) String actor) {
        try {
            actorContext.setActor(actor);
            return service.cleanupOldBackups();
        } finally {
            actorContext.clear();
        }
    }

    @GetMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getLog() {
        return auditLog.readLog();
    }
}