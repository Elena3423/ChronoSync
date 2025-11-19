package org.chronosync.proyecto.modelo;

import java.time.LocalDateTime;

public class Turno {

    // Atributos
    private Integer id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tipo;
    private String estado;
    private Integer usuarioId;

    /**
     * Constructor vacio (requerido)
     */
    public Turno() {}

    /**
     * Constructor completo
     */
    public Turno(Integer id, LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipo, String estado, Integer usuarioId) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
        this.estado = estado;
        this.usuarioId = usuarioId;
    }

    /**
     * Constructor sin ID, para inserciones
     */
    public Turno(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipo, String estado, Integer usuarioId) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
        this.estado = estado;
        this.usuarioId = usuarioId;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", tipo='" + tipo + '\'' +
                ", estado='" + estado + '\'' +
                ", usuarioId=" + usuarioId +
                '}';
    }
}