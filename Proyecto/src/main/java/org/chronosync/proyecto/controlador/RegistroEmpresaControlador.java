package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.modelo.Negocio;
import org.chronosync.proyecto.util.CargadorUtil;

public class RegistroEmpresaControlador {

    @FXML TextField fieldNombre;
    @FXML TextField fieldDireccion;
    @FXML TextField fieldTelefono;
    @FXML TextField fieldEmail;
    @FXML TextField fieldRepetirEmail;
    @FXML Button btnRegistrar;
    @FXML Label txtEmpresaExiste;

    private final NegocioDAO negocioDAO = new NegocioDAO();

    @FXML
    public void initialize(){
        // ENTER en el último campo = registrar
        fieldRepetirEmail.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                registrar();
            }
        });

        // El texto "Ya tienes empresa" vuelve al login
        txtEmpresaExiste.setOnMouseClicked(this::volver);

        btnRegistrar.setOnMouseClicked(e -> registrar());
    }

    private void registrar() {
        String nombre = fieldNombre.getText();
        String direccion = fieldDireccion.getText();
        String telefono = fieldTelefono.getText();
        String email = fieldEmail.getText();
        String repetirEmail = fieldRepetirEmail.getText();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || repetirEmail.isEmpty()) {
            showAlert("Campos vacíos", "Rellena todos los campos.");
            return;
        }

        if (!email.equals(repetirEmail)) {
            showAlert("Error", "Los correos no coinciden.");
            return;
        }

        if (negocioDAO.obtenerPorEmail(email) != null) {
            showAlert("Error", "Ya existe un usuario con ese correo.");
            return;
        }

        Negocio negocio = new Negocio(nombre, direccion, telefono, email);
        boolean insercion = negocioDAO.insertar(negocio);

        if (!insercion) {
            showAlert("Error", "No se pudo registrar el negocio.");
            return;
        }

        showAlert("Registrado", "Negocio creado correctamente.");

        Stage stage = (Stage) fieldRepetirEmail.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/menuAdmin.fxml");

    }

    private void volver(MouseEvent e) {
        Stage stage = (Stage) txtEmpresaExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/crearUnirseNegocio.fxml");
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(c);
        a.showAndWait();
    }
}
