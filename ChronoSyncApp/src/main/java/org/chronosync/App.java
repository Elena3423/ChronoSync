package org.chronosync;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        final String FXML_PATH = "/org.chronosync/vista/fxml/prueba.fxml";
        URL fxmlUrl = getClass().getResource(FXML_PATH);
        Parent root = FXMLLoader.load(Objects.requireNonNull(fxmlUrl, "El archivo FXML no se encontró en: " + FXML_PATH));

        Scene scene = new Scene(root);
        stage.setTitle("ChronoSync - Cliente");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}