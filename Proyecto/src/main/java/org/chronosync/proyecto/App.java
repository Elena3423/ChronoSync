package org.chronosync.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.bd.CrearTablas;

import java.sql.SQLException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Chrono Sync");
        stage.show();
    }

    public static void main(String[] args) {
        // launch();
        try {
            ConexionBD.obtenerConexion();
            CrearTablas.crearTablas();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            ConexionBD.cerrarConexion();
        }
    }
}
