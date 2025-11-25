package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.HashUtil;
import org.chronosync.proyecto.util.SesionUtil;

public class LoginControlador {

    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private Label txtCuentaNoExiste;
    @FXML private Button btnIniciarSesion;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        // Al pulsar enter, intentar loguearse
        fieldPassword.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                intentarLogin();
            }
        });

        // Al hacer clic en el texto "No estás registrado", te lleva al registro
        txtCuentaNoExiste.setOnMouseClicked(this::irRegistro);

        // Al hacer clic en el boton de iniciar sesion, te lleva el menu principal
        btnIniciarSesion.setOnMouseClicked(e -> intentarLogin());
    }

    private void intentarLogin() {
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        // Si los campos del email y la contraseña están vacíos, se muestra una alerta
        if (email.isEmpty() || password.isEmpty()) {
            verAlerta("Campos vacíos", "Rellena todos los campos");
            return;
        }

        // Si el email no existe en ningún usuario, se muestra una alerta
        Usuario u = usuarioDAO.obtenerPorEmail(email);
        if (u == null) {
            verAlerta("Error", "Email incorrecto");
            return;
        }

        // Si la contraseña no coincide con la contraseña guardada, se muestra una alerta
        String hash = HashUtil.sha256(password);
        if (!hash.equals(u.getPassword())) {
            verAlerta("Error", "Contraseña incorrecta");
            return;
        }

        // Guardamos la sesión
        SesionUtil.obtenerInstancia().setUsuario(u);

        // Redirigimos (provisional)
        verAlerta("Éxito", "Inicio de sesión correcto");

        Stage stage = (Stage) fieldEmail.getScene().getWindow();

        // Si nuestra cuenta no tiene asignado ningún negocio mostramos crear/unirse
        // Si tiene un negocio mostramos el panel principal
        if (u.getNegocioId() == null) {
            CargadorUtil.cambiarEscena(stage, "/fxml/crearUnirseNegocio.fxml");
        } else {
            CargadorUtil.cambiarEscena(stage, "/fxml/menuAdmin.fxml");
        }

    }

    private void irRegistro(MouseEvent event) {
        Stage stage = (Stage) txtCuentaNoExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/register.fxml");
    }

    private void verAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

}
