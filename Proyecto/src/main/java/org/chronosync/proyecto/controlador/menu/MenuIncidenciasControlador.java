package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.IncidenciaDAO;
import org.chronosync.proyecto.modelo.Incidencia;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

public class MenuIncidenciasControlador {
    @FXML private Button btnPanelPrincipal;
    @FXML private Button btnEmpleados;
    @FXML private Button btnTurnos;
    @FXML private Button btnExportaciones;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;

    @FXML private Label txtNombre;
    @FXML private Label txtRol;

    @FXML private Button btnAñadirIncidencia;

    @FXML private TableView<IncidenciaView> tabla;
    @FXML private TableColumn<IncidenciaView, String> colEmpleado;
    @FXML private TableColumn<IncidenciaView, String> colTipo;
    @FXML private TableColumn<IncidenciaView, String> colDescripcion;
    @FXML private TableColumn<IncidenciaView, String> colEstado;
    @FXML private TableColumn<IncidenciaView, Void> colAcciones;

    private final IncidenciaDAO incidenciaDAO = new IncidenciaDAO();

    public static class IncidenciaView {
        private final Incidencia incidencia;
        private final String nombreEmpleado;

        public IncidenciaView(Incidencia i, String nombre) {
            this.incidencia = i;
            this.nombreEmpleado = nombre;
        }

        public String getNombreEmpleado() { return nombreEmpleado; }
        public String getTipo() { return incidencia.getTipo(); }
        public String getEstado() { return incidencia.getEstado(); }
        public String getComentarios() { return incidencia.getComentarios(); }
        public Integer getTurnoId() { return incidencia.getTurnoId(); }
        public Incidencia getIncidencia() { return incidencia; }
    }

    @FXML
    public void initialize() {
        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);

        configurarTabla();
        cargarDatos();
        mostrarDatosUsuario();
        aplicarRestriccionesSeguridad();

        btnAñadirIncidencia.setOnAction(e -> manejarNuevaIncidencia());
    }

    private void navegar(MouseEvent e) {
        Button btn = (Button) e.getSource();

        String fxmlRuta;
        if (btn == btnPanelPrincipal) {
            fxmlRuta = "/fxml/menuPrincipal.fxml";
        } else if (btn == btnEmpleados) {
            fxmlRuta = "/fxml/menuEmpleados.fxml";
        } else if (btn == btnTurnos) {
            fxmlRuta = "/fxml/menuTurnos.fxml";
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

    private void configurarTabla() {
        // Usamos Lambdas para extraer los datos manualmente.

        colEmpleado.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getNombreEmpleado()));

        colTipo.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getTipo()));

        colDescripcion.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getComentarios()));

        colEstado.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getEstado()));

        configurarBotonesAccion();
    }

    private void cargarDatos() {
        ObservableList<IncidenciaView> datos = FXCollections.observableArrayList();
        Integer filtroId = null;

        // REGLA DE NEGOCIO: Si es empleado, solo ve las suyas
        if (SesionUtil.getUsuario().getRolId().equals(2)) {
            filtroId = SesionUtil.getUsuario().getId();
        }

        var resultados = incidenciaDAO.obtenerIncidenciasConNombre(filtroId);
        for (var res : resultados) {
            datos.add(new IncidenciaView(
                    (Incidencia) res.get("incidencia"),
                    (String) res.get("nombreEmpleado")
            ));
        }
        tabla.setItems(datos);
    }

    private void configurarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnAprobar = new Button("✔");
            private final Button btnRechazar = new Button("✘");
            private final HBox container = new HBox(5, btnAprobar, btnRechazar);

            {
                btnAprobar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                btnRechazar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnAprobar.setOnAction(e -> procesarEstado(getTableRow().getItem(), "Aprobada"));
                btnRechazar.setOnAction(e -> procesarEstado(getTableRow().getItem(), "Rechazada"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !SesionUtil.getUsuario().getRolId().equals(1)) {
                    setGraphic(null);
                } else {
                    // Solo mostrar acciones si está Pendiente
                    IncidenciaView row = getTableRow().getItem();
                    if (row != null && "Pendiente".equalsIgnoreCase(row.getEstado())) {
                        setGraphic(container);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void procesarEstado(IncidenciaView view, String nuevoEstado) {
        if (view != null) {
            String estadoDB = nuevoEstado.equals("Aprobada") ? "Aceptada" : "Rechazada";
            incidenciaDAO.actualizarEstado(view.getIncidencia().getId(), estadoDB);
            cargarDatos();
        }
    }

    private void aplicarRestriccionesSeguridad() {
        // Obtenemos el rol del usuario actual
        Integer rolId = SesionUtil.getUsuario().getRolId();

        if (rolId.equals(2)) { // Si es EMPLEADO
            colEmpleado.setVisible(false); // No ve su propio nombre repetido
            colAcciones.setVisible(false);
            btnAñadirIncidencia.setText("Solicitar Ausencia");
        } else if (rolId.equals(1)) { // Si es ADMIN
            colAcciones.setVisible(true);
            btnAñadirIncidencia.setVisible(false);
        }
    }

    private void manejarNuevaIncidencia() {
        // 1. Crear el Diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Crear Nueva Incidencia");
        dialog.setHeaderText("Describe el motivo de la incidencia o solicitud");

        // 2. Botones del Diálogo
        ButtonType btnGuardar = new ButtonType("Enviar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // 3. Componentes del Formulario
        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList(
                "Ausencia", "Cambio", "Retraso", "Otra"
        ));
        cbTipo.setPromptText("Selecciona el tipo");
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        TextArea txtComentarios = new TextArea();
        txtComentarios.setPromptText("Escribe aquí los detalles...");
        txtComentarios.setPrefRowCount(4);

        VBox layout = new VBox(10, new Label("Tipo:"), cbTipo, new Label("Comentarios:"), txtComentarios);
        layout.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(layout);

        // 4. Mostrar y Procesar
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnGuardar) {
                if (cbTipo.getValue() == null || txtComentarios.getText().trim().isEmpty()) {
                    mostrarAlerta("Error", "Debes rellenar todos los campos.");
                    return;
                }

                // Crear objeto Incidencia
                Incidencia nueva = new Incidencia();
                nueva.setTipo(cbTipo.getValue());
                nueva.setEstado("Pendiente");
                nueva.setComentarios(txtComentarios.getText());
                nueva.setUsuarioId(SesionUtil.getUsuario().getId());

                nueva.setTurnoId(1);

                if (incidenciaDAO.insertar(nueva)) {
                    cargarDatos();
                } else {
                    mostrarAlerta("Error", "No se pudo guardar. Verifica que el turno_id existe en la base de datos.");
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
