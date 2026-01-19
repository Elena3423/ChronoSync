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
import org.chronosync.proyecto.util.AlertaUtil;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

public class RegistroEmpresaControlador {

    // Conectamos los elementos del FXML
    @FXML TextField fieldNombre;
    @FXML TextField fieldDireccion;
    @FXML TextField fieldTelefono;
    @FXML TextField fieldEmail;
    @FXML TextField fieldRepetirEmail;
    @FXML Button btnRegistrar;
    @FXML Label txtEmpresaExiste;

    private final NegocioDAO negocioDAO = new NegocioDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
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

        // Acción del botón principal
        btnRegistrar.setOnMouseClicked(e -> registrar());
    }

    /**
     * Método que contiene la lógica para registrar un negocio
     */
    private void registrar() {

        // Recogemos los datos del formulario
        String nombre = fieldNombre.getText();
        String direccion = fieldDireccion.getText();
        String telefono = fieldTelefono.getText();
        String email = fieldEmail.getText();
        String repetirEmail = fieldRepetirEmail.getText();

        // Comprobamos que no hayan campos vacíos
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || repetirEmail.isEmpty()) {
            AlertaUtil.mostrarAdvertencia("Campos vacíos", "Rellena todos los campos.");
            return;
        }

        // Comprobamos que el email se haya escrito bien las dos veces
        if (!email.equals(repetirEmail)) {
            AlertaUtil.mostrarError("Error", "Los correos no coinciden.");
            return;
        }

        // Comprobamos que una empresa no esté logueada con ese email
        if (negocioDAO.obtenerPorEmail(email) != null) {
            AlertaUtil.mostrarError("Error", "Ya existe un negocio con ese correo.");
            return;
        }

        // Creamos el negocio y lo insertamos en la BD
        Negocio negocio = new Negocio(nombre, direccion, telefono, email);
        Integer idNegocio = negocioDAO.insertar(negocio);

        if (idNegocio == null) {
            AlertaUtil.mostrarError("Error", "No se pudo registrar el negocio.");
            return;
        }

        // Obtenemos el usuario actual
        Usuario usuario = SesionUtil.getUsuario();

        if (usuario == null) {
            AlertaUtil.mostrarError("Error", "No hay usuario en sesión.");
            return;
        }

        // Asignar usuario como admin de este negocio
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean actualizado = usuarioDAO.asignarAdminNegocio(usuario.getId(), idNegocio);

        if (!actualizado) {
            AlertaUtil.mostrarError("Error", "Negocio creado, pero no se pudo asignar al usuario.");
            return;
        }

        // Actualizamos la sesión para que sepa que tiene todos los privilegios
        usuario.setNegocioId(idNegocio);
        usuario.setRolId(1);
        usuario.setActivo(true);

        SesionUtil.setUsuario(usuario);

        AlertaUtil.mostrarInfo("Registrado", "Negocio creado correctamente.");

        Stage stage = (Stage) fieldRepetirEmail.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/menuPrincipal.fxml");
    }

    /**
     *Método que envia al usuario al crear/unirse
     *
     * @param e evento del ratón
     */
    private void volver(MouseEvent e) {
        Stage stage = (Stage) txtEmpresaExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/crearUnirseNegocio.fxml");
    }

}
