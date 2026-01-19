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
import org.chronosync.proyecto.util.SesionUtil;

public class LoginControlador {

    // Conectamos los elementos del FXML
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private Label txtCuentaNoExiste;
    @FXML private Button btnIniciarSesion;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        // Al pulsar enter, intentar loguearse
        fieldPassword.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                intentarLogin();
            }
        });

        // Al hacer clic en el texto te lleva al registro
        txtCuentaNoExiste.setOnMouseClicked(this::irRegistro);

        // Al hacer clic en el botÓn de iniciar sesión te lleva el menu principal
        btnIniciarSesion.setOnMouseClicked(e -> intentarLogin());
    }

    /**
     * Lógica que comprueba si un usuario puede pasar o no
     */
    private void intentarLogin() {
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        // Si los campos del email y la contraseña están vacíos, se muestra una alerta
        if (email.isEmpty() || password.isEmpty()) {
            AlertaUtil.mostrarAdvertencia("Campos vacíos", "Rellena todos los campos");
            return;
        }

        // Si el email no existe en ningún usuario, se muestra una alerta
        Usuario u = usuarioDAO.obtenerPorEmail(email);
        if (u == null) {
            AlertaUtil.mostrarError("Error", "Email incorrecto");
            return;
        }

        // Si la contraseña no coincide con la contraseña guardada, se muestra una alerta
        String hash = HashUtil.sha256(password);
        if (!hash.equals(u.getPassword())) {
            AlertaUtil.mostrarError("Error", "Contraseña incorrecta");
            return;
        }

        // Guardamos la sesión
        SesionUtil.obtenerInstancia().setUsuario(u);

        // Redirigimos (provisional)
        AlertaUtil.mostrarInfo("Éxito", "Inicio de sesión correcto");

        Stage stage = (Stage) fieldEmail.getScene().getWindow();

        // Si nuestra cuenta no tiene asignado ningún negocio mostramos crear/unirse
        // Si tiene un negocio mostramos el panel principal
        if (u.getNegocioId() == null) {
            CargadorUtil.cambiarEscena(stage, "/fxml/crearUnirseNegocio.fxml");
        } else {
            CargadorUtil.cambiarEscena(stage, "/fxml/menuPrincipal.fxml");
        }

    }

    /**
     * Método que envia al usuario al registro
     * @param event evento del ratón
     */
    private void irRegistro(MouseEvent event) {
        Stage stage = (Stage) txtCuentaNoExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/register.fxml");
    }

}
