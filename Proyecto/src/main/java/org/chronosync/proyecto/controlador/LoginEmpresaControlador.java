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
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

public class LoginEmpresaControlador {
    @FXML private TextField fieldCodigo;
    @FXML private Button btnUnirse;
    @FXML private Label txtEmpresaNoExiste;

    private final NegocioDAO negocioDAO = new NegocioDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        fieldCodigo.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                unirse();
            }
        });

        txtEmpresaNoExiste.setOnMouseClicked(this::volver);
        btnUnirse.setOnMouseClicked(e -> unirse());
    }

    @FXML
    public void unirse() {
        String codigo = fieldCodigo.getText().trim();

        if (codigo.isEmpty()) {
            mostrar("Error", "Introduce un código.");
            return;
        }

        Negocio negocio = negocioDAO.obtenerPorCodigo(codigo);

        if (negocio == null) {
            mostrar("Error", "No existe ningún negocio con ese código.");
            return;
        }

        Usuario usuario = SesionUtil.getUsuario();

        if (usuario == null) {
            mostrar("Error", "No hay usuario en sesión.");
            return;
        }

        boolean ok = usuarioDAO.asignarEmpleadoNegocio(usuario.getId(), negocio.getId());

        if (!ok) {
            mostrar("Error", "No se pudo unir al negocio.");
            return;
        }

        usuario.setNegocioId(negocio.getId());
        usuario.setRolId(2);
        usuario.setActivo(true);
        SesionUtil.setUsuario(usuario);

        mostrar("Perfecto", "Te has unido al negocio correctamente.");

        Stage stage = (Stage) fieldCodigo.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/menuPrincipal.fxml");
    }

    private void volver(MouseEvent e) {
        Stage stage = (Stage) txtEmpresaNoExiste.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/crearUnirseNegocio.fxml");
    }

    private void mostrar(String t, String c) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(t);
        alert.setHeaderText(null);
        alert.setContentText(c);
        alert.showAndWait();
    }
}
