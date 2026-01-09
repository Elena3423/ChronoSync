package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Incidencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncidenciaDAO {

    public boolean insertar(Incidencia incidencia) {
        String sql = "INSERT INTO incidencias (tipo, estado, comentarios, usuario_id, turno_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, incidencia.getTipo()); // 'Ausencia', 'Cambio', 'Retraso', 'Otra'
            stmt.setString(2, incidencia.getEstado()); // 'Pendiente', 'Aceptada', 'Rechazada'
            stmt.setString(3, incidencia.getComentarios());
            stmt.setInt(4, incidencia.getUsuarioId());

            stmt.setInt(5, incidencia.getTurnoId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int id, String nuevoEstado) {
        // Ajustado a tus valores ENUM: 'Aceptada' (en lugar de Aprobada)
        String sql = "UPDATE incidencias SET estado = ? WHERE id = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /**
     * Método que obtiene una incidencia según su ID.
     *
     * @param id identificador de la incidencia
     * @return Incidencia encontrada o null si no existe
     */
    public Incidencia obtenerPorId(int id) {
        String sql = "SELECT * FROM incidencias WHERE id = ?";
        Incidencia incidencia = null;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                incidencia = construirIncidencia(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo incidencia por ID: " + e.getMessage());
        }

        return incidencia;
    }

    /**
     * Método que obtiene una lista con todas las incidencias de la BD.
     *
     * @return lista de incidencias
     */
    public List<Incidencia> obtenerTodas() {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM incidencias";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirIncidencia(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo todas las incidencias: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método que actualiza una incidencia existente.
     *
     * @param incidencia objeto Incidencia con los nuevos datos
     * @return true si la actualización fue correcta
     */
    public boolean actualizar(Incidencia incidencia) {
        String sql = "UPDATE incidencias SET tipo=?, estado=?, comentarios=?, usuario_id=?, turno_id=? WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, incidencia.getTipo());
            stmt.setString(2, incidencia.getEstado());
            stmt.setString(3, incidencia.getComentarios());
            stmt.setInt(4, incidencia.getUsuarioId());
            stmt.setInt(5, incidencia.getTurnoId());
            stmt.setInt(6, incidencia.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando incidencia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que elimina una incidencia de la BD.
     *
     * @param id identificador de la incidencia
     * @return true si la operación fue correcta
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM incidencias WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error eliminando incidencia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que construye un objeto Incidencia desde un ResultSet.
     *
     * @param rs datos de la consulta
     * @return objeto Incidencia construido
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Incidencia construirIncidencia(ResultSet rs) throws SQLException {
        return new Incidencia(
                rs.getInt("id"),
                rs.getString("tipo"),
                rs.getString("estado"),
                rs.getString("comentarios"),
                rs.getInt("usuario_id"),
                rs.getInt("turno_id")
        );
    }

    public int contarInformesPendientes(int idNegocio) {
        String sql = "SELECT COUNT(*)\n" +
                "        FROM incidencia\n" +
                "        WHERE id_negocio = ?\n" +
                "          AND estado = 'pendiente'";

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
            System.out.println("Error al contar informes pendientes: " + e.getMessage());
        }

        return conteo;
    }

    public List<Map<String, Object>> obtenerIncidenciasConNombre(Integer usuarioIdFiltro) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT i.*, u.nombre, u.apellidos FROM incidencias i " +
                "JOIN usuarios u ON i.usuario_id = u.id " +
                (usuarioIdFiltro != null ? "WHERE i.usuario_id = ?" : "") +
                " ORDER BY i.id DESC";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (usuarioIdFiltro != null) ps.setInt(1, usuarioIdFiltro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                Incidencia inc = new Incidencia(
                        rs.getInt("id"),
                        rs.getString("tipo"),
                        rs.getString("estado"),
                        rs.getString("comentarios"),
                        rs.getInt("usuario_id"),
                        rs.getInt("turno_id")
                );
                fila.put("incidencia", inc);
                fila.put("nombreEmpleado", rs.getString("nombre") + " " + rs.getString("apellidos"));
                lista.add(fila);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

}
