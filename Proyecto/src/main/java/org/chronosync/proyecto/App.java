package org.chronosync.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.bd.CrearTablas;
import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {

        try {
            ConexionBD.obtenerConexion();
            CrearTablas.crearTablas();
        } catch (Exception ex) {
            System.out.println("Error en la conexión o creación de tablas: " + ex.getMessage());
        } finally {
            ConexionBD.cerrarConexion();
        }

        launch(args); // Llamar a launch después de la inicialización de la BD
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 1600, 1000); // Tamaño inicial

            primaryStage.setTitle("Chrono Sync - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista de login: " + e.getMessage());
        }
    }
}