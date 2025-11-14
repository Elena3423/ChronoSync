package org.chronosync.proyecto.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CrearTablas {
    public static void crearNuevaTabla() {
        String url = "jdbc:sqlite:src/main/java/org/chronosync/proyecto/bd/chronosync.db";

        

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsuario);

        } catch (SQLException e) {
            System.err.println("Error al crear la tabla: " + e.getMessage());
        }
    }
}
