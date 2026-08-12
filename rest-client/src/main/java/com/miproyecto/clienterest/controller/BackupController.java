package com.miproyecto.clienterest.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miproyecto.clienterest.service.BackupClientService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("menu/backups")
public class BackupController {


    private final BackupClientService backupService;


    public BackupController(BackupClientService backupService) {
        this.backupService = backupService;
    }


    @PostMapping("/create")
    public String createBackup(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            session.invalidate();
            return "redirect:/login";
        }

        backupService.createBackup(actorOf(session));

        redirectAttributes.addFlashAttribute(
                "popup",
                "Backup creado correctamente"
        );

        return "redirect:/menu/backups";
    }


    @GetMapping
    public String listBackups(HttpSession session, Model model) {

        if (!esAdmin(session)) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute(
            "backups",
            backupService.listBackups()
        );

        return "app/backups";
    }
    @PostMapping("/restore/{file}")
    public String restoreBackup(
            @PathVariable String file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            session.invalidate();
            return "redirect:/login";
        }

        try {

            Boolean restored = backupService.restoreBackup(file, actorOf(session));

            if (Boolean.TRUE.equals(restored)) {

                redirectAttributes.addFlashAttribute(
                        "popup",
                        "Backup restaurado correctamente"
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "No se pudo restaurar el backup"
                );
            }

        } catch (RestClientException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo restaurar el backup: " + e.getMessage()
            );
        }

        return "redirect:/menu/backups";
    }
    @GetMapping("/download/{file}")
    public ResponseEntity<Resource> downloadBackup(
            @PathVariable String file,
            HttpSession session) {

        if (!esAdmin(session)) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(java.net.URI.create("/login"))
                    .build();
        }

        Resource resource = backupService.downloadBackup(file, actorOf(session));

        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file + "\""
                )
                .body(resource);
    }

    @PostMapping("/delete/{file}")
    public String deleteBackup(
            @PathVariable String file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            session.invalidate();
            return "redirect:/login";
        }

        try {

            Boolean deleted = backupService.deleteBackup(file, actorOf(session));

            if (Boolean.TRUE.equals(deleted)) {

                redirectAttributes.addFlashAttribute(
                        "popup",
                        "Backup eliminado correctamente"
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "No se pudo eliminar el backup"
                );
            }

        } catch (RestClientException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo eliminar el backup: " + e.getMessage()
            );
        }

        return "redirect:/menu/backups";
    }

    @GetMapping("/log")
    public ResponseEntity<String> viewLog(HttpSession session) {

        if (!esAdmin(session)) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(java.net.URI.create("/login"))
                    .build();
        }

        String log = backupService.getLog();

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(log != null ? log : "");
    }

    private boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    private String actorOf(HttpSession session) {
        Object username = session.getAttribute("username");
        return username != null ? String.valueOf(username) : "DESCONOCIDO";
    }
}