package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Turno;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnoDAO {

    /**
     * Método que inserta un nuevo turno en la base de datos.
     *
     * @param turno objeto Turno a insertar
     * @return devuelve true si la operación fue exitosa; false si ocurrió un error
     */
    public boolean insertar(Turno turno) {
        String sql = "INSERT INTO turno (fecha_inicio, fecha_fin, tipo, estado, usuario_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, turno.getFechaInicio().toString());
            stmt.setString(2, turno.getFechaFin().toString());
            stmt.setString(3, turno.getTipo());
            stmt.setString(4, turno.getEstado());
            stmt.setInt(5, turno.getUsuarioId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertando turno: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que actualiza un turno existente.
     *
     * @param turno objeto Turno con nuevos datos
     * @return devuelve true si la actualización fue correcta
     */
    public boolean actualizar(Turno turno) {
        String sql = "UPDATE turno SET fecha_inicio=?, fecha_fin=?, tipo=?, estado=?, usuario_id=? WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, turno.getFechaInicio().toString());
            stmt.setString(2, turno.getFechaFin().toString());
            stmt.setString(3, turno.getTipo());
            stmt.setString(4, turno.getEstado());
            stmt.setInt(5, turno.getUsuarioId());
            stmt.setInt(6, turno.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando turno: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que elimina un turno de la BD.
     *
     * @param id identificador del turno
     * @return devuelve true si la operación fue correcta
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM turno WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error eliminando turno: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que inserta un turno a la BD
     *
     * @param usuarioId id del usuario al que pertenece el turno
     * @param tipo tipo de turno de trabajo (mañana, tarde, noche)
     * @param fecha hora y fecha del turno de trabajo
     * @return devuelve true si la operación fue correcta
     */
    public boolean insertarTurno(int usuarioId, String tipo, LocalDate fecha) {

        int horaInicio = switch (tipo.toLowerCase()) {
            case "mañana" -> 6;
            case "tarde" -> 14;
            case "noche" -> 22;
            default -> 8;
        };

        // Calculamos inicio y fin (8 horas después)
        java.time.LocalDateTime inicio = fecha.atTime(horaInicio, 0);
        java.time.LocalDateTime fin = inicio.plusHours(8);

        String sql = "INSERT INTO turno (usuario_id, tipo, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setString(2, tipo);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(inicio));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(fin));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método que obtiene un Map de los turnos que hay en cada dia de trabajo
     *
     * @param ym año y mes del turno
     * @param usuarioId id del usuario al que pertenece el turno
     * @return devuelve el mapa con la lista de turnos de un mes
     */
    public Map<LocalDate, List<String>> obtenerTurnosDelMes(YearMonth ym, Integer usuarioId) {
        Map<LocalDate, List<String>> mapa = new HashMap<>();

        String sql = "SELECT t.id, t.fecha_inicio, t.tipo, u.nombre " +
                "FROM turno t " +
                "JOIN usuarios u ON t.usuario_id = u.id " +
                "WHERE MONTH(t.fecha_inicio) = ? AND YEAR(t.fecha_inicio) = ? " +
                (usuarioId != null ? "AND t.usuario_id = ? " : "");

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ym.getMonthValue());
            ps.setInt(2, ym.getYear());
            if (usuarioId != null) {
                ps.setInt(3, usuarioId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha_inicio").toLocalDate();
                String info = rs.getInt("id") + "#" + rs.getString("nombre") + " - " + rs.getString("tipo");
                mapa.computeIfAbsent(fecha, k -> new ArrayList<>()).add(info);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapa;
    }

    /**
     * Método que obtiene una lista de turnos en función de los filtros seleccionados en el apartado de exportaciones
     *
     * @param idEmpleado id del empleado que ejecuta la acción
     * @param periodo periodo seleccionado desde el menú
     * @return
     */
    public List<Turno> obtenerTurnosPorFiltro(int idEmpleado, String periodo) {
        List<Turno> lista = new ArrayList<>();
        String condicionFecha = "";

        switch (periodo) {
            case "Semana actual":
                condicionFecha = "YEARWEEK(fecha_inicio, 1) = YEARWEEK(CURDATE(), 1)";
                break;
            case "Mes actual":
                condicionFecha = "MONTH(fecha_inicio) = MONTH(CURDATE()) AND YEAR(fecha_inicio) = YEAR(CURDATE())";
                break;
            case "Mes anterior":
                condicionFecha = "MONTH(fecha_inicio) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) " +
                        "AND YEAR(fecha_inicio) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))";
                break;
            default:
                condicionFecha = "1=1";
        }

        String sql = "SELECT * FROM turno WHERE usuario_id = ? AND " + condicionFecha;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Turno t = new Turno();
                t.setId(rs.getInt("id"));
                t.setFechaInicio(rs.getTimestamp("fecha_inicio").toLocalDateTime());
                t.setFechaFin(rs.getTimestamp("fecha_fin") != null ?
                        rs.getTimestamp("fecha_fin").toLocalDateTime() : null);
                t.setTipo(rs.getString("tipo"));
                t.setEstado(rs.getString("estado"));
                t.setUsuarioId(rs.getInt("usuario_id"));
                lista.add(t);
            }

        } catch (SQLException e) {
            System.err.println("Error en TurnoDAO: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método que obtiene una lista de turnos para mostrar las horas trabajadas
     *
     * @param usuarioId id del usuario que ejecuta la acción
     * @param periodo periodo seleccionado desde el menú
     * @return
     */
    public List<Turno> obtenerResumenHoras(int usuarioId, String periodo) {
        List<Turno> lista = new ArrayList<>();
        String condicionFecha = "";

        switch (periodo) {
            case "Semana actual":
                condicionFecha = "AND YEARWEEK(fecha_inicio, 1) = YEARWEEK(CURDATE(), 1)";
                break;
            case "Mes actual":
                condicionFecha = "AND MONTH(fecha_inicio) = MONTH(CURDATE()) AND YEAR(fecha_inicio) = YEAR(CURDATE())";
                break;
            case "Mes anterior":
                condicionFecha = "AND MONTH(fecha_inicio) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))";
                break;
            default: condicionFecha = "";
        }

        String sql = "SELECT * FROM turno WHERE usuario_id = ? AND fecha_fin IS NOT NULL " + condicionFecha + " ORDER BY fecha_inicio DESC";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Turno t = new Turno();
                t.setFechaInicio(rs.getTimestamp("fecha_inicio").toLocalDateTime());
                t.setFechaFin(rs.getTimestamp("fecha_fin").toLocalDateTime());
                t.setTipo(rs.getString("tipo"));
                lista.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Método que cuenta el total de turnos en un mes de un usuario
     *
     * @param usuarioId id del usuario al que hace referencia
     * @return devuelve el total de turnos asginados a un usuario
     */
    public int contarTurnosMesUsuario(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM turno " +
                "WHERE usuario_id = ? " +
                "AND MONTH(fecha_inicio) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(fecha_inicio) = YEAR(CURRENT_DATE())";

        int total = 0;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al contar turnos del usuario: " + e.getMessage());
        }

        return total;
    }

    /**
     * Actualiza la franja horaria de un turno existente recalculando las horas de inicio y fin.
     */
    public boolean actualizarTipoTurno(int idTurno, String nuevoTipo) {
        // Primero necesitamos saber la fecha original para mantenerla
        String sqlSelect = "SELECT fecha_inicio FROM turno WHERE id = ?";
        String sqlUpdate = "UPDATE turno SET tipo = ?, fecha_inicio = ?, fecha_fin = ? WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            LocalDate fechaOriginal;
            try (PreparedStatement psSel = conn.prepareStatement(sqlSelect)) {
                psSel.setInt(1, idTurno);
                ResultSet rs = psSel.executeQuery();
                if (rs.next()) {
                    fechaOriginal = rs.getTimestamp("fecha_inicio").toLocalDateTime().toLocalDate();
                } else {
                    return false;
                }
            }

            // Calculamos nuevas horas
            int horaInicio = switch (nuevoTipo.toLowerCase()) {
                case "mañana" -> 6;
                case "tarde" -> 14;
                case "noche" -> 22;
                default -> 8;
            };

            java.time.LocalDateTime inicio = fechaOriginal.atTime(horaInicio, 0);
            java.time.LocalDateTime fin = inicio.plusHours(8);

            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)) {
                psUpd.setString(1, nuevoTipo);
                psUpd.setTimestamp(2, java.sql.Timestamp.valueOf(inicio));
                psUpd.setTimestamp(3, java.sql.Timestamp.valueOf(fin));
                psUpd.setInt(4, idTurno);
                return psUpd.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}