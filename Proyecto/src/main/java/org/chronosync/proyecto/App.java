package org.chronosync.proyecto;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.chronosync.proyecto.bd.ConexionBD;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Método que se ejecuta antes de que aparezca la ventana
     * Conectamos la base de datos sin congelar la interfaz
     */
    @Override
    public void init() {
        System.out.println("Iniciando aplicación y conectando a la base de datos...");
        try {
            ConexionBD.obtenerConexion();
        } catch (Exception ex) {
            System.err.println("Error! No se pudo conectar a la base de datos: " + ex.getMessage());
        }
    }

    /**
     * Método que muestra la primera ventana de la aplicación
     *
     * @param primerStage primera ventana de la app
     * @throws Exception se lanza si no se puede cargar la ventana
     */
    @Override
    public void start(Stage primerStage) throws Exception {
        try {
            // Buscamos el archivo de la ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Creamos la escena
            Scene scene = new Scene(root);

            // Le ponemos el icono a la ventana y el nombre del programa
            try {
                Image icono = new Image(getClass().getResourceAsStream("/img/iconoPNG.png"));
                primerStage.getIcons().add(icono);
            } catch (Exception e) {
                System.err.println("No se pudo cargar el icono de la ventana.");
            }

            primerStage.setTitle("Chrono Sync");
            primerStage.setScene(scene);

            // Usamos Platform.runLater para que la ventana se abra directamente en grande
            Platform.runLater(() -> {
                primerStage.setMaximized(true);
                primerStage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista de login: " + e.getMessage());
        }
    }

    /**
     * Método que se ejecuta al cerrar la app y que no deja conexiones abiertas
     * @throws Exception se lanza si no se puede cerrar la BD
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Cerrando Chrono Sync de forma segura...");
        try {
            ConexionBD.cerrarConexion();
            System.out.println("Conexión con la base de datos cerrada con éxito!");
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión a la BD: " + e.getMessage());
        }
    }
}