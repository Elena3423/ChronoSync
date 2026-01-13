package org.chronosync.proyecto.controlador.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Negocio;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

import java.io.File;

public class MenuConfiguracionControlador {
    @FXML private Button btnPanelPrincipal;
    @FXML private Button btnEmpleados;
    @FXML private Button btnTurnos;
    @FXML private Button btnIncidencias;
    @FXML private Button btnExportaciones;
    @FXML private Button btnCerrarSesion;

    @FXML private Label txtNombre, txtRol;

    @FXML private TextField txtFieldNombreEmpresa, txtFieldCodigoUnion, txtFieldDireccion, txtFieldTelefono, txtFieldEmail;
    @FXML private Button btnGuardar;

    @FXML private TextField txtFieldNombre, txtFieldApellidos;
    @FXML private Button btnGuardar2;

    private NegocioDAO negocioDAO = new NegocioDAO();
    private Negocio negocioActual;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);

        mostrarDatosUsuario();

        Usuario usuarioLogueado = SesionUtil.getUsuario();

        // 1. Datos de usuario en sidebar
        txtNombre.setText(usuarioLogueado.getNombre());
        txtRol.setText(usuarioLogueado.getRolId() == 1 ? "Administrador" : "Empleado");

        // 2. Cargar datos del negocio
        negocioActual = negocioDAO.obtenerPorId(usuarioLogueado.getNegocioId());
        if (negocioActual != null) {
            mapearDatosAFormulario();
        }

        // 3. SEGURIDAD: Solo Admin (Rol 1) puede editar
        if (usuarioLogueado.getRolId() != 1) {
            bloquearAccesoSoloAdmin();
        }

        // 4. Configurar eventos
        btnGuardar.setOnAction(e -> accionGuardar());

        cargarDatosPerfil();
        btnGuardar2.setOnAction(e -> accionGuardarPerfil());
    }

    private void navegar(MouseEvent e) {
        Button btn = (Button) e.getSource();

        String fxmlRuta;
        if (btn == btnPanelPrincipal) {
            fxmlRuta = "/fxml/menuPrincipal.fxml";
        } else if (btn == btnEmpleados) {
            fxmlRuta = "/fxml/menuEmpleados.fxml";
        } else if (btn == btnIncidencias) {
            fxmlRuta = "/fxml/menuIncidencias.fxml";
        } else if (btn == btnExportaciones) {
            fxmlRuta = "/fxml/menuExportaciones.fxml";
        } else if (btn == btnTurnos) {
            fxmlRuta = "/fxml/menuTurnos.fxml";
        } else {
            return;
        }

        Stage stage = (Stage) btn.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, fxmlRuta);
    }

    private void mostrarDatosUsuario() {
        txtNombre.setText(SesionUtil.getUsuario().getNombre());
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            txtRol.setText("Administrador");
        } else if (SesionUtil.getUsuario().getRolId().equals(2)) {
            txtRol.setText("Empleado");
        }
    }

    private void cerrarSesion(MouseEvent e) {
        Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
        SesionUtil.cerrarSesion(stage);
    }

    private void mapearDatosAFormulario() {
        txtFieldNombreEmpresa.setText(negocioActual.getNombre());
        txtFieldDireccion.setText(negocioActual.getDireccion());
        txtFieldTelefono.setText(negocioActual.getTelefono());
        txtFieldEmail.setText(negocioActual.getEmail());
        txtFieldCodigoUnion.setText(negocioActual.getCodigoUnion());
        txtFieldCodigoUnion.setEditable(false);
    }

    private void bloquearAccesoSoloAdmin() {
        txtFieldNombreEmpresa.setDisable(true);
        txtFieldDireccion.setDisable(true);
        txtFieldTelefono.setDisable(true);
        txtFieldEmail.setDisable(true);
        btnGuardar.setVisible(false);
    }

    private void accionGuardar() {
        negocioActual.setNombre(txtFieldNombreEmpresa.getText());
        negocioActual.setDireccion(txtFieldDireccion.getText());
        negocioActual.setTelefono(txtFieldTelefono.getText());
        negocioActual.setEmail(txtFieldEmail.getText());

        if (negocioDAO.actualizar(negocioActual)) {
            mostrarAlerta("Éxito", "Configuración actualizada correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudieron guardar los cambios.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void cargarDatosPerfil() {
        Usuario usuarioActual = SesionUtil.getUsuario();
        if (usuarioActual != null) {
            txtFieldNombre.setText(usuarioActual.getNombre());
            txtFieldApellidos.setText(usuarioActual.getApellidos());
        }
    }

    private void accionGuardarPerfil() {
        Usuario usuarioActual = SesionUtil.getUsuario();

        // Validar que no estén vacíos
        if (txtFieldNombre.getText().isEmpty() || txtFieldApellidos.getText().isEmpty()) {
            mostrarAlerta("Error", "El nombre y apellidos no pueden estar vacíos.", Alert.AlertType.WARNING);
            return;
        }

        usuarioActual.setNombre(txtFieldNombre.getText());
        usuarioActual.setApellidos(txtFieldApellidos.getText());

        // Llamamos al usuarioDAO que ya deberías tener para actualizar
        if (usuarioDAO.actualizar(usuarioActual)) {
            // Actualizamos también los labels de la barra lateral (opcional)
            txtNombre.setText(usuarioActual.getNombre());

            mostrarAlerta("Éxito", "Tus datos personales se han actualizado.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el perfil.", Alert.AlertType.ERROR);
        }
    }
}
