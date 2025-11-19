module org.chronosync.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens org.chronosync.proyecto to javafx.fxml;
    exports org.chronosync.proyecto;

    opens org.chronosync.proyecto.controlador to javafx.fxml;
}