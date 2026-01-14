package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Exportacion;

import java.sql.*;

public class ExportacionDAO {

    /**
     * Método que inserta una nueva exportación en la base de datos.
     *
     * @param exportacion objeto Exportacion a insertar
     * @return true si la operación fue exitosa; false si ocurrió un error
     */
    public boolean insertar(Exportacion exportacion) {
        String sql = "INSERT INTO exportacion (tipo_formato, fecha_generacion, usuario_id, negocio_id) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, exportacion.getTipoFormato());
            stmt.setString(2, exportacion.getFechaGeneracion().toString());
            stmt.setInt(3, exportacion.getUsuarioId());
            stmt.setInt(4, exportacion.getNegocioId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertando exportación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que elimina una exportación de la BD.
     *
     * @param id identificador de la exportación
     * @return true si la operación fue correcta
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM exportacion WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error eliminando exportación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que cuenta el total de exportaciones en un més de un negocio
     *
     * @param idNegocio id del negocio al que hace referencia
     * @return devuelve el total de exportaciones del mes
     */
    public int contarExportacionesMesActual(int idNegocio) {
        String sql = "SELECT COUNT(*) FROM exportacion WHERE negocio_id = ? " +
                "AND MONTH(fecha_generacion) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(fecha_generacion) = YEAR(CURRENT_DATE())";

        int conteo = 0;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idNegocio);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    conteo = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al contar exportaciones del mes actual: " + e.getMessage());
        }

        return conteo;
    }

}