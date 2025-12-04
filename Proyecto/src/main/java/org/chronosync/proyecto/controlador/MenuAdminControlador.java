package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.chronosync.proyecto.util.CargadorUtil;

import javax.swing.text.html.ImageView;

public class MenuAdminControlador {
    @FXML private Button btnPanelPrincipal;
    @FXML private Button btnTurnos;
    @FXML private Button btnIncidencias;
    @FXML private Button btnExportaciones;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnConfiguracion1;
    @FXML private Button btnCerrarSesion;

    @FXML private Label txtNombre;
    @FXML private Label txtRol;
    @FXML private ImageView imgIcono;

    @FXML private AnchorPane anchorContenidoPrincipal;

    @FXML
    public void initialize() {
        btnConfiguracion.setOnMouseClicked(this::irConfiguracion);
        btnCerrarSesion.setOnMouseClicked(this::irCerrarSesion);

    }

//    @FXML
//    private void onTurnosClick() {
//
//    }
//
//    @FXML
//    private void onIncidenciasClick() {
//
//    }
//
//    @FXML
//    private void onExportacionesClick() {
//
//    }

    @FXML
    private void irConfiguracion(MouseEvent e) {
        Stage stage = (Stage) btnConfiguracion.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "fxml/menuAdminConfiguracion.fxml");
    }


    @FXML
    private void irCerrarSesion(MouseEvent e) {
        Stage stage = (Stage) btnConfiguracion.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/login.fxml");

    }


}
