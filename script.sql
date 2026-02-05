--- SCRIPT CHRONOSYNC
--- Salvador Rodríguez Fernández y Elena García Fernández

--- TABLA NEGOCIO
CREATE TABLE IF NOT EXISTS negocio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(50),
    email VARCHAR(255) NOT NULL UNIQUE,
    codigo_union VARCHAR(20)
) ENGINE=InnoDB;

--- TABLA ROL
CREATE TABLE IF NOT EXISTS rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_rol ENUM('Administrador', 'Empleado') NOT NULL,
    descripcion TEXT NOT NULL
) ENGINE=InnoDB;

--- TABLA USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 0,
    rol_id INT DEFAULT NULL,
    negocio_id INT DEFAULT NULL,
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE SET NULL,
    FOREIGN KEY (negocio_id) REFERENCES negocio(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--- TABLA TURNO
CREATE TABLE IF NOT EXISTS turno (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    tipo ENUM('Mañana', 'Tarde', 'Noche', 'Complementario') NOT NULL,
    estado ENUM('Activo', 'Anulado', 'Sustituido') NOT NULL,
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--- TABLA INCIDENCIAS
CREATE TABLE IF NOT EXISTS incidencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('Ausencia', 'Cambio', 'Retraso', 'Otra') NOT NULL,
    estado ENUM('Pendiente', 'Aceptada', 'Rechazada') NOT NULL,
    comentarios TEXT,
    usuario_id INT NOT NULL,
    turno_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (turno_id) REFERENCES turno(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--- TABLA EXPORTACIÓN
CREATE TABLE IF NOT EXISTS exportacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_formato ENUM('PDF', 'EXCEL', 'CSV') NOT NULL,
    fecha_generacion DATETIME NOT NULL,
    usuario_id INT NOT NULL,
    negocio_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (negocio_id) REFERENCES negocio(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--- INSERTAMOS LOS ROLES QUE LA APLICACIÓN TENDRÁ POR DEFECTO
INSERT INTO rol (tipo_rol, descripcion)
VALUES
    ('Administrador', 'Administrador del negocio'),
    ('Empleado', 'Empleado con permisos limitados');