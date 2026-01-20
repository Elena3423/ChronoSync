package org.chronosync.proyecto.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class CargadorUtil {

    // Bloque estático para cargar las fuentes al inicar la clase
    static {
        try {
            String[] fuentes = {"Roboto-Regular.ttf", "Roboto-Bold.ttf", "Roboto-Italic.ttf"};
            for (String f : fuentes) {
                URL fontUrl = CargadorUtil.class.getResource("fonts/" + f);
                if (fontUrl == null) {
                    System.err.println("Error al encontrar las fuentes: " + f);
                } else {
                    Font cargada = Font.loadFont(fontUrl.toExternalForm(), 10);
                    if (cargada == null) System.err.println("Error de renderizado: " + f);
                    else System.out.println("Fuente cargada: " + cargada.getFamily());
                }
            }
        } catch (Exception e) {
            System.err.println("Error crítico al cargar fuentes");
        }
    }

    /**
     * Cambia a otro archivo FXML forzando que se ajuste al tamaño máximo de la pantalla
     *
     * @param stage ventana principal sobre la que se aplicará el cambio
     * @param rutaFXML ruta del archivo FXML que queremos cargar
     */
    public static void cambiarEscena(Stage stage, String rutaFXML) {
        try {
            // Buscamos dónde está el archivo FXML
            URL localizacion = CargadorUtil.class.getResource(rutaFXML);

            if (localizacion == null) {
                System.err.println("Error! No se ha encontrado el archivo fxml: " + rutaFXML);
                return;
            }

            // Cargamos la nueva vista
            FXMLLoader loader = new FXMLLoader(localizacion);
            loader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
            Parent root = loader.load();

            // Si el FXML es una región (como AnchorPane o VBox), le decimos que ocupe todo el espacio
            if (root instanceof javafx.scene.layout.Region) {
                javafx.scene.layout.Region region = (javafx.scene.layout.Region) root;
                region.setPrefWidth(Double.MAX_VALUE);
                region.setPrefHeight(Double.MAX_VALUE);
            }

            // Actualizamos el estilo antes de mostrarlo para evitar fallos
            root.applyCss();
            root.layout();

            // Mostramos la escena en la ventana
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Usamos Platform.runLater() para esperar a que se procese el cambio de ventana
            javafx.application.Platform.runLater(() -> {
                stage.setMaximized(false);
                stage.setMaximized(true);
                stage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el fichero " + rutaFXML, e);
        }
    }
}