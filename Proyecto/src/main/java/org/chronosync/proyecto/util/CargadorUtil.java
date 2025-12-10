package org.chronosync.proyecto.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.awt.*;
import java.io.IOException;

public class CargadorUtil {

    static {
        try {
            Font.loadFont(CargadorUtil.class.getResourceAsStream("fonts/Roboto-Regular.ttf"), 10);
            Font.loadFont(CargadorUtil.class.getResourceAsStream("fonts/Roboto-Bold.ttf"), 10);
            Font.loadFont(CargadorUtil.class.getResourceAsStream("fonts/Roboto-Italic.ttf"), 10);
        } catch (Exception e) {
            System.err.println("Error al cargar las fuentes Roboto");
        }
    }

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

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el fichero " + rutaFXML, e);
        }
    }
}