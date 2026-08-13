package com.miproyecto.apirest.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tareas")
public class Tarea {

    public enum Estado {
        PENDIENTE, EN_CURSO, COMPLETADA
    }

    public enum Prioridad {
        ALTA, MEDIA, BAJA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Users responsable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Column(name = "horas_empleadas")
    private Double horasEmpleadas;

    public Tarea() {
    }

    public Tarea(Integer id, String titulo, String descripcion, Cliente cliente, Users responsable,
                 Estado estado, Prioridad prioridad, LocalDate fechaLimite, Double horasEmpleadas) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cliente = cliente;
        this.responsable = responsable;
        this.estado = estado;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
        this.horasEmpleadas = horasEmpleadas;
    }

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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Users getResponsable() {
        return responsable;
    }

    public void setResponsable(Users responsable) {
        this.responsable = responsable;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
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
