module org.chronosync.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;

    // Librerías de exportación
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires kernel;
    requires layout;
    requires io;
    requires commons;

    opens org.chronosync.proyecto to javafx.fxml;
    exports org.chronosync.proyecto;

    opens org.chronosync.proyecto.modelo to javafx.base;

    opens org.chronosync.proyecto.controlador to javafx.fxml;
    opens org.chronosync.proyecto.controlador.menu to javafx.fxml;
}
