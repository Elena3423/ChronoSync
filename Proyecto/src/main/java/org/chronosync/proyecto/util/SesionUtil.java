package org.chronosync.proyecto.util;

import javafx.stage.Stage;
import org.chronosync.proyecto.modelo.Usuario;

/**
 * Clase de utilidad que gestiona la sesión del usuario en la app
 * Asegura que solo exista una instancia de sesión
 */
public class SesionUtil {

    private static SesionUtil instancia;
    private static Usuario usuarioActual;

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
    public static Usuario getUsuario() {
        return usuarioActual;
    }

    /**
     * Método que establece el usuario que acaba de iniciar sesión
     *
     * @param usuario el usuario que representa el usuario recién logueado
     */
    public static void setUsuario(Usuario usuario) {
        usuarioActual = usuario;
    }

    /**
     * Cierra la sesión actual
     */
    public static void cerrarSesion(Stage stage) {
        usuarioActual = null;
        CargadorUtil.cambiarEscena(stage, "/fxml/login.fxml");
    }
}
