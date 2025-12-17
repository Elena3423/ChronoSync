package org.chronosync.proyecto.controlador.menu;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

import java.time.LocalDate;
import java.time.YearMonth;

import javafx.scene.control.Label;
import javafx.geometry.Pos;

public class MenuTurnosControlador {
    @FXML private Button btnPanelPrincipal;
    @FXML private Button btnEmpleados;
    @FXML private Button btnIncidencias;
    @FXML private Button btnExportaciones;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;

    @FXML private Label txtNombre;
    @FXML private Label txtRol;

    @FXML private Label titulo;
    @FXML private Label subtitulo;

    @FXML private GridPane gridCalendario;
    @FXML private Label lblMes;
    @FXML private Button btnMesAnterior;
    @FXML private Button btnMesSiguiente;

    private YearMonth yearMonth;

    @FXML
    public void initialize() {
        mostrarDatosUsuario();

        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);

        yearMonth = YearMonth.now();

        btnMesAnterior.setOnAction(e -> cambiarMes(-1));
        btnMesSiguiente.setOnAction(e -> cambiarMes(1));

        titulo.setText("Calendario de Turnos");
        subtitulo.setText("Visualiza y gestiona los turnos asignados");

        dibujarCalendario();

    }

    private void mostrarDatosUsuario() {
        txtNombre.setText(SesionUtil.getUsuario().getNombre());
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            txtRol.setText("Administrador");
        } else if (SesionUtil.getUsuario().getRolId().equals(2)) {
            txtRol.setText("Empleado");
        }
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
        } else if (btn == btnConfiguracion) {
            fxmlRuta = "/fxml/menuConfiguracion.fxml";
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

    private void cambiarMes(int meses) {
        yearMonth = yearMonth.plusMonths(meses);
        dibujarCalendario();
    }

    private void dibujarCalendario() {
        gridCalendario.getChildren().clear();

        // 1. Limpiamos restricciones para evitar que se acumulen al cambiar de mes
        gridCalendario.getColumnConstraints().clear();
        gridCalendario.getRowConstraints().clear();

        String css = getClass().getResource("/css/menu.css").toExternalForm();
        if (!gridCalendario.getStylesheets().contains(css)) {
            gridCalendario.getStylesheets().add(css);
        }

        // 2. Forzamos el centrado del GridPane en su contenedor padre
        gridCalendario.setAlignment(Pos.CENTER);

        // 3. Definimos 7 columnas que ocupen el 100% equitativamente
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            cc.setHalignment(HPos.CENTER); // Centra el contenido horizontalmente en la celda
            gridCalendario.getColumnConstraints().add(cc);
        }

        // 4. Definimos 6 filas que ocupen el 100% equitativamente
        for (int i = 0; i < 6; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / 6);
            rc.setValignment(VPos.CENTER); // Centra el contenido verticalmente en la celda
            gridCalendario.getRowConstraints().add(rc);
        }

        // Espaciado entre tarjetas
        gridCalendario.setHgap(10);
        gridCalendario.setVgap(10);
        gridCalendario.setPadding(new Insets(10));

        lblMes.setText(yearMonth.getMonth() + " " + yearMonth.getYear());
        LocalDate primerDia = yearMonth.atDay(1);
        int inicio = primerDia.getDayOfWeek().getValue() - 1;
        int diasMes = yearMonth.lengthOfMonth();

        int fila = 0;
        int columna = inicio;

        for (int dia = 1; dia <= diasMes; dia++) {
            VBox celda = new VBox();
            celda.getStyleClass().add("tarjeta");

            // Alineación interna: El número arriba a la izquierda
            celda.setAlignment(Pos.TOP_LEFT);

            // Importante: La tarjeta debe intentar ocupar todo el espacio de la celda
            celda.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            GridPane.setHgrow(celda, Priority.ALWAYS);
            GridPane.setVgrow(celda, Priority.ALWAYS);

            Label lbl = new Label(String.valueOf(dia));
            lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

            celda.getChildren().add(lbl);
            gridCalendario.add(celda, columna, fila);

            columna++;
            if (columna == 7) {
                columna = 0;
                fila++;
            }
        }
    }

}
