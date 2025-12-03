package org.chronosync.proyecto.bd;

import org.chronosync.proyecto.secure.ConfigManager;

import java.sql.*;
import java.util.Properties;

/**
 * Clase de utilidad para gestionar la conexión a la base de datos
 * Asegura que solo haya una conexión activa
 */
public class ConexionBD {

    // Variable que almacena la única instancia de la conexión
    private static Connection conn = null;

    /* URL de conexión a la base de datos SQLite
    private static final String url = "jdbc:mysql://mysql-chronosync.alwaysdata.net/chronosync_bd";
    private static final String usuario = "441742_user";
    private static String password = "SalvaElena0604";*/

    /**
     * Método para obtener la conexión a la base de datos
     * Si la conexión no existe o está cerrada, intenta establecer una nueva
     * @return Devuelve el objeto Connection a la base de datos
     */
    public static Connection obtenerConexion() {
        try {
            // Si no hay conexión o está cerrada, la creamos
            if (conn == null || conn.isClosed()) {

                // Cargamos los datos desde ConfigManager (dev o secure)
                Properties p = ConfigManager.getConfig();
                String url = p.getProperty("db.url");
                String usuario = p.getProperty("db.user");
                String password = p.getProperty("db.password");

                // Conectamos a MySQL
                conn = DriverManager.getConnection(url, usuario, password);
                System.out.println("Conexión establecida con MySQL (ConfigManager).");
            }

        } catch (Exception e) {
            System.out.println("Error al obtener la conexión: " + e.getMessage());
            e.printStackTrace();
        }

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