package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.HashUtil;

public class RegistroControlador {

    @FXML private TextField fieldNombre;
    @FXML private TextField fieldApellido;
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private PasswordField fieldPassword2;
    @FXML private Label txtCuentaExiste;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        // ENTER en el último campo = registrar
        fieldPassword2.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                registrar();
            }
        });

        // El texto "Inicia sesión" vuelve al login
        txtCuentaExiste.setOnMouseClicked(this::volverLogin);
    }

    private void registrar() {
        String nombre = fieldNombre.getText().trim();
        String apellido = fieldApellido.getText().trim();
        String email = fieldEmail.getText().trim();
        String pass = fieldPassword.getText();
        String pass2 = fieldPassword2.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            showAlert("Campos vacíos", "Rellena todos los campos.");
            return;
        }

        if (!pass.equals(pass2)) {
            showAlert("Error", "Las contraseñas no coinciden.");
            return;
        }

        if (usuarioDAO.obtenerPorEmail(email) != null) {
            showAlert("Error", "Ya existe un usuario con ese correo.");
            return;
        }

        Usuario nuevo = new Usuario(nombre, apellido, email, HashUtil.sha256(pass));
        boolean insercion = usuarioDAO.insertar(nuevo);

        if (!insercion) {
            showAlert("Error", "No se pudo registrar el usuario.");
            return;
        }

        showAlert("Registrado", "Usuario creado correctamente.");

        volverLogin(null);
    }

    private void volverLogin(MouseEvent e) {
        Stage stage = (Stage) txtCuentaExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/login.fxml");
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(c);
        a.showAndWait();
    }
}
