package org.chronosync.proyecto.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de definir y crear todas las tablas necesarias
 * para la base de datos del proyecto (SQLite).
 * Utiliza sentencias SQL de tipo DDL (Data Definition Language)
 */
public class CrearTablas {

    /**
     * Define y ejecuta las sentencias SQL para la creación de todas las tablas.
     * Si las tablas ya existen, no se podrán crear
     */
    public static void crearTablas() {

        // 1. Definición tabla 'negocio'
        // Almacena la información de la empresa
        String sqlNegocio = "CREATE TABLE IF NOT EXISTS negocio (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "nombre TEXT NOT NULL,\n"
                + "direccion TEXT NOT NULL,\n"
                + "telefono TEXT NOT NULL,\n"
                + "email TEXT NOT NULL UNIQUE\n"
                + ");";

        // 2. Definición tabla 'rol'
        // Almacena los tipos de rol disponibles para el usuario
        String sqlRol = "CREATE TABLE IF NOT EXISTS rol (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "tipo_rol TEXT NOT NULL CHECK(tipo_rol IN ('Administrador', 'Empleado')),\n"
                + "descripcion TEXT NOT NULL\n"
                + ");";

        // 3. Definición tabla 'usuarios'
        // Almacena los datos de los usuarios y establece sus relaciones
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "nombre TEXT NOT NULL,\n"
                + "apellidos TEXT NOT NULL,\n"
                + "email TEXT NOT NULL UNIQUE,\n"
                + "activo INTEGER NOT NULL CHECK (activo IN (0,1)),\n"
                + "rol_id INTEGER NOT NULL,\n"
                + "negocio_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY(rol_id) REFERENCES rol(id),\n"
                + "FOREIGN KEY(negocio_id) REFERENCES negocio(id)\n"
                + ");";

        // 4. Definición tabla 'turno'
        // Almacena los turnos de trabajo asignados a cada usuario
        String sqlTurno = "CREATE TABLE IF NOT EXISTS turno (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "fecha_inicio DATETIME NOT NULL,\n"
                + "fecha_fin DATETIME NOT NULL,\n"
                + "tipo TEXT NOT NULL CHECK (tipo IN ('Mañana', 'Tarde', 'Noche', 'Complementario')),\n"
                + "estado TEXT NOT NULL CHECK (estado IN ('Activo', 'Anulado', 'Sustituido')),\n"
                + "usuario_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY(usuario_id) REFERENCES usuarios(id)\n"
                + ");";

        // 5. Definición tabla 'incidencias'
        // Almacena solicitudes o reportes relacionados con usuarios y turnos
        String sqlIncidencias = "CREATE TABLE IF NOT EXISTS incidencias (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "tipo TEXT NOT NULL CHECK (tipo IN ('Ausencia', 'Cambio', 'Retraso', 'Otra')),\n"
                + "estado TEXT NOT NULL CHECK (estado IN ('Pendiente', 'Aceptada', 'Rechazada')),\n"
                + "comentarios TEXT,\n"
                + "usuario_id INTEGER NOT NULL,\n"
                + "turno_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY(usuario_id) REFERENCES usuarios(id),\n"
                + "FOREIGN KEY(turno_id) REFERENCES turno(id)\n"
                + ");";

        // 6. Definición tabla 'exportacion'
        // Registra las operaciones de exportación de datos realizadas
        String sqlExportacion = "CREATE TABLE IF NOT EXISTS exportacion (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "tipo_formato TEXT NOT NULL CHECK (tipo_formato IN ('PDF', 'EXCEL', 'CSV')),\n"
                + "fecha_generacion DATETIME NOT NULL,\n"
                + "usuario_id INTEGER NOT NULL,\n"
                + "negocio_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY(usuario_id) REFERENCES usuarios(id),\n"
                + "FOREIGN KEY(negocio_id) REFERENCES negocio(id)\n"
                + ");";

        // Obtenemos la conexión activa y la gestiona automáticamente
        // Creamos un objeto Statement para poder ejecutar comandos SQL
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement()) {

            // Ejecutamos las sentencias DDL para crear cada tabla
            stmt.execute(sqlNegocio);
            stmt.execute(sqlRol);
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlTurno);
            stmt.execute(sqlIncidencias);
            stmt.execute(sqlExportacion);

            // Mensajes de confirmación
            System.out.println("Tabla 'NEGOCIO' ha sido creada o ya existe");
            System.out.println("Tabla 'ROL' ha sido creada o ya existe");
            System.out.println("Tabla 'USUARIOS' ha sido creada o ya existe");
            System.out.println("Tabla 'TURNO' ha sido creada o ya existe");
            System.out.println("Tabla 'INCIDENCIAS' ha sido creada o ya existe");
            System.out.println("Tabla 'EXPORTACION' ha sido creada o ya existe");

        } catch (SQLException e) {
            // Si falla la conexión o alguna setencia SQL, muestra el error por consola
            System.err.println("Error al crear la tabla: " + e.getMessage());
        }
    }
}
