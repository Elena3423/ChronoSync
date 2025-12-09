package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Turno;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

}