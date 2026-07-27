package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoryController {

    @GetMapping("/history")
    public String historyGet() {
        
        return "app/history";
    }
}
