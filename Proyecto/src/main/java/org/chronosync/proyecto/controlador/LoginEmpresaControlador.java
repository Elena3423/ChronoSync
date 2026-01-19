package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class LoginEmpresaControlador {

    // Conectamos los elementos del FXML
    @FXML private TextField fieldCodigo;
    @FXML private Button btnUnirse;
    @FXML private Label txtEmpresaNoExiste;

    private final NegocioDAO negocioDAO = new NegocioDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        // Permitimos que el usuario envíe el código al pulsar enter
        fieldCodigo.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                unirse();
            }
        });

        // Configuramos el clic para volver atrás o intentar la unión
        txtEmpresaNoExiste.setOnMouseClicked(this::volver);
        btnUnirse.setOnMouseClicked(e -> unirse());
    }

    /**
     * Método que intenta vincular al usuario con un negocio
     */
    @FXML
    public void unirse() {
        String codigo = fieldCodigo.getText().trim();

        // Comprobación que no esté vacio
        if (codigo.isEmpty()) {
            AlertaUtil.mostrarError("Error", "Introduce un código.");
            return;
        }

        // Buscamos que el código exista en la BD
        Negocio negocio = negocioDAO.obtenerPorCodigo(codigo);

        if (negocio == null) {
            AlertaUtil.mostrarError("Error", "No existe ningún negocio con ese código.");
            return;
        }

        // Recuperamos el usuario que está ejecutando la app
        Usuario usuario = SesionUtil.getUsuario();

        if (usuario == null) {
            AlertaUtil.mostrarError("Error", "No hay usuario en sesión.");
            return;
        }

        // Intentamos hacer el cambio en la BD
        boolean ok = usuarioDAO.asignarEmpleadoNegocio(usuario.getId(), negocio.getId());

        if (!ok) {
            AlertaUtil.mostrarError("Error", "No se pudo unir al negocio.");
            return;
        }

        // Actualizamos la información del usuario
        usuario.setNegocioId(negocio.getId());
        usuario.setRolId(2);
        usuario.setActivo(true);
        SesionUtil.setUsuario(usuario);
        AlertaUtil.mostrarInfo("Perfecto", "Te has unido al negocio correctamente.");

        // Enviamos al usuario al panel principal
        Stage stage = (Stage) fieldCodigo.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/menuPrincipal.fxml");
    }

    /**
     * Método que envia al usuario a la ventana de crear/unirse
     * @param e evento del ratón
     */
    private void volver(MouseEvent e) {
        Stage stage = (Stage) txtEmpresaNoExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/crearUnirseNegocio.fxml");
    }

}
