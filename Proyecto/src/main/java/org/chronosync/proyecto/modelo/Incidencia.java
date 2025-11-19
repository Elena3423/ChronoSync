package org.chronosync.proyecto.modelo;

public class Incidencia {

    // Atributos
    private Integer id;
    private String tipo;
    private String estado;
    private String comentarios;
    private Integer usuarioId;
    private Integer turnoId;

    /**
     * Constructor vacio
     */
    public Incidencia() {}

    /**
     * Constructor completo
     */
    public Incidencia(Integer id, String tipo, String estado, String comentarios, Integer usuarioId, Integer turnoId) {
        this.id = id;
        this.tipo = tipo;
        this.estado = estado;
        this.comentarios = comentarios;
        this.usuarioId = usuarioId;
        this.turnoId = turnoId;
    }

    /**
     * Constructor sin ID, para inserciones
     */
    public Incidencia(String tipo, String estado, String comentarios, Integer usuarioId, Integer turnoId) {
        this.tipo = tipo;
        this.estado = estado;
        this.comentarios = comentarios;
        this.usuarioId = usuarioId;
        this.turnoId = turnoId;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Integer turnoId) {
        this.turnoId = turnoId;
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", estado='" + estado + '\'' +
                ", comentarios='" + comentarios + '\'' +
                ", usuarioId=" + usuarioId +
                ", turnoId=" + turnoId +
                '}';
    }
}