package com.miproyecto.apirest.servicio;

import java.util.List;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.modelo.Usuario;
import com.miproyecto.apirest.repositorio.UsuarioRepository;



@Service
public class UsuarioServicio {


    private final UsuarioRepository usuarioRepository;


    public UsuarioServicio(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }


}