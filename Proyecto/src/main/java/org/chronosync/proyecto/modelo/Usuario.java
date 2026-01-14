package org.chronosync.proyecto.modelo;

public class Usuario {

    // Atributos según la tabla SQL
    private Integer id;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private boolean activo;
    private Integer rolId;
    private Integer negocioId;

    /**
     * Constructor vacio
     */
    public Usuario() {}

    /**
     * Constructor sin ID, para inserciones
     */
    public Usuario(String nombre, String apellidos, String email, String password) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.activo = false;
    }

    /**
     * Constructor completo
     */
    public Usuario(Integer id, String nombre, String apellidos, String email, String password, boolean activo, Integer rolId, Integer negocioId
    ) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.activo = activo;
        this.rolId = rolId;
        this.negocioId = negocioId;
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Integer negocioId) {
        this.negocioId = negocioId;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}