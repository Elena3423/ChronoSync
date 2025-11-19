package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Negocio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NegocioDAO {

    /**
     * Método que inserta un nuevo negocio en la base de datos.
     *
     * @param negocio objeto Negocio a insertar
     * @return true si la operación fue exitosa; false si ocurrió un error
     */
    public boolean insertar(Negocio negocio) {
        String sql = "INSERT INTO negocio (nombre, direccion, telefono, email) VALUES (?, ?, ?, ?)";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignamos los parámetros
            stmt.setString(1, negocio.getNombre());
            stmt.setString(2, negocio.getDireccion());
            stmt.setString(3, negocio.getTelefono());
            stmt.setString(4, negocio.getEmail());

            // Devolvemos true si se ha insertado al menos 1 línea
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertando negocio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que obtiene un negocio según su ID.
     *
     * @param id identificador del negocio
     * @return Negocio encontrado o null si no existe
     */
    public Negocio obtenerPorId(int id) {
        String sql = "SELECT * FROM negocio WHERE id = ?";
        Negocio negocio = null;

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                negocio = construirNegocio(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo negocio por ID: " + e.getMessage());
        }

        return negocio;
    }

    /**
     * Método que obtiene una lista con todos los negocios registrados en la BD.
     *
     * @return lista de negocios
     */
    public List<Negocio> obtenerTodos() {
        List<Negocio> lista = new ArrayList<>();
        String sql = "SELECT * FROM negocio";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirNegocio(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo todos los negocios: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método que actualiza un negocio existente en la BD.
     *
     * @param negocio objeto Negocio con los nuevos datos
     * @return true si la actualización fue correcta
     */
    public boolean actualizar(Negocio negocio) {
        String sql = "UPDATE negocio SET nombre=?, direccion=?, telefono=?, email=? WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, negocio.getNombre());
            stmt.setString(2, negocio.getDireccion());
            stmt.setString(3, negocio.getTelefono());
            stmt.setString(4, negocio.getEmail());
            stmt.setInt(5, negocio.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando negocio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que elimina un negocio de la BD.
     *
     * @param id identificador del negocio
     * @return true si la operación fue correcta
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM negocio WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error eliminando negocio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que construye un objeto Negocio a partir del ResultSet.
     *
     * @param rs fila de la consulta
     * @return objeto Negocio construido
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Negocio construirNegocio(ResultSet rs) throws SQLException {
        return new Negocio(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("direccion"),
                rs.getString("telefono"),
                rs.getString("email")
        );
    }
}