package com.miproyecto.clienterest.dto;

import java.time.LocalDate;

public class TareaDTO {

    private Integer id;
    private String titulo;
    private String descripcion;
    private ClienteDTO cliente;
    private AdminUserDTO responsable;
    private String estado;
    private String prioridad;
    private LocalDate fechaLimite;
    private Double horasEmpleadas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public AdminUserDTO getResponsable() {
        return responsable;
    }

    public void setResponsable(AdminUserDTO responsable) {
        this.responsable = responsable;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Double getHorasEmpleadas() {
        return horasEmpleadas;
    }

    public void setHorasEmpleadas(Double horasEmpleadas) {
        this.horasEmpleadas = horasEmpleadas;
    }
}
