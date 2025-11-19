package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    /**
     * Método que inserta un nuevo rol en la base de datos.
     *
     * @param rol objeto Rol a insertar
     * @return true si la operación fue exitosa; false si ocurrió un error
     */
    public boolean insertar(Rol rol) {
        String sql = "INSERT INTO rol (tipo_rol, descripcion) VALUES (?, ?)";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignamos los parámetros
            stmt.setString(1, rol.getTipoRol());
            stmt.setString(2, rol.getDescripcion());

            // Devolvemos true si se ha insertado al menos 1 línea
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertando rol: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que obtiene un rol por su ID.
     *
     * @param id identificador del rol
     * @return Rol encontrado o null si no existe
     */
    public Rol obtenerPorId(int id) {
        String sql = "SELECT * FROM rol WHERE id = ?";
        Rol rol = null;

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                rol = construirRol(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo rol por ID: " + e.getMessage());
        }

        return rol;
    }

    /**
     * Método que obtiene una lista con todos los roles registrados en la BD.
     *
     * @return lista de roles
     */
    public List<Rol> obtenerTodos() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM rol";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirRol(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo todos los roles: " + e.getMessage());
        }

        return lista;
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
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Rol construirRol(ResultSet rs) throws SQLException {
        return new Rol(
                rs.getInt("id"),
                rs.getString("tipo_rol"),
                rs.getString("descripcion")
        );
    }
}