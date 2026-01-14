package org.chronosync.proyecto.modelo;

public class Rol {

    // Atributos
    private Integer id;
    private String tipoRol;
    private String descripcion;

    /**
     * Constructor vacio
     */
    public Rol() {}

    /**
     * Constructor sin ID, para inserciones
     */
    public Rol(String tipoRol, String descripcion) {
        this.tipoRol = tipoRol;
        this.descripcion = descripcion;
    }

    /**
     * Constructor completo
     */
    public Rol(Integer id, String tipoRol, String descripcion) {
        this.id = id;
        this.tipoRol = tipoRol;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", tipoRol='" + tipoRol + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}