package com.miproyecto.clienterest.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.service.AdminService;
import com.miproyecto.clienterest.service.BackupClientService;


@Controller
@RequestMapping("/menu/admin")
public class AdminController {

    private final AdminService adminServ;

    private final BackupClientService backupService;

    public AdminController(
            AdminService adminServ,
            BackupClientService backupService) {

        this.adminServ = adminServ;
        this.backupService = backupService;
    }

    @GetMapping
    public String listUsers(Model model) {

        List<AdminUserDTO> users =
                adminServ.findPendingUsers();


        model.addAttribute(
                "users",
                users
        );

        return "app/menu-admin";
    }

    @PostMapping("/backup")
    public String createBackup() {


        backupService.createBackup();


        return "redirect:/menu/admin";
    }


    @PostMapping("/restore/{file}")
    public String restoreBackup(
            @PathVariable String file,
            RedirectAttributes redirectAttributes) {

        try {

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

        } catch (RestClientException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo restaurar el backup: " + e.getMessage()
            );
        }

        return "redirect:/menu/admin";
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


    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Integer id) {
        adminServ.approve(id);
        return "redirect:/menu/admin";
    }

    @PostMapping("/block/{id}")
    public String block(@PathVariable Integer id) {
        adminServ.block(id);
        return "redirect:/menu/admin";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        adminServ.delete(id);
        return "redirect:/menu/admin";
    }
}