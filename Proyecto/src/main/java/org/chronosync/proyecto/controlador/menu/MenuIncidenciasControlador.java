package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.IncidenciaDAO;
import org.chronosync.proyecto.modelo.Incidencia;
import org.chronosync.proyecto.util.AlertaUtil;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

import java.util.List;
import java.util.Map;

public class MenuIncidenciasControlador {
    // Botones de navegación lateral
    @FXML private Button btnPanelPrincipal, btnEmpleados, btnTurnos, btnExportaciones, btnConfiguracion, btnCerrarSesion;
    @FXML private Label txtNombre, txtRol;

    // Botón para crear nuevas solicitudes
    @FXML private Button btnAñadirIncidencia;

    // Tabla principal de incidencias
    @FXML private TableView<IncidenciaView> tabla;
    @FXML private TableColumn<IncidenciaView, String> colEmpleado;
    @FXML private TableColumn<IncidenciaView, String> colTipo;
    @FXML private TableColumn<IncidenciaView, String> colDescripcion;
    @FXML private TableColumn<IncidenciaView, String> colEstado;
    @FXML private TableColumn<IncidenciaView, Void> colAcciones;

    private final IncidenciaDAO incidenciaDAO = new IncidenciaDAO();

    /**
     * Clase interna para mostrar datos en la tabla
     */
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
        public Incidencia getIncidencia() { return incidencia; }
    }

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mostrarDatosUsuario();
        configurarNavegacion();
        configurarTabla();
        cargarDatos();
        aplicarRestriccionesSeguridad();

        btnAñadirIncidencia.setOnAction(e -> manejarNuevaIncidencia());
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
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }

    /**
     * Método que configura como se extraen los datos por cada columna de la tabla
     */
    private void configurarTabla() {
        colEmpleado.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getNombreEmpleado()));
        colTipo.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getTipo()));
        colDescripcion.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getComentarios()));

        colDescripcion.setCellFactory(column -> {
            return new TableCell<IncidenciaView, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item);
                        setWrapText(true);
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 5;");
                    }
                }
            };
        });

        colEstado.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleStringProperty(fila.getValue().getEstado()));

        configurarBotonesAccion();
    }

    /**
     * Método que carga las incidencias de la BD aplicando filtros de privacidad
     */
    private void cargarDatos() {
        // Bloqueamos la tabla temporalmente para evitar clics mientras carga
        tabla.setPlaceholder(new ProgressIndicator());

        // Extraemos los datos de sesión para filtrar
        final int negocioId = SesionUtil.getUsuario().getNegocioId();
        Integer filtroUsuarioId = null;

        // Si es empleado, solo ve sus propias incidencias. Si es admin, ve todas las del negocio.
        if (SesionUtil.getUsuario().getRolId().equals(2)) {
            filtroUsuarioId = SesionUtil.getUsuario().getId();
        }

        final Integer finalFiltroId = filtroUsuarioId;

        Task<ObservableList<IncidenciaView>> task = new Task<>() {
            @Override
            protected ObservableList<IncidenciaView> call() throws Exception {
                ObservableList<IncidenciaView> datos = FXCollections.observableArrayList();

                List<Map<String, Object>> resultados = incidenciaDAO.obtenerIncidenciasConNombre(finalFiltroId, negocioId);

                for (var res : resultados) {
                    datos.add(new IncidenciaView(
                            (Incidencia) res.get("incidencia"),
                            (String) res.get("nombreEmpleado")
                    ));
                }
                return datos;
            }
        };

        task.setOnSucceeded(e -> {
            tabla.setItems(task.getValue());
            if (task.getValue().isEmpty()) {
                tabla.setPlaceholder(new Label("No hay incidencias registradas en su negocio."));
            }
        });

        task.setOnFailed(e -> {
            AlertaUtil.mostrarError("Error", "Error al cargar los datos de la base de datos.");
        });

        new Thread(task).start();
    }

    /**
     * Crea los botones de aprobar/rechazar dentro de las celdas
     */
    private void configurarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnAprobar = new Button("✔");
            private final Button btnRechazar = new Button("✘");
            private final HBox container = new HBox(5, btnAprobar, btnRechazar);

            {
                btnAprobar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
                btnRechazar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

                btnAprobar.setOnAction(e -> procesarEstado(getTableRow().getItem(), "Aprobada"));
                btnRechazar.setOnAction(e -> procesarEstado(getTableRow().getItem(), "Rechazada"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !SesionUtil.getUsuario().getRolId().equals(1)) {
                    setGraphic(null);
                } else {
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

    /**
     * Actualiza el estado de la incidencia en la BD y refresca la tabla
     *
     * @param view objeto de la vista de la incidencia
     * @param nuevoEstado nuevo estado de la incidencia
     */
    private void procesarEstado(IncidenciaView view, String nuevoEstado) {
        if (view == null) return;

        String estadoDB = nuevoEstado.equals("Aprobada") ? "Aceptada" : "Rechazada";

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return incidenciaDAO.actualizarEstado(view.getIncidencia().getId(), estadoDB);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                cargarDatos();
            }
        });

        new Thread(task).start();
    }

    /**
     * Método que aplica una serie de restricciones en función del rol que tenga un usuario en un negocio
     */
    private void aplicarRestriccionesSeguridad() {
        Integer rolId = SesionUtil.getUsuario().getRolId();

        if (rolId.equals(2)) { // Si es EMPLEADO
            colEmpleado.setVisible(false);
            colAcciones.setVisible(false);
            btnAñadirIncidencia.setText("Solicitar Ausencia");
        } else if (rolId.equals(1)) { // Si es ADMIN
            colAcciones.setVisible(true);
            btnAñadirIncidencia.setVisible(false);
        }
    }

    /**
     * Método que maneja una nueva incidencia creada
     */
    private void manejarNuevaIncidencia() {
        // 1. Crear el Diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Crear Nueva Incidencia");
        dialog.setHeaderText("Describe el motivo de la incidencia o solicitud");

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/iconoPNG.png")));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono del diálogo.");
        }

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/formularios.css").toExternalForm());

        // 2. Botones del Diálogo
        ButtonType btnGuardar = new ButtonType("Enviar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // 3. Componentes del Formulario
        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList(
                "Ausencia", "Cambio", "Retraso", "Otra"
        ));
        cbTipo.setPromptText("Selecciona el tipo");
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        // NUEVO: ComboBox para seleccionar el turno real del empleado
        org.chronosync.proyecto.dao.TurnoDAO turnoDAO = new org.chronosync.proyecto.dao.TurnoDAO();
        List<org.chronosync.proyecto.modelo.Turno> turnosUsuario = turnoDAO.obtenerTurnosPorFiltro(
                SesionUtil.getUsuario().getId(), "Mes actual"
        );

        ComboBox<org.chronosync.proyecto.modelo.Turno> cbTurnos = new ComboBox<>(FXCollections.observableArrayList(turnosUsuario));
        cbTurnos.setPromptText("Selecciona el turno afectado");
        cbTurnos.setMaxWidth(Double.MAX_VALUE);

        // Configurar cómo se ve el turno en el ComboBox (Fecha y Tipo)
        cbTurnos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(org.chronosync.proyecto.modelo.Turno t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) setText(null);
                else setText(t.getFechaInicio().toLocalDate() + " - " + t.getTipo());
            }
        });
        cbTurnos.setButtonCell(cbTurnos.getCellFactory().call(null));

        TextArea txtComentarios = new TextArea();
        txtComentarios.setPromptText("Escribe aquí los detalles...");
        txtComentarios.setPrefRowCount(4);

        // Añadimos el nuevo ComboBox al layout
        VBox layout = new VBox(10,
                new Label("Turno afectado:"), cbTurnos,
                new Label("Tipo de incidencia:"), cbTipo,
                new Label("Comentarios:"), txtComentarios);
        layout.setPadding(new javafx.geometry.Insets(20));

        layout.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-border-color: #AAAAAA; -fx-border-radius: 10;");

        dialog.getDialogPane().setContent(layout);

        // 4. Mostrar y Procesar
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnGuardar) {
                String tipo = cbTipo.getValue();
                String comentarios = txtComentarios.getText();
                org.chronosync.proyecto.modelo.Turno turnoSeleccionado = cbTurnos.getValue();

                // Validamos que el turno no sea nulo para evitar el error de Foreign Key
                if (tipo == null || comentarios.trim().isEmpty() || turnoSeleccionado == null) {
                    AlertaUtil.mostrarError("Error", "Debes rellenar todos los campos, incluido el turno.");
                    return;
                }

                Incidencia nueva = new Incidencia();
                nueva.setTipo(tipo);
                nueva.setEstado("Pendiente");
                nueva.setComentarios(comentarios);
                nueva.setUsuarioId(SesionUtil.getUsuario().getId());
                // Asignamos el ID real del turno seleccionado
                nueva.setTurnoId(turnoSeleccionado.getId());

                // Operación de inserción en hilo secundario
                Task<Boolean> insertTask = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return incidenciaDAO.insertar(nueva);
                    }
                };

                insertTask.setOnSucceeded(e -> {
                    if (insertTask.getValue()) {
                        cargarDatos();
                    } else {
                        AlertaUtil.mostrarError("Error", "No se pudo guardar la incidencia. Verifique la conexión.");
                    }
                });

                new Thread(insertTask).start();
            }
        });
    }
}