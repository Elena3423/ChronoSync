package org.chronosync.proyecto.modelo;

public class Negocio {

    // Atributos
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String codigoUnion;

    /**
     * Constructor vacio (requerido)
     */
    public Negocio() {}

    /**
     * Constructor completo
     */
    public Negocio(Integer id, String nombre, String direccion, String telefono, String email, String codigoUnion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.codigoUnion = codigoUnion;
    }

    /**
     * Constructor sin ID, para inserciones
     */
    public Negocio(String nombre, String direccion, String telefono, String email) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigoUnion() { return codigoUnion; }

    public void setCodigoUnion(String codigoUnion) { this.codigoUnion = codigoUnion; }

    @Override
    public String toString() {
        return "Negocio{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", codigoUnion='" + codigoUnion + '\'' +
                '}';
    }
}