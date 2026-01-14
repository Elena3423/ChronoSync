package org.chronosync.proyecto.modelo;

import java.time.LocalDateTime;

public class Exportacion {

    // Atributos
    private Integer id;
    private String tipoFormato;
    private LocalDateTime fechaGeneracion;
    private Integer usuarioId;
    private Integer negocioId;

    /**
     * Constructor vacio (requerido)
     */
    public Exportacion() {}

    /**
     * Constructor sin ID, para inserciones
     */
    public Exportacion(String tipoFormato, LocalDateTime fechaGeneracion, Integer usuarioId, Integer negocioId) {
        this.tipoFormato = tipoFormato;
        this.fechaGeneracion = fechaGeneracion;
        this.usuarioId = usuarioId;
        this.negocioId = negocioId;
    }

    /**
     * Constructor completo
     */
    public Exportacion(Integer id, String tipoFormato, LocalDateTime fechaGeneracion, Integer usuarioId, Integer negocioId) {
        this.id = id;
        this.tipoFormato = tipoFormato;
        this.fechaGeneracion = fechaGeneracion;
        this.usuarioId = usuarioId;
        this.negocioId = negocioId;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoFormato() {
        return tipoFormato;
    }

    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Integer negocioId) {
        this.negocioId = negocioId;
    }

    @Override
    public String toString() {
        return "Exportacion{" +
                "id=" + id +
                ", tipoFormato='" + tipoFormato + '\'' +
                ", fechaGeneracion=" + fechaGeneracion +
                ", usuarioId=" + usuarioId +
                ", negocioId=" + negocioId +
                '}';
    }
}