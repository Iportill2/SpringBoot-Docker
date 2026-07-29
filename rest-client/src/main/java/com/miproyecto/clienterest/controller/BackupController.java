package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String restoreBackup(@PathVariable String file) {

        backupService.restoreBackup(file);

        return "redirect:/backups";
    }

}