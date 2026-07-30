package com.miproyecto.clienterest.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miproyecto.clienterest.service.BackupClientService;


@Controller
@RequestMapping("/backups")
public class BackupController {


    private final BackupClientService backupService;


    public BackupController(BackupClientService backupService) {
        this.backupService = backupService;
    }


    @PostMapping("/create")
    public String createBackup() {

        backupService.createBackup();

        return "redirect:/backups";
    }


    @GetMapping
    public String listBackups(Model model) {

        model.addAttribute(
            "backups",
            backupService.listBackups()
        );

        return "app/backups";
    }
    @PostMapping("/restore/{file}")
    public String restoreBackup(
            @PathVariable String file,
            RedirectAttributes redirectAttributes) {

        Boolean restored = backupService.restoreBackup(file);

        if (Boolean.TRUE.equals(restored)) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Backup restaurado correctamente"
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo restaurar el backup"
            );
        }

        return "redirect:/backups";
    }
    @GetMapping("/download/{file}")
    public ResponseEntity<Resource> downloadBackup(
            @PathVariable String file) {

        Resource resource = backupService.downloadBackup(file);

        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file + "\""
                )
                .body(resource);
    }
}