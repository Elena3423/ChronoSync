package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.util.CargadorUtil;

public class CrearUnirseNegocioControlador {

    @FXML Button btnCrearNegocio;
    @FXML Button btnUnirmeNegocio;

    @FXML
    public void initialize() {
        btnCrearNegocio.setOnMouseClicked(this::irRegistroEmpresa);
        btnUnirmeNegocio.setOnMouseClicked(this::irLoginEmpresa);
    }

    private void irRegistroEmpresa(MouseEvent e) {
        Stage stage = (Stage) btnCrearNegocio.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/registerEmpresa.fxml");
    }

    private void irLoginEmpresa(MouseEvent e) {
        Stage stage = (Stage) btnCrearNegocio.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/loginEmpresa.fxml");
    }

}
