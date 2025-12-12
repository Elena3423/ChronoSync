package org.chronosync.proyecto.controlador.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

public class MenuConfiguracionControlador {
    @FXML private Button btnPanelPrincipal;
    @FXML private Button btnEmpleados;
    @FXML private Button btnTurnos;
    @FXML private Button btnIncidencias;
    @FXML private Button btnExportaciones;
    @FXML private Button btnCerrarSesion;

    @FXML
    public void initialize() {
        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
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

    private void cerrarSesion(MouseEvent e) {
        Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
        SesionUtil.cerrarSesion(stage);
    }
}
