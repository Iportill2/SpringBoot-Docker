package com.miproyecto.apirest.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    

    public Usuario() {
    }

    public Usuario(int id,String nombre) {
    	this.id = id;
        this.nombre = nombre;
        
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }


    public void setId(int id) {
        this.id = id;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}