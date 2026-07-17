package com.miproyecto.apirest.repositorio;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.modelo.Usuario;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

}
