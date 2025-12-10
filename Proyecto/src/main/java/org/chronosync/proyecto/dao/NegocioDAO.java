package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Negocio;
import org.chronosync.proyecto.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NegocioDAO {

    /**
     * Método que inserta un nuevo negocio en la base de datos.
     *
     * @param negocio objeto Negocio a insertar
     * @return true si la operación fue exitosa; false si ocurrió un error
     */
    public Integer insertar(Negocio negocio) {
        String sql = "INSERT INTO negocio (nombre, direccion, telefono, email, codigo_union) VALUES (?, ?, ?, ?, ?)";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asignamos los parámetros
            stmt.setString(1, negocio.getNombre());
            stmt.setString(2, negocio.getDireccion());
            stmt.setString(3, negocio.getTelefono());
            stmt.setString(4, negocio.getEmail());

            String codigoUnion = generarCodigoUnion();
            stmt.setString(5, codigoUnion);

            int filas = stmt.executeUpdate();
            if (filas == 0) return null;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return  rs.getInt(1);
            }

            return null;

        } catch (SQLException e) {
            System.out.println("Error insertando negocio: " + e.getMessage());
            return null;
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

    public Negocio obtenerPorEmail(String email) {
        String sql = "SELECT * FROM negocio WHERE LOWER(email) = LOWER(?)";
        Negocio negocio = null;

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignamos el email al parámetro
            stmt.setString(1, email);
            // Ejecutamos la consulta
            ResultSet rs = stmt.executeQuery();

            // Si existe un resultado
            if (rs.next()) {
                negocio = construirNegocio(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo usuario por email: " + e.getMessage());
        }

        return negocio;
    }

    public Negocio obtenerPorCodigo(String codigo) {
        String sql = "SELECT * FROM negocio WHERE codigo_union = ?";
        Negocio negocio = null;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                negocio = construirNegocio(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo negocio por código: " + e.getMessage());
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

    private String generarCodigoUnion() {
        String letras = "ABDCEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) {
            codigo.append(letras.charAt(rnd.nextInt(letras.length())));
        }

        return codigo.toString();
    }

    public int contarTurnosMesActual(int idNegocio) {
        String sql = "SELECT COUNT(*)\n" +
                "        FROM turno\n" +
                "        WHERE id_negocio = ?\n" +
                "          AND fecha_inicio <= LAST_DAY(CURRENT_DATE())\n" +
                "          AND fecha_fin >= DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')";

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
            System.out.println("Error al contar turnos del mes actual: " + e.getMessage());
        }

        return conteo;
    }
}