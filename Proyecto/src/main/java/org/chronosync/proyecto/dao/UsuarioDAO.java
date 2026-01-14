package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Método que inserta un nuevo usuario en la base de datos.
     *
     * @param usuario objeto Usuario a insertar
     * @return true si la operación fue exitosa; false si ocurrió un error
     */
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellidos, email, password, activo, rol_id, negocio_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidos());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPassword());
            stmt.setInt(5, usuario.isActivo() ? 1 : 0);
            stmt.setObject(6, usuario.getRolId());
            stmt.setObject(7, usuario.getNegocioId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertando usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que actualiza los datos de un usuario existente en la BD.
     *
     * @param usuario usuario con los nuevos datos
     * @return devuelve true si la actualización fue correcta
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre=?, apellidos=?, email=?, password=?, activo=?, rol_id=?, negocio_id=? WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidos());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPassword());
            stmt.setInt(5, usuario.isActivo() ? 1 : 0);
            stmt.setObject(6, usuario.getRolId());
            stmt.setObject(7, usuario.getNegocioId());
            stmt.setInt(8, usuario.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que elimina un usuario de la BD según su ID.
     *
     * @param id identificador del usuario
     * @return devuelve true si la operación se realizó correctamente
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error eliminando usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que construye un objeto Usuario a partir de los datos de un ResultSet.
     *
     * @param rs fila devuelta por la consulta
     * @return objeto Usuario construido
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Usuario construirUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getInt("activo") == 1,
                rs.getObject("rol_id", Integer.class),
                rs.getObject("negocio_id", Integer.class)
        );
    }

    /**
     * Método que obtiene un usuario de la BD a partir de su email.
     * Funciona como método auxiliar para login.
     *
     * @param email email del usuario a buscar
     * @return devuelve Usuario encontrado o null si no existe
     */
    public Usuario obtenerPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?)";
        Usuario usuario = null;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = construirUsuario(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo usuario por email: " + e.getMessage());
        }

        return usuario;
    }

    /**
     * Método que obtiene una lista con todos los usuarios registrados en la BD.
     *
     * @return devuelve una lista con todos los usuarios registrados
     */
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirUsuario(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo todos los usuarios: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método que obtiene una lista con todos los empleados de un negocio registrados en la BD.
     *
     * @return devuelve una lista con todos los empleados de un negocio
     */
    public List<Usuario> obtenerTodosLosEmpleados() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol_id = 2";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setRolId(rs.getInt("rol_id"));
                lista.add(u);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar los empleados: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Método que establece a un usuario como administrador de un negocio
     *
     * @param idUsuario id del usuario al que queremos hacer admin
     * @param idNegocio id del negocio al que pertenece el usuario
     * @return devuelve true si se ha actualizado al menos un registro del BD
     */
    public boolean asignarAdminNegocio(int idUsuario, int idNegocio) {
        String sql = "UPDATE usuarios SET activo = 1, rol_id = 1, negocio_id = ? WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idNegocio);
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error asignando admin al negocio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que establece a un usuario como empleado de un negocio
     *
     * @param idUsuario id del usuario al que queremos hacer empleado
     * @param idNegocio id del negocio al que pertenece el usuario
     * @return devuelve true si se ha actualizado al menos un registro del BD
     */
    public boolean asignarEmpleadoNegocio(int idUsuario, int idNegocio) {
        String sql = "UPDATE usuarios SET activo = 1, rol_id = 2, negocio_id = ? WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idNegocio);
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error asignando empleado a negocio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que cuenta los usuarios que hay activos en un negocio
     *
     * @param idNegocio id del negocio al que pertenece el usuario
     * @return devuelve la cantidad de empleados activos del negocio al que pertenece
     */
    public int contarUsuariosActivosPorNegocio(int idNegocio) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE negocio_id = ? AND activo = 1";
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
            System.out.println("Error al contar los usuarios activos por negocio: " + e.getMessage());
        }
        return conteo;
    }

    /**
     * Método que cuenta lo usuario que hay inactivos en un negocio
     *
     * @param idNegocio id del negocio al que pertenece el usuario
     * @return devuelve la cantidad de empleados inactivos del negocio al que pertenece
     */
    public int contarUsuariosInactivosPorNegocio(int idNegocio) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE negocio_id = ? AND activo = 0";
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
            System.out.println("Error al contar los usuarios inactivos por negocio: " + e.getMessage());
        }
        return conteo;
    }

    /**
     * Método que calcula el total de horas trabajadas en un mes de un usuario
     *
     * @param usuarioId id del usuario del que calculará el total de horas trabajadas
     * @return devuelve el total de horas en formato decimal
     */
    public double calcularHorasTrabajadasMes(int usuarioId) {
        String sql = "SELECT SUM(TIMESTAMPDIFF(MINUTE, fecha_inicio, fecha_fin)) " +
                "FROM turno " +
                "WHERE usuario_id = ? " +
                "AND MONTH(fecha_inicio) = MONTH(CURDATE()) " +
                "AND YEAR(fecha_inicio) = YEAR(CURDATE()) " +
                "AND fecha_fin IS NOT NULL";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int minutosTotales = rs.getInt(1);
                return minutosTotales / 60.0;
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular las horás trabajadas en un mes: " + e.getMessage());
        }
        return 0.0;
    }

}