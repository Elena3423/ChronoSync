package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Exportacion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * Método que obtiene una exportación según su ID.
     *
     * @param id identificador de la exportación
     * @return Exportacion encontrada o null si no existe
     */
    public Exportacion obtenerPorId(int id) {
        String sql = "SELECT * FROM exportacion WHERE id = ?";
        Exportacion exp = null;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exp = construirExportacion(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo exportación por ID: " + e.getMessage());
        }

        return exp;
    }

    /**
     * Método que obtiene una lista con todas las exportaciones de la BD.
     *
     * @return lista de exportaciones
     */
    public List<Exportacion> obtenerTodas() {
        List<Exportacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM exportacion";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirExportacion(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo todas las exportaciones: " + e.getMessage());
        }

        return lista;
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
     * Método que construye un objeto Exportacion desde un ResultSet.
     *
     * @param rs datos de la consulta
     * @return objeto Exportacion construido
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Exportacion construirExportacion(ResultSet rs) throws SQLException {
        return new Exportacion(
                rs.getInt("id"),
                rs.getString("tipo_formato"),
                LocalDateTime.parse(rs.getString("fecha_generacion")),
                rs.getInt("usuario_id"),
                rs.getInt("negocio_id")
        );
    }

    public int contarExportacionesMesActual(int idNegocio) {
        String sql = "SELECT COUNT(*)\n" +
                "        FROM exportacion\n" +
                "        WHERE id_negocio = ?\n" +
                "          AND fecha_generacion <= LAST_DAY(CURRENT_DATE())\n" +
                "          AND fecha_generacion >= DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')";

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