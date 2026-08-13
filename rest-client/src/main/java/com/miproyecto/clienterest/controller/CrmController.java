package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menu")
public class CrmController {

    @GetMapping("/crm")
    public String crmGet() {
        
        return "app/crm";
    }
}
