package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.AlertaUtil;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.HashUtil;

public class RegistroControlador {

    // Conectamos los elementos del FXML
    @FXML private TextField fieldNombre;
    @FXML private TextField fieldApellido;
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private PasswordField fieldPassword2;
    @FXML private Label txtCuentaExiste;
    @FXML private Button btnRegistrar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        // ENTER en el último campo = registrar
        fieldPassword2.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                registrar();
            }
        });

        // El texto "Inicia sesión" vuelve al login
        txtCuentaExiste.setOnMouseClicked(this::volverLoginEnter);

        // Acción del botón principal
        btnRegistrar.setOnMouseClicked(e -> registrar());
    }

    /**
     * Método que contiene la lógica para registrar a un usuario
     */
    private void registrar() {
        // Limpiamos espacios en blancos accidentales
        String nombre = fieldNombre.getText().trim();
        String apellido = fieldApellido.getText().trim();
        String email = fieldEmail.getText().trim();
        String pass = fieldPassword.getText();
        String pass2 = fieldPassword2.getText();

        // Verificamos que no falte ningún dato
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            AlertaUtil.mostrarError("Campos vacíos", "Rellena todos los campos.");
            return;
        }

        // Comprobamos que las dos contraseñas sean iguales
        if (!pass.equals(pass2)) {
            AlertaUtil.mostrarError("Error", "Las contraseñas no coinciden.");
            return;
        }

        // Comprobamos si el email ya está registrado por otro usuario
        if (usuarioDAO.obtenerPorEmail(email) != null) {
            AlertaUtil.mostrarError("Error", "Ya existe un usuario con ese correo.");
            return;
        }

        // Creamos el objeto usuario y guardamos la contraseña de forma segura
        Usuario nuevo = new Usuario(nombre, apellido, email, HashUtil.sha256(pass));
        boolean insercion = usuarioDAO.insertar(nuevo);

        // Verificamos si la BD aceptó el registro
        if (!insercion) {
            AlertaUtil.mostrarError("Error", "No se pudo registrar el usuario.");
            return;
        }

        AlertaUtil.mostrarInfo("Registrado", "Usuario creado correctamente.");

        // Volvemos al login para que el usuario entre con sus credenciales
        volverLoginEnter(null);
    }

    /**
     *Método que envia al usuario al login
     *
     * @param e evento del ratón
     */
    private void volverLoginEnter(MouseEvent e) {
        Stage stage = (Stage) txtCuentaExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/login.fxml");
    }

}
