package org.chronosync.proyecto.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class CargadorUtil {

    /**
     * Cambia a otro archivo FXML dentro de la misma ventana
     *
     * @param stage ventana actual
     * @param rutaFXML ruta del archivo FXML
     */
    public static void cambiarEscena(Stage stage, String rutaFXML) {
        try {
            // Se usa getClass().getResource para asegurar que la ruta se resuelve correctamente en el classpath.
            Parent root = FXMLLoader.load(CargadorUtil.class.getResource(rutaFXML));

            Rectangle2D pantalla =  Screen.getPrimary().getVisualBounds();

            Scene scene = new Scene(root, pantalla.getWidth(), pantalla.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el fichero " + rutaFXML, e);
        }
    }
}