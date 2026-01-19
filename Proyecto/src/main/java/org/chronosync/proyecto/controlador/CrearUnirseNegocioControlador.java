package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.util.CargadorUtil;

public class CrearUnirseNegocioControlador {

    // Conectamos los elementos del FXML
    @FXML Button btnCrearNegocio;
    @FXML Button btnUnirmeNegocio;

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        btnCrearNegocio.setOnMouseClicked(this::irRegistroEmpresa);
        btnUnirmeNegocio.setOnMouseClicked(this::irLoginEmpresa);
    }

    /**
     * Método que envia al usuario al registro de empresa
     * @param e evento del ratón
     */
    private void irRegistroEmpresa(MouseEvent e) {
        Stage stage = (Stage) btnCrearNegocio.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/registerEmpresa.fxml");
    }

    /**
     * Método que envia al usuario al login de empresa
     * @param e evento del ratón
     */
    private void irLoginEmpresa(MouseEvent e) {
        Stage stage = (Stage) btnCrearNegocio.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/loginEmpresa.fxml");
    }

}
