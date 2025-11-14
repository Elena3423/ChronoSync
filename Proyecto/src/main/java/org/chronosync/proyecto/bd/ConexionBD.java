package org.chronosync.proyecto.bd;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    /**
     * Establece la conexión a la base de datos SQLite
     * Si el archivo .db no existe, se crea automáticamente
     */
    public static void conexion(){

        // Declaramos la conexión fuera del try para que sea accesible desde el finally
        Connection conn = null;
        try {
            // URL de la conexión
            String url = "jdbc:sqlite:src/main/java/org/chronosync/proyecto/bd/chronosync.db";

            // Establecemos la conexión con la base de datos
            conn = DriverManager.getConnection(url);

            if (conn != null) {
                System.out.println("Conexión establecida con la base de datos");
            }

        } catch (SQLException e) {
            System.out.println("Error al conectarse a la base de datos: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("Conexión cerrada");
                }
            } catch (SQLException ex) {
                System.out.println("Error al intentar desconectarse con la base de datos: " + ex.getMessage());
            }
        }
    }
}
