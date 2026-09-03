package com.miproyecto.apirest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.Tarea;
import com.miproyecto.apirest.model.Users;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByResponsableId(Integer responsableId);

    List<Tarea> findByResponsableIsNull();

    List<Tarea> findByClienteId(Integer clienteId);

    List<Tarea> findByEstado(Tarea.Estado estado);
    
    void deleteByClienteId(Integer clienteId);
}
