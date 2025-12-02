module ChronoSyncApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens org.chronosync to javafx.graphics, javafx.fxml;
    exports org.chronosync;
}