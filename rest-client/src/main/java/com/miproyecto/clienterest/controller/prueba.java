package com.miproyecto.clienterest.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miproyecto.clienterest.dto.UsuarioDto;
import com.miproyecto.clienterest.servicio.ClienteServ;


@Controller
@RequestMapping("/cliente")

public class prueba {

    private final ClienteServ clienteServ;


    public prueba(ClienteServ clienteServ) {
        this.clienteServ = clienteServ;
    }
    


    @GetMapping("/inicio")
    public String inicio() {
       
        return "inicio";
    }


    @GetMapping("/json")
    public String json() {
    	
        return null;
    }


    @GetMapping("/usuarios")
    public String usuarios(Model model) {

        List<UsuarioDto> usuarios = clienteServ.obtenerUsuarios();

        model.addAttribute("usuarios", usuarios);

        return "usuarios";
    }
}