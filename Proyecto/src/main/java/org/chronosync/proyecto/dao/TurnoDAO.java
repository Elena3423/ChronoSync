package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Turno;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @return true si la operación fue exitosa; false si ocurrió un error
     */
    public boolean insertar(Turno turno) {
        String sql = "INSERT INTO turno (fecha_inicio, fecha_fin, tipo, estado, usuario_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        // Obtenemos la conexión
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
     * Método que obtiene un turno según su ID.
     *
     * @param id identificador del turno
     * @return Turno encontrado o null si no existe
     */
    public Turno obtenerPorId(int id) {
        String sql = "SELECT * FROM turno WHERE id = ?";
        Turno turno = null;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                turno = construirTurno(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo turno por ID: " + e.getMessage());
        }

        return turno;
    }

    /**
     * Método que obtiene una lista con todos los turnos de la BD.
     *
     * @return lista de turnos
     */
    public List<Turno> obtenerTodos() {
        List<Turno> lista = new ArrayList<>();
        String sql = "SELECT * FROM turno";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirTurno(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo todos los turnos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método que actualiza un turno existente.
     *
     * @param turno objeto Turno con nuevos datos
     * @return true si la actualización fue correcta
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
     * @return true si la operación fue correcta
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
     * Método que construye un objeto Turno desde un ResultSet.
     *
     * @param rs datos de la consulta
     * @return objeto Turno construido
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Turno construirTurno(ResultSet rs) throws SQLException {
        return new Turno(
                rs.getInt("id"),
                LocalDateTime.parse(rs.getString("fecha_inicio")),
                LocalDateTime.parse(rs.getString("fecha_fin")),
                rs.getString("tipo"),
                rs.getString("estado"),
                rs.getInt("usuario_id")
        );
    }

    public int contarTurnosMesUsuario(int usuarioId) {
        // Consulta que filtra por ID de usuario y el mes/año actual
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

    public boolean insertarTurnoRapido(int usuarioId, String tipo) {
        String sql = "INSERT INTO turno (fecha_inicio, fecha_fin, tipo, estado, usuario_id) " +
                "VALUES (NOW(), DATE_ADD(NOW(), INTERVAL 8 HOUR), ?, 'Activo', ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            stmt.setInt(2, usuarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<LocalDate, List<String>> obtenerTurnosDelMes(YearMonth ym, Integer usuarioId) {
        Map<LocalDate, List<String>> mapa = new HashMap<>();

        // Consulta dinámica: si hay usuarioId, añadimos el filtro AND t.usuario_id = ?
        String sql = "SELECT t.fecha_inicio, t.tipo, u.nombre " +
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
                // Guardamos "Nombre - Tipo" para que el controlador sepa separar el texto del color
                String info = rs.getString("nombre") + " - " + rs.getString("tipo");
                mapa.computeIfAbsent(fecha, k -> new ArrayList<>()).add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }

    public boolean insertarTurno(int usuarioId, String tipo, LocalDate fecha) {
        // Definimos la hora de inicio según el turno
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

    public List<Turno> obtenerTurnosPorFiltro(int idEmpleado, String periodo) {
        List<Turno> lista = new ArrayList<>();
        String condicionFecha = "";

        // Lógica de fechas corregida para MySQL
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
                condicionFecha = "1=1"; // Traer todo si no coincide
        }

        String sql = "SELECT * FROM turno WHERE usuario_id = ? AND " + condicionFecha;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Turno t = new Turno();
                t.setId(rs.getInt("id"));
                // Usamos getTimestamp para convertir a LocalDateTime
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

}