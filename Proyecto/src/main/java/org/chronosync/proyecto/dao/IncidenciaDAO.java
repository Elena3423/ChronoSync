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

            stmt.setString(1, incidencia.getTipo());
            stmt.setString(2, incidencia.getEstado());
            stmt.setString(3, incidencia.getComentarios());
            stmt.setInt(4, incidencia.getUsuarioId());
            stmt.setInt(5, incidencia.getTurnoId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL en insertar incidencia: " + e.getMessage());
            return false;
        }
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
     * Método que actualiza el estado de una incidencia
     *
     * @param id id de la incidencia a la que hace referencia
     * @param nuevoEstado estado que asignaremos
     * @return devuelve true si se ha realizado la modificación
     */
    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE incidencias SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Método que cuenta la cantidad de informes pendientes de un negocio
     *
     * @param negocioId id del negocio al que hace referencia
     * @return devuelve el total de informes pendientes
     */
    public int contarInformesPendientes(int negocioId) {
        String sql = "SELECT COUNT(i.id) FROM incidencias i " +
                "JOIN usuarios u ON i.usuario_id = u.id " +
                "WHERE u.negocio_id = ? AND i.estado != 'Resuelta'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, negocioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al contar informes: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Método que cuenta la cantidad de incidencias de un usuario
     *
     * @param usuarioId id del usuario al que hace referencia
     * @return devuelve el total de incidencias del usuario
     */
    public int contarIncidenciasUsuario(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM incidencias WHERE usuario_id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error en al contar las incidencias: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Método que obtiene una lista de incidencias junto con el nombre del usuario asociado,
     * filtrando por negocio y, opcionalmente, por un usuario específico.
     *
     * @param usuarioIdFiltro id del usuario (si es empleado) o null (si es admin)
     * @param negocioIdFiltro id del negocio del usuario conectado
     * @return devuelve una lista de mapas que contiene un objeto incidencia
     */
    public List<Map<String, Object>> obtenerIncidenciasConNombre(Integer usuarioIdFiltro, int negocioIdFiltro) {
        List<Map<String, Object>> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT i.*, u.nombre, u.apellidos FROM incidencias i " +
                        "JOIN usuarios u ON i.usuario_id = u.id " +
                        "WHERE u.negocio_id = ? "
        );

        if (usuarioIdFiltro != null) {
            sql.append("AND i.usuario_id = ? ");
        }

        sql.append("ORDER BY i.id DESC");

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, negocioIdFiltro);

            if (usuarioIdFiltro != null) {
                ps.setInt(2, usuarioIdFiltro);
            }

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

        } catch (SQLException e) {
            System.err.println("Error al obtener incidencias con nombre: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método que obtiene una lista de incidencias en función de unos filtros
     * seleccionados en el apartado de exportaciones.
     */
    public List<Incidencia> obtenerIncidenciasPorFiltro(int usuarioId, String periodo, String estado) {
        List<Incidencia> lista = new ArrayList<>();

        // AJUSTE: Cambiado 'turnos' por 'turno' y 'fecha' por 'fecha_inicio'
        StringBuilder sql = new StringBuilder(
                "SELECT i.* FROM incidencias i " +
                        "JOIN turno t ON i.turno_id = t.id " +
                        "WHERE i.usuario_id = ?"
        );

        if (estado != null && !estado.equalsIgnoreCase("Todos")) {
            sql.append(" AND i.estado = ?");
        }

        switch (periodo) {
            case "Semana actual":
                sql.append(" AND YEARWEEK(t.fecha_inicio, 1) = YEARWEEK(CURDATE(), 1)");
                break;
            case "Mes actual":
                sql.append(" AND MONTH(t.fecha_inicio) = MONTH(CURDATE()) AND YEAR(t.fecha_inicio) = YEAR(CURDATE())");
                break;
            case "Mes anterior":
                sql.append(" AND MONTH(t.fecha_inicio) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) " +
                        "AND YEAR(t.fecha_inicio) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))");
                break;
        }

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setInt(1, usuarioId);

            if (estado != null && !estado.equalsIgnoreCase("Todos")) {
                stmt.setString(2, estado);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Incidencia inc = new Incidencia(
                        rs.getInt("id"),
                        rs.getString("tipo"),
                        rs.getString("estado"),
                        rs.getString("comentarios"),
                        rs.getInt("usuario_id"),
                        rs.getInt("turno_id")
                );
                lista.add(inc);
            }

        } catch (SQLException e) {
            System.err.println("Error en obtenerIncidenciasPorFiltro: " + e.getMessage());
        }

        return lista;
    }
}