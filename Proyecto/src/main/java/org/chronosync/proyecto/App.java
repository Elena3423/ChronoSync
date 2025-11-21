package org.chronosync.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.chronosync.proyecto.bd.ConexionBD;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {

        try {
            ConexionBD.obtenerConexion();
        } catch (Exception ex) {
            System.out.println("Error en la conexión o creación de tablas: " + ex.getMessage());
        }

        launch(args); // Llamar a launch después de la inicialización de la BD
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 1920, 1080); // Tamaño inicial

            Image icono = new Image(getClass().getResourceAsStream("/img/iconoPNG.png"));
            primaryStage.getIcons().add(icono);

            primaryStage.setTitle("Chrono Sync - Login");
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista de login: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        System.out.println("La aplicación se está cerrando. Cerrando la conexión a la BD...");
        try {
            ConexionBD.cerrarConexion();
            System.out.println("Conexión a la BD cerrada con éxito");
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión a la BD: " + e.getMessage());
        }
    }
}