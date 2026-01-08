package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.TurnoDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

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
    @FXML private Button btnAñadirTurno;

    private YearMonth yearMonth;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final TurnoDAO turnoDAO = new TurnoDAO();

    @FXML
    public void initialize() {
        yearMonth = YearMonth.now();
        mostrarDatosUsuario();
        aplicarRestriccionesSeguridad();
        configurarNavegacion();

        titulo.setText("Calendario de Turnos");
        subtitulo.setText("Visualiza y gestiona los turnos asignados");

        btnMesAnterior.setOnAction(e -> cambiarMes(-1));
        btnMesSiguiente.setOnAction(e -> cambiarMes(1));
        if (btnAñadirTurno != null) btnAñadirTurno.setOnAction(e -> manejarAnadirTurno());

        dibujarCalendario();

    }

    private void aplicarRestriccionesSeguridad() {
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        // Si el rol no es Administrador (ID 1)
        if (!usuarioLogueado.getRolId().equals(1)) {

            // Ocultar y quitar espacio del botón Añadir Turno
            btnAñadirTurno.setVisible(false);
            btnAñadirTurno.setManaged(false);

            // Opcional: Ocultar otros botones de administración si existen
            if (btnEmpleados != null) {
                // Un empleado quizás no debería gestionar otros empleados
                // btnEmpleados.setDisable(true);
            }

            subtitulo.setText("Visualización de turnos asignados");
        }
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
        btnEmpleados.setOnMouseClicked(this::navegar);
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
        gridCalendario.getColumnConstraints().clear();
        gridCalendario.getRowConstraints().clear();

        // 1. Cabecera del mes
        String mesTexto = yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        lblMes.setText(mesTexto.substring(0, 1).toUpperCase() + mesTexto.substring(1) + " " + yearMonth.getYear());

        configurarDimensionesGrid();

        // --- LÓGICA DE FILTRADO POR ROL ---
        Usuario user = SesionUtil.getUsuario();
        Integer filtroId = null;

        // Si es Empleado (Rol 2), solo pasamos su propio ID para filtrar
        if (user.getRolId().equals(2)) {
            filtroId = user.getId();
        }

        // Pedimos los turnos al DAO (filtrados o no)
        Map<LocalDate, List<String>> turnosDelMes = turnoDAO.obtenerTurnosDelMes(yearMonth, filtroId);

        LocalDate primerDia = yearMonth.atDay(1);
        int desplazamiento = primerDia.getDayOfWeek().getValue() - 1;
        int diasMes = yearMonth.lengthOfMonth();

        int fila = 0;
        int columna = desplazamiento;

        for (int dia = 1; dia <= diasMes; dia++) {
            LocalDate fechaActual = yearMonth.atDay(dia);
            VBox celda = crearCeldaDia(dia, turnosDelMes.getOrDefault(fechaActual, Collections.emptyList()));
            gridCalendario.add(celda, columna, fila);

            columna++;
            if (columna == 7) {
                columna = 0;
                fila++;
            }
        }
    }

    private VBox crearCeldaDia(int dia, List<String> turnos) {
        VBox celda = new VBox(5);
        celda.getStyleClass().add("tarjeta");
        celda.setPadding(new Insets(5));
        celda.setAlignment(Pos.TOP_LEFT);
        celda.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(celda, Priority.ALWAYS);
        GridPane.setVgrow(celda, Priority.ALWAYS);

        Label lblDia = new Label(String.valueOf(dia));
        lblDia.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        celda.getChildren().add(lblDia);

        for (String tipo : turnos) {
            celda.getChildren().add(crearEtiquetaTurno(tipo));
        }
        return celda;
    }

    private Label crearEtiquetaTurno(String info) {
        // info viene como "Juan - Mañana"
        String[] partes = info.split(" - ");
        String nombre = partes[0];
        String tipo = partes[1];

        Label etiqueta = new Label(nombre); // O puedes poner tipo si prefieres que el empleado vea "Mañana"
        etiqueta.setMaxWidth(Double.MAX_VALUE);
        etiqueta.setPadding(new Insets(2, 5, 2, 5));

        String color = switch (tipo.toLowerCase()) {
            case "mañana" -> "#3A7BD5";
            case "tarde" -> "#58B368";
            case "noche" -> "#9B59B6";
            default -> "#AAAAAA";
        };

        etiqueta.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 10;", color));
        return etiqueta;
    }

    private void configurarDimensionesGrid() {
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            cc.setHalignment(HPos.CENTER);
            gridCalendario.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < 6; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / 6);
            rc.setValignment(VPos.CENTER);
            gridCalendario.getRowConstraints().add(rc);
        }
    }

    @FXML
    private void manejarAnadirTurno() {
        if (!SesionUtil.getUsuario().getRolId().equals(1)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Acceso Denegado");
            alert.setHeaderText(null);
            alert.setContentText("No tienes permisos para realizar esta acción.");
            alert.showAndWait();
            return;
        }

        // 1. Obtener lista de empleados
        List<Usuario> empleados = usuarioDAO.obtenerTodos();

        // 2. Crear el Diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Asignar Nuevo Turno");
        dialog.setHeaderText("Completa los datos para registrar el turno.");

        // Aplicar el estilo del diálogo (opcional, para que herede tu CSS)
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/menu.css").toExternalForm());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // 3. Crear componentes del formulario
        ComboBox<Usuario> cbEmpleados = new ComboBox<>(FXCollections.observableArrayList(empleados));
        cbEmpleados.setPromptText("Seleccionar empleado");
        cbEmpleados.setMaxWidth(Double.MAX_VALUE);

        // Formatear visualización del ComboBox de empleados
        cbEmpleados.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty ? null : u.getNombre() + " " + u.getApellidos());
            }
        });
        cbEmpleados.setButtonCell(cbEmpleados.getCellFactory().call(null));

        // Selector de Fecha (Dato clave para que aparezca en el calendario)
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        dpFecha.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cbTurnos = new ComboBox<>(FXCollections.observableArrayList("Mañana", "Tarde", "Noche"));
        cbTurnos.setPromptText("Seleccionar tipo de turno");
        cbTurnos.setMaxWidth(Double.MAX_VALUE);

        // 4. Layout del contenido organizado con Labels
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("Trabajador:"), cbEmpleados,
                new Label("Fecha del turno:"), dpFecha,
                new Label("Franja horaria:"), cbTurnos
        );
        dialog.getDialogPane().setContent(content);

        // 5. Procesar resultado e insertar en Base de Datos
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {
            Usuario sel = cbEmpleados.getValue();
            String tipo = cbTurnos.getValue();
            LocalDate fecha = dpFecha.getValue();

            if (sel != null && tipo != null && fecha != null) {
                // LLAMADA REAL AL DAO
                boolean insertado = turnoDAO.insertarTurno(sel.getId(), tipo, fecha);

                if (insertado) {
                    // Si se guardó, refrescamos el dibujo del calendario
                    dibujarCalendario();
                } else {
                    // Alerta en caso de fallo en la BD
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("No se pudo guardar el turno en la base de datos.");
                    alert.showAndWait();
                }
            } else {
                // Alerta si faltan campos
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Campos incompletos");
                alert.setContentText("Por favor, rellene todos los campos del formulario.");
                alert.showAndWait();
            }
        }
    }
}
