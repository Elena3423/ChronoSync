package org.chronosync.proyecto.bd;

import java.sql.*;

public class ConexionBD {

    private static Connection conn = null;

    // Fragmentación de credenciales para dificultar la lectura simple del binario
    private static final String S1 = "jdbc:mysql://";
    private static final String S2 = "mysql-chronosync";
    private static final String S3 = ".alwaysdata.net/";
    private static final String S4 = "chronosync_bd";

    private static final String U1 = "441742_";
    private static final String U2 = "user";

    // Contraseña reconstruida en tiempo de ejecución
    private static String getP() {
        return "Salva" + "Elena" + "0604";
    }

    /**
     * Obtiene la conexión a la base de datos de forma directa.
     */
    public static Connection obtenerConexion() {
        try {
            if (conn == null || conn.isClosed()) {

                // Reconstruimos la URL y el Usuario
                String fullUrl = S1 + S2 + S3 + S4;
                String fullUser = U1 + U2;
                String fullPass = getP();

                // Conectamos a MySQL directamente
                conn = DriverManager.getConnection(fullUrl, fullUser, fullPass);
                System.out.println("Conexión establecida con MySQL.");
            }
        } catch (SQLException e) {
            System.err.println("Error de SQL al obtener la conexión: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general al obtener la conexión: " + e.getMessage());
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
            System.out.println("Error al cerrar la base de datos: " +  e.getMessage());
        }
    }
}