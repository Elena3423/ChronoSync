package org.chronosync.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Chrono Sync");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
