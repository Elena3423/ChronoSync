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
        String sqlNegocio = "CREATE TABLE IF NOT EXISTS negocio ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "direccion TEXT, "
                + "telefono TEXT, "
                + "email TEXT NOT NULL UNIQUE CHECK (email LIKE '%@%.%')"
                + ");";

        // 2. Definición tabla 'rol'
        // Almacena los tipos de rol disponibles para el usuario
        String sqlRol = "CREATE TABLE IF NOT EXISTS rol ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tipo_rol TEXT NOT NULL CHECK(tipo_rol IN ('Administrador', 'Empleado')), "
                + "descripcion TEXT NOT NULL"
                + ");";

        // 3. Definición tabla 'usuarios'
        // Almacena los datos de los usuarios y establece sus relaciones
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "apellidos TEXT NOT NULL, "
                + "email TEXT NOT NULL UNIQUE CHECK (email LIKE '%@%.%'), "
                + "password TEXT NOT NULL CHECK (LENGTH(password) >= 6 AND password NOT LIKE '% %'), "
                + "activo INTEGER NOT NULL DEFAULT 0 CHECK (activo IN (0,1)), "
                + "rol_id INTEGER, "
                + "negocio_id INTEGER, "
                + "FOREIGN KEY(rol_id) REFERENCES rol(id), "
                + "FOREIGN KEY(negocio_id) REFERENCES negocio(id)"
                + ");";

        // 4. Definición tabla 'turno'
        // Almacena los turnos de trabajo asignados a cada usuario
        String sqlTurno = "CREATE TABLE IF NOT EXISTS turno ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "fecha_inicio DATETIME NOT NULL, "
                + "fecha_fin DATETIME NOT NULL, "
                + "tipo TEXT NOT NULL CHECK (tipo IN ('Mañana', 'Tarde', 'Noche', 'Complementario')), "
                + "estado TEXT NOT NULL CHECK (estado IN ('Activo', 'Anulado', 'Sustituido')), "
                + "usuario_id INTEGER NOT NULL, "
                + "FOREIGN KEY(usuario_id) REFERENCES usuarios(id)"
                + ");";

        // 5. Definición tabla 'incidencias'
        // Almacena solicitudes o reportes relacionados con usuarios y turnos
        String sqlIncidencias = "CREATE TABLE IF NOT EXISTS incidencias ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tipo TEXT NOT NULL CHECK (tipo IN ('Ausencia', 'Cambio', 'Retraso', 'Otra')), "
                + "estado TEXT NOT NULL CHECK (estado IN ('Pendiente', 'Aceptada', 'Rechazada')), "
                + "comentarios TEXT, "
                + "usuario_id INTEGER NOT NULL, "
                + "turno_id INTEGER NOT NULL, "
                + "FOREIGN KEY(usuario_id) REFERENCES usuarios(id), "
                + "FOREIGN KEY(turno_id) REFERENCES turno(id)"
                + ");";

        // 6. Definición tabla 'exportacion'
        // Registra las operaciones de exportación de datos realizadas
        String sqlExportacion = "CREATE TABLE IF NOT EXISTS exportacion ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tipo_formato TEXT NOT NULL CHECK (tipo_formato IN ('PDF', 'EXCEL', 'CSV')), "
                + "fecha_generacion DATETIME NOT NULL, "
                + "usuario_id INTEGER NOT NULL, "
                + "negocio_id INTEGER NOT NULL, "
                + "FOREIGN KEY(usuario_id) REFERENCES usuarios(id), "
                + "FOREIGN KEY(negocio_id) REFERENCES negocio(id)"
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