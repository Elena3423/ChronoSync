package org.chronosync.proyecto.bd;

import java.sql.*;

public class ConexionBD {

    private static Connection conn = null;
    private static String url = "jdbc:sqlite:src/main/java/org/chronosync/proyecto/bd/chronosync.db";

    public static Connection obtenerConexion(){
        try {

            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url);
                System.out.println("Conexión establecida con la base de datos");

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys=ON;");
                    System.out.println("Soporte para claves foráneas activado");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el conexion: " + e.getMessage());
        }
        return conn;
    }

    public static void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.out.println("Error al desconectarse de la base de datos: " +  e.getMessage());
        }
    }

}
