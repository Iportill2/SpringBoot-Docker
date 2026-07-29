package com.miproyecto.clienterest.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.service.AdminService;
import com.miproyecto.clienterest.service.BackupApiClient;
import com.miproyecto.clienterest.model.BackupInfo;


@Controller
@RequestMapping("/menu-admin")
public class AdminController {


    private final AdminService adminServ;

    private final BackupApiClient backupClient;



    public AdminController(
            AdminService adminServ,
            BackupApiClient backupClient) {

        this.adminServ = adminServ;
        this.backupClient = backupClient;
    }



    @GetMapping
    public String listUsers(Model model) {


        List<AdminUserDTO> users =
                adminServ.findPendingUsers();


        List<BackupInfo> backups =
                backupClient.listBackups();


        model.addAttribute(
                "users",
                users
        );


        model.addAttribute(
                "backups",
                backups
        );


        return "app/menu-admin";
    }



    @PostMapping("/backup")
    public String createBackup() {


        backupClient.createBackup();


        return "redirect:/menu-admin";
    }



    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Integer id) {

        adminServ.approve(id);

        return "redirect:/menu-admin";
    }



    @PostMapping("/block/{id}")
    public String block(@PathVariable Integer id) {

        adminServ.block(id);

        return "redirect:/menu-admin";
    }



    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        adminServ.delete(id);

        return "redirect:/menu-admin";
    }
}