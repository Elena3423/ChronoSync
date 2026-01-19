package org.chronosync.proyecto.controlador.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Negocio;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.AlertaUtil;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

public class MenuConfiguracionControlador {
    // Botones de navegación lateral
    @FXML private Button btnPanelPrincipal, btnEmpleados, btnTurnos, btnIncidencias, btnExportaciones, btnCerrarSesion;
    @FXML private Label txtNombre, txtRol;

    // Campos de texto para la Configuración de Empresa
    @FXML private TextField txtFieldNombreEmpresa, txtFieldCodigoUnion, txtFieldDireccion, txtFieldTelefono, txtFieldEmail;
    @FXML private Button btnGuardar;

    // Campos de texto para el Perfil de Usuario
    @FXML private TextField txtFieldNombre, txtFieldApellidos;
    @FXML private Button btnGuardar2;

    private NegocioDAO negocioDAO = new NegocioDAO();
    private Negocio negocioActual;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        mostrarDatosUsuario();
        configurarNavegacion();

        Usuario usuarioLogueado = SesionUtil.getUsuario();

        // 1. Datos de usuario en sidebar
        txtNombre.setText(usuarioLogueado.getNombre());
        txtRol.setText(usuarioLogueado.getRolId() == 1 ? "Administrador" : "Empleado");

        // 2. Cargar datos del negocio
        negocioActual = negocioDAO.obtenerPorId(usuarioLogueado.getNegocioId());
        if (negocioActual != null) {
            mapearDatosAFormulario();
        }

        // 3. Solo Admin (Rol 1) puede editar
        if (usuarioLogueado.getRolId() != 1) {
            bloquearAccesoSoloAdmin();
        }

        // 4. Configurar eventos
        btnGuardar.setOnAction(e -> accionGuardar());

        cargarDatosPerfil();
        btnGuardar2.setOnAction(e -> accionGuardarPerfil());
    }

    /**
     * Método que muestra los datos del usuario por pantalla
     */
    private void mostrarDatosUsuario() {
        txtNombre.setText(SesionUtil.getUsuario().getNombre());
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            txtRol.setText("Administrador");
        } else if (SesionUtil.getUsuario().getRolId().equals(2)) {
            txtRol.setText("Empleado");
        }
    }

    /**
     * Método que cambia la escena en función del botón pulsado
     *
     * @param e evento del ratón
     */
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

    /**
     * Método que cierra las sesión actual del usuario
     *
     * @param e evento del ratón
     */
    private void cerrarSesion(MouseEvent e) {
        Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
        SesionUtil.cerrarSesion(stage);
    }

    /**
     * Método que configura los botones de navegación
     */
    private void configurarNavegacion() {
        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }

    /**
     * Método que configura los datos del formulario en función del usuario
     */
    private void mapearDatosAFormulario() {
        txtFieldNombreEmpresa.setText(negocioActual.getNombre());
        txtFieldDireccion.setText(negocioActual.getDireccion());
        txtFieldTelefono.setText(negocioActual.getTelefono());
        txtFieldEmail.setText(negocioActual.getEmail());
        txtFieldCodigoUnion.setText(negocioActual.getCodigoUnion());
        txtFieldCodigoUnion.setEditable(false);
    }

    /**
     * Método que bloquea la edición de algunos campos a los empleados
     */
    private void bloquearAccesoSoloAdmin() {
        txtFieldNombreEmpresa.setDisable(true);
        txtFieldDireccion.setDisable(true);
        txtFieldTelefono.setDisable(true);
        txtFieldEmail.setDisable(true);
        btnGuardar.setVisible(false);
    }

    /**
     * Método que configura la acción del botón guardar
     */
    private void accionGuardar() {
        negocioActual.setNombre(txtFieldNombreEmpresa.getText());
        negocioActual.setDireccion(txtFieldDireccion.getText());
        negocioActual.setTelefono(txtFieldTelefono.getText());
        negocioActual.setEmail(txtFieldEmail.getText());

        if (negocioDAO.actualizar(negocioActual)) {
            AlertaUtil.mostrarInfo("Éxito", "Configuración actualizada correctamente.");
        } else {
            AlertaUtil.mostrarError("Error", "No se pudieron guardar los cambios.");
        }
    }

    /**
     * Método que carga los datos del perfil del usuario
     */
    private void cargarDatosPerfil() {
        Usuario usuarioActual = SesionUtil.getUsuario();
        if (usuarioActual != null) {
            txtFieldNombre.setText(usuarioActual.getNombre());
            txtFieldApellidos.setText(usuarioActual.getApellidos());
        }
    }

    /**
     * Método que actualiza los datos de un usuario a los escritos en el formulario
     */
    private void accionGuardarPerfil() {
        Usuario usuarioActual = SesionUtil.getUsuario();

        // Validar que no estén vacíos
        if (txtFieldNombre.getText().isEmpty() || txtFieldApellidos.getText().isEmpty()) {
            AlertaUtil.mostrarError("Error", "El nombre y apellidos no pueden estar vacíos.");
            return;
        }

        usuarioActual.setNombre(txtFieldNombre.getText());
        usuarioActual.setApellidos(txtFieldApellidos.getText());

        // Llamamos al usuarioDAO que ya deberías tener para actualizar
        if (usuarioDAO.actualizar(usuarioActual)) {
            txtNombre.setText(usuarioActual.getNombre());

            AlertaUtil.mostrarError("Éxito", "Tus datos personales se han actualizado.");
        } else {
            AlertaUtil.mostrarError("Error", "No se pudo actualizar el perfil.");
        }
    }
}
