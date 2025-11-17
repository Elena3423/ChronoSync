package org.chronosync.proyecto.bd;

import java.sql.*;

/**
 * Clase de utilidad para gestionar la conexión a la base de datos SQLite
 * Asegura que solo haya una conexión activa
 */
public class ConexionBD {

    // Variable que almacena la única instancia de la conexión
    private static Connection conn = null;

    // URL de conexión a la base de datos SQLite
    private static String url = "jdbc:sqlite:src/main/java/org/chronosync/proyecto/bd/chronosync.db";

    /**
     * Método para obtener la conexión a la base de datos
     * Si la conexión no existe o está cerrada, intenta establecer una nueva
     * @return Devuelve el objeto Connection a la base de datos
     */
    public static Connection obtenerConexion(){
        try {

            // Verifica si la conexión es nula o si ya está cerrada
            if (conn == null || conn.isClosed()) {

                // Establece la conexión utilizando la URL
                conn = DriverManager.getConnection(url);
                System.out.println("Conexión establecida con la base de datos");

                // Intenta activar el soporte para crear claves fóraneas
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys=ON;");
                    System.out.println("Soporte para claves foráneas activado");
                }
            }
        } catch (SQLException e) {
            // Muestra cualquier error que ocurra durante la conexión
            System.out.println("Error al obtener el conexion: " + e.getMessage());
        }

        // Devuelve la conexión establecida (o null si da error)
        return conn;
    }

    /**
     * Método para cerrar la conexión activa a la base de datos
     */
    public static void cerrarConexion() {
        try {

            // Verifica si la conexión existe y no está cerrada antes de intentar cerrarla
            if (conn != null && !conn.isClosed()) {
                conn.close(); // Cierra la conexión
                conn = null; // Establece la variable a null para permitir una nueva conexión
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            // Muestra cualquier error al intentar cerrar la conexión
            System.out.println("Error al desconectarse de la base de datos: " +  e.getMessage());
        }
    }

}