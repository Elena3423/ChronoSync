package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Rol;

import java.sql.*;

public class RolDAO {

    /**
     * Método que inserta un nuevo rol en la base de datos.
     *
     * @param rol objeto Rol a insertar
     * @return true si la operación fue exitosa; false si ocurrió un error
     */
    public boolean insertar(Rol rol) {
        String sql = "INSERT INTO rol (tipo_rol, descripcion) VALUES (?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol.getTipoRol());
            stmt.setString(2, rol.getDescripcion());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertando rol: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que actualiza un rol existente en la BD.
     *
     * @param rol objeto Rol con los nuevos datos
     * @return true si la actualización fue correcta
     */
    public boolean actualizar(Rol rol) {
        String sql = "UPDATE rol SET tipo_rol=?, descripcion=? WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol.getTipoRol());
            stmt.setString(2, rol.getDescripcion());
            stmt.setInt(3, rol.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando rol: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que elimina un rol de la BD.
     *
     * @param id identificador del rol
     * @return true si la operación fue correcta
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM rol WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error eliminando rol: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que construye un objeto Rol a partir del ResultSet.
     *
     * @param rs fila de la consulta
     * @return objeto Rol construido
     * @throws SQLException se lanza si ocurre un error al leer los datos
     */
    private Rol construirRol(ResultSet rs) throws SQLException {
        return new Rol(
                rs.getInt("id"),
                rs.getString("tipo_rol"),
                rs.getString("descripcion")
        );
    }
}