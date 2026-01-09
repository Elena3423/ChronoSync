package org.chronosync.proyecto.dao;

import org.chronosync.proyecto.bd.ConexionBD;
import org.chronosync.proyecto.modelo.Usuario;

import javax.swing.text.html.parser.Entity;
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

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignamos parámetros al PreparedStatement
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidos());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPassword());
            stmt.setInt(5, usuario.isActivo() ? 1 : 0);
            stmt.setObject(6, usuario.getRolId());
            stmt.setObject(7, usuario.getNegocioId());

            // Devolvemos true si se ha insertado al menos 1 línea
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertando usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método que obtiene un usuario de la BD a partir de su email.
     * Funciona como método auxiliar para login.
     *
     * @param email email del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    public Usuario obtenerPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?)";
        Usuario usuario = null;

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignamos el email al parámetro
            stmt.setString(1, email);
            // Ejecutamos la consulta
            ResultSet rs = stmt.executeQuery();

            // Si existe un resultado
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
     * @return lista de usuarios
     */
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Mientras haya resultados, añadimos a la lista
            while (rs.next()) {
                lista.add(construirUsuario(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo todos los usuarios: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método que actualiza los datos de un usuario existente en la BD.
     *
     * @param usuario usuario con los nuevos datos
     * @return true si la actualización fue correcta
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre=?, apellidos=?, email=?, password=?, activo=?, rol_id=?, negocio_id=? WHERE id=?";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignamos parámetros al PreparedStatement
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidos());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPassword());
            stmt.setInt(5, usuario.isActivo() ? 1 : 0);
            stmt.setObject(6, usuario.getRolId());
            stmt.setObject(7, usuario.getNegocioId());
            stmt.setInt(8, usuario.getId());

            // Devolvemos true si se ha insertado al menos 1 línea
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
     * @return true si la operación se realizó correctamente
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";

        // Obtenemos la conexión
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignamos el id al parámetro
            stmt.setInt(1, id);
            // Devolvemos true si se ha eliminado al menos 1 línea
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

    public List<Usuario> obtenerTodosLosEmpleados() {
        List<Usuario> lista = new ArrayList<>();
        // Filtramos por rol_id = 2 (Empleados)
        String sql = "SELECT * FROM usuarios WHERE rol_id = 2";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Usamos el constructor de tu clase Usuario
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setRolId(rs.getInt("rol_id"));
                // ... añade el resto de campos que tenga tu modelo
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        }
        return lista;
    }

}