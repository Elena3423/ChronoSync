package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.TurnoDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

import java.util.List;

public class MenuEmpleadosControlador {
    @FXML private Button btnPanelPrincipal;
    @FXML private Button btnTurnos;
    @FXML private Button btnIncidencias;
    @FXML private Button btnExportaciones;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;

    @FXML private Label txtNombre;
    @FXML private Label txtRol;

    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, Integer> colPuesto;
    @FXML private TableColumn<Usuario, Boolean> colEstado;
    @FXML private TableColumn<Usuario, Integer> colTurnos;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private TurnoDAO turnoDAO = new TurnoDAO();

    @FXML
    public void initialize() {
        configurarNavegacion();
        mostrarDatosUsuario();

        tabla.getStyleClass().add("");

        // Configuración de las columnas básicas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("rolId"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colTurnos.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Formatear columna puesto
        colPuesto.setCellFactory(column -> new TableCell<Usuario, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("table-cell"); // Limpiar para evitar duplicados
                getStyleClass().add("table-cell");

                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item == 1) {
                        setText("Administrador");
                        setStyle("-fx-text-fill: #3A7BD5; -fx-font-weight: bold;");
                    } else {
                        setText("Empleado");
                        setStyle("-fx-text-fill: #7f8c8d;");
                    }
                }
            }
        });

        // Formatear columna estado
        colEstado.setCellFactory(column -> new TableCell<Usuario, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().add("table-cell");

                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item) {
                        setText("Activo");
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else {
                        setText("Inactivo");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Formatear columna turnos
        colTurnos.setCellFactory(column -> new TableCell<Usuario, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().add("table-cell");

                if (empty || item == null) {
                    setText(null);
                } else {
                    int totalTurnos = turnoDAO.contarTurnosMesUsuario(item);
                    setText(totalTurnos + " turnos");
                    setStyle("-fx-text-fill: #333333;");
                }
            }
        });

        actualizarTabla();
    }

    private void actualizarTabla() {
        List<Usuario> lista = usuarioDAO.obtenerTodos();
        tabla.setItems(FXCollections.observableArrayList(lista));
    }

    private void mostrarDatosUsuario() {
        txtNombre.setText(SesionUtil.getUsuario().getNombre());
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            txtRol.setText("Administrador");
        } else if (SesionUtil.getUsuario().getRolId().equals(2)) {
            txtRol.setText("Empleado");
        }
    }

    private void configurarNavegacion() {
        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }

    private void navegar(MouseEvent e) {
        Button btn = (Button) e.getSource();

        String fxmlRuta;
        if (btn == btnPanelPrincipal) {
            fxmlRuta = "/fxml/menuPrincipal.fxml";
        } else if (btn == btnTurnos) {
            fxmlRuta = "/fxml/menuTurnos.fxml";
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
}
