module org.chronosync.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.chronosync.proyecto to javafx.fxml;
    exports org.chronosync.proyecto;
}