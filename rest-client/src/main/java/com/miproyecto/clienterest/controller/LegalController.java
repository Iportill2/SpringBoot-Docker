package com.miproyecto.clienterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/legal/privacy")
    public String privacidad() {
        return "legal/privacy";
    }

    @GetMapping("/legal/cookies")
    public String cookies() {
        return "legal/cookies";
    }
}
