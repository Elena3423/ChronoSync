package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Negocio;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

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
            showAlert("Error", "Ya existe un negocio con ese correo.");
            return;
        }

        // Crear negocio
        Negocio negocio = new Negocio(nombre, direccion, telefono, email);
        Integer idNegocio = negocioDAO.insertar(negocio);

        if (idNegocio == null) {
            showAlert("Error", "No se pudo registrar el negocio.");
            return;
        }

        // Obtener usuario actual
        Usuario usuario = SesionUtil.getUsuario();

        if (usuario == null) {
            showAlert("Error", "No hay usuario en sesión.");
            return;
        }

        // Asignar usuario como admin de este negocio
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean actualizado = usuarioDAO.asignarAdminNegocio(usuario.getId(), idNegocio);

        if (!actualizado) {
            showAlert("Error", "Negocio creado, pero no se pudo asignar al usuario.");
            return;
        }

        // Actualizar también el objeto de sesión
        usuario.setNegocioId(idNegocio);
        usuario.setRolId(1);
        usuario.setActivo(true);

        SesionUtil.setUsuario(usuario);

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
