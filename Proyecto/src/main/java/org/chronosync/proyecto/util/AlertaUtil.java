package org.chronosync.proyecto.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * Clase centralizada para mostrar mensajes al usuario.
 * Así evitamos repetir el código de las alertas en cada controlador.
 */
public class AlertaUtil {

    /**
     * Muestra una alerta de información básica.
     * @param titulo  El texto que irá en la barra de la ventana.
     * @param mensaje El cuerpo del mensaje.
     */
    public static void mostrarInfo(String titulo, String mensaje) {
        crearAlerta(titulo, mensaje, AlertType.INFORMATION);
    }

    /**
     * Muestra una alerta de error (con icono de parada).
     * @param titulo  El texto del error.
     * @param mensaje Qué ha fallado exactamente.
     */
    public static void mostrarError(String titulo, String mensaje) {
        crearAlerta(titulo, mensaje, AlertType.ERROR);
    }

    /**
     * Muestra una alerta de advertencia.
     */
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        crearAlerta(titulo, mensaje, AlertType.WARNING);
    }

    /**
     * Método interno privado para configurar la alerta y mostrarla.
     */
    private static void crearAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        // Intentamos ponerle el icono de la app a la ventana de la alerta
        try {
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(AlertaUtil.class.getResourceAsStream("/img/iconoPNG.png")));
        } catch (Exception e) {
            // Si falla el icono, no pasa nada, la alerta se muestra igual
        }

        alerta.showAndWait();
    }
}