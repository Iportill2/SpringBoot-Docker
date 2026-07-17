package com.miproyecto.apirest.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.miproyecto.apirest.modelo.Usuario;
import com.miproyecto.apirest.servicio.UsuarioServicio;

@Controller
@RequestMapping("/cli")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/html")
    public String html(Model model) {

        List<Usuario> usuarios = usuarioServicio.obtenerTodos();

        model.addAttribute("usuarios", usuarios);

        return "inicio";
    }
}