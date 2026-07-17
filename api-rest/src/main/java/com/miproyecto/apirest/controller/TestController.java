package com.miproyecto.apirest.controller;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.modelo.Usuario;
import com.miproyecto.apirest.servicio.UsuarioServicio;

@RestController
@RequestMapping("/api")
public class TestController {
	private final UsuarioServicio usuarioServicio;

    public TestController(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }
    @GetMapping("/string")
    public String string() {
        List<Usuario> usuarios = usuarioServicio.obtenerTodos();

        if (usuarios.isEmpty()) {
            return "No hay usuarios";
        }

        String resultado = "";

        for (Usuario usuario : usuarios) {
            resultado += "ID: " + usuario.getId()
                    + " - Nombre: " + usuario.getNombre()
                    + "<br>";
        }

        return resultado;
    }
    @GetMapping("/json")
    public List<Usuario> json() {
    	
    	return usuarioServicio.obtenerTodos();
    }
    

    
}