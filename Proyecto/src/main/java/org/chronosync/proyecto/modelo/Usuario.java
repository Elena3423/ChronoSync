package org.chronosync.proyecto.modelo;

public class Usuario {
    // Atributos
    private int id;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private int activo;
    private Integer rolId;
    private Integer negocioId;

    /**
     * Constructor vacio (requerido en ciertos frameworks y uso general)
     */
    public Usuario() {}

    /**
     * Constructor para crear un usuario nuevo (sin rol ni negocio)
     */
    public Usuario(String nombre, String apellidos, String email, String password) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.activo = 0;
    }

    /**
     * Constructor completo, útil para cargar usuarios desde la base de datos
     */
    public Usuario(int id, String nombre, String apellidos, String email, String password, int activo, Integer rolId, Integer negocioId) {
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
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
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", rolId=" + rolId +
                ", negocioId=" + negocioId +
                '}';
    }
}