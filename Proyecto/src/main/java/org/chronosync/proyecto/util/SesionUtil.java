package org.chronosync.proyecto.util;

import org.chronosync.proyecto.modelo.Usuario;

/**
 * Clase de utilidad que gestiona la sesión del usuario en la app
 * Asegura que solo exista una instancia de sesión
 */
public class SesionUtil {

    private static SesionUtil instancia;
    private Usuario usuario;

    /**
     * Constructor privado que impide que se creen nuevas instancias desde fuera de esta clase
     */
    private SesionUtil() {}

    /**
     * Método que obtiene la única instancia de sesión
     *
     * @return la única instancia de sesión
     */
    public static SesionUtil obtenerInstancia() {
        if (instancia == null) {
            instancia = new SesionUtil();
        }
        return instancia;
    }

    /**
     * Méteodo que obtiene el usuario actualmente logueado
     *
     * @return el usuario que representa la sesión activa, o null si no hay sesión iniciada
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Método que establece el usuario que acaba de iniciar sesión
     *
     * @param usuario el usuario que representa el usuario recién logueado
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Cierra la sesión actual
     */
    public void cerrarSesion() {
        usuario = null;
    }
}
