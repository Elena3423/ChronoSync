package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
import org.chronosync.proyecto.util.AlertaUtil;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.SesionUtil;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Stream;

import javafx.geometry.Pos;

public class MenuTurnosControlador {
    // Elementos de navegación
    @FXML private Button btnPanelPrincipal, btnEmpleados, btnIncidencias, btnExportaciones, btnConfiguracion, btnCerrarSesion;
    @FXML private Label txtNombre, txtRol;

    // Elementos del calendario
    @FXML private Label titulo, subtitulo, lblMes;
    @FXML private GridPane gridCalendario;
    @FXML private Button btnMesAnterior, btnMesSiguiente, btnAñadirTurno;

    private YearMonth yearMonth;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final TurnoDAO turnoDAO = new TurnoDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        yearMonth = YearMonth.now(); // Empezamos en el mes actual
        mostrarDatosUsuario();
        aplicarRestriccionesSeguridad();
        configurarNavegacion();

        titulo.setText("Calendario de Turnos");
        subtitulo.setText("Visualiza y gestiona los turnos asignados");

        // Configuramos los botones de control del calendario
        btnMesAnterior.setOnAction(e -> cambiarMes(-1));
        btnMesSiguiente.setOnAction(e -> cambiarMes(1));
        if (btnAñadirTurno != null) {
            btnAñadirTurno.setOnAction(e -> manejarAnadirTurno());
        }

        dibujarCalendario();
    }

    /**
     * Método que oculta las funciones de creación a los empleados
     */
    private void aplicarRestriccionesSeguridad() {
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        if (!usuarioLogueado.getRolId().equals(1)) {
            btnAñadirTurno.setVisible(false);
            btnAñadirTurno.setManaged(false);
            subtitulo.setText("Visualización de turnos asignados");
        }
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
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }

    /**
     * Método que cambia el mes del calendario
     *
     * @param meses
     */
    private void cambiarMes(int meses) {
        yearMonth = yearMonth.plusMonths(meses);
        dibujarCalendario();
    }

    /**
     * Método que crea el calendario consultando los datos del mes
     */
    private void dibujarCalendario() {
        gridCalendario.getChildren().clear();
        configurarDimensionesGrid();

        String mesTexto = yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        lblMes.setText(mesTexto.substring(0, 1).toUpperCase() + mesTexto.substring(1) + " " + yearMonth.getYear());

        // Añadimos un icono de carga visual mientras consultamos la BD
        ProgressIndicator loading = new ProgressIndicator();
        gridCalendario.add(loading, 3, 2); // Lo colocamos en el centro aproximado del grid

        Usuario user = SesionUtil.getUsuario();
        final Integer filtroId = user.getRolId().equals(2) ? user.getId() : null;
        final YearMonth mesActual = yearMonth;

        // Creamos una tarea para la consulta pesada a la BD
        Task<Map<LocalDate, List<String>>> tareaCarga = new Task<>() {
            @Override
            protected Map<LocalDate, List<String>> call() throws Exception {
                // Esto ocurre en un HILO SECUNDARIO (No bloquea la ventana)
                return turnoDAO.obtenerTurnosDelMes(mesActual, filtroId);
            }
        };

        // Cuando la tarea se ejecute correctamente, entonces:
        tareaCarga.setOnSucceeded(e -> {
            gridCalendario.getChildren().remove(loading); // Quitamos el icono de carga
            Map<LocalDate, List<String>> turnosDelMes = tareaCarga.getValue();
            rellenarCeldasCalendario(turnosDelMes);
        });

        // Si hay un error, entonces:
        tareaCarga.setOnFailed(e -> {
            gridCalendario.getChildren().remove(loading);
            AlertaUtil.mostrarError("Error de Carga", "No se pudieron recuperar los turnos.");
            tareaCarga.getException().printStackTrace();
        });

        // Lanzamos el hilo
        new Thread(tareaCarga).start();
    }

    /**
     * Método que rellena cada una de las celdas del calendario
     *
     * @param turnosDelMes mapa con la lista de turnos que hay en un mes
     */
    private void rellenarCeldasCalendario(Map<LocalDate, List<String>> turnosDelMes) {
        LocalDate primerDia = yearMonth.atDay(1);
        int desplazamiento = primerDia.getDayOfWeek().getValue() - 1;
        int diasMes = yearMonth.lengthOfMonth();

        for (int dia = 1; dia <= diasMes; dia++) {
            int columna = (dia + desplazamiento - 1) % 7;
            int fila = (dia + desplazamiento - 1) / 7;

            LocalDate fechaActual = yearMonth.atDay(dia);
            VBox celda = crearCeldaDia(dia, turnosDelMes.getOrDefault(fechaActual, Collections.emptyList()));
            gridCalendario.add(celda, columna, fila);
        }
    }

    /**
     * Método que crea un contenedor para un día con sus etiquetas de turnos
     *
     * @param dia
     * @param turnos
     * @return
     */
    private VBox crearCeldaDia(int dia, List<String> turnos) {
        VBox celda = new VBox(5);
        celda.getStyleClass().add("tarjeta");
        celda.setPadding(new Insets(5));
        celda.setAlignment(Pos.TOP_LEFT);

        // Hacemos que la celda crezca para llenar el espacio del Grid
        celda.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(celda, Priority.ALWAYS);
        GridPane.setVgrow(celda, Priority.ALWAYS);

        Label lblDia = new Label(String.valueOf(dia));
        lblDia.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        celda.getChildren().add(lblDia);

        // Añadimos una etiqueta de color por cada turno que haya ese día
        for (String tipo : turnos) {
            celda.getChildren().add(crearEtiquetaTurno(tipo));
        }

        return celda;
    }

    /**
     * Método que crea una etiqueta de distinto color para cada tipo de turno
     *
     * @param info información del turno
     * @return
     */
    private Label crearEtiquetaTurno(String info) {
        String[] partes = info.split(" - ");
        String nombre = partes[0];
        String tipo = partes[1];

        Label etiqueta = new Label(nombre);
        etiqueta.setMaxWidth(Double.MAX_VALUE);
        etiqueta.setPadding(new Insets(2, 5, 2, 5));

        // Código de colores en función de la hora
        String color = switch (tipo.toLowerCase()) {
            case "mañana" -> "#3A7BD5";
            case "tarde" -> "#58B368";
            case "noche" -> "#9B59B6";
            default -> "#AAAAAA";
        };

        etiqueta.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 10;", color));
        return etiqueta;
    }

    /**
     * Método que configura las dimensiones del grid
     */
    private void configurarDimensionesGrid() {
        gridCalendario.getColumnConstraints().clear();
        gridCalendario.getRowConstraints().clear();

        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            cc.setHalignment(HPos.CENTER);
            cc.setFillWidth(true);
            gridCalendario.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < 6; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / 6);
            rc.setValignment(VPos.TOP);
            rc.setFillHeight(true);
            gridCalendario.getRowConstraints().add(rc);
        }
    }

    /**
     * Método que abre un cuadro de diálogo para asignar turnos
     */
    @FXML
    private void manejarAnadirTurno() {
        // Verificamos el rol del usuario
        if (!SesionUtil.getUsuario().getRolId().equals(1)) {
            AlertaUtil.mostrarAdvertencia("Acceso Denegado", "No tienes permisos.");
            return;
        }

        // Configuramos el diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Asignar Nuevo Turno");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/menu.css").toExternalForm());

        ButtonType btnGuardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardarType, ButtonType.CANCEL);

        // Cargamos los usuarios del negocio (Ahora con placeholder o gestión de carga)
        ComboBox<Usuario> cbEmpleados = crearComboBoxEmpleados();
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        ComboBox<String> cbTurnos = new ComboBox<>(FXCollections.observableArrayList("Mañana", "Tarde", "Noche"));

        // Damos un estilo
        Stream.of(cbEmpleados, dpFecha, cbTurnos).forEach(c -> c.setMaxWidth(Double.MAX_VALUE));

        VBox content = new VBox(10, new Label("Trabajador:"), cbEmpleados,
                new Label("Fecha:"), dpFecha,
                new Label("Franja:"), cbTurnos);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);

        // Guardamos de forma asíncrona (hilos)
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnGuardarType) {
                ejecutarGuardadoAsync(cbEmpleados.getValue(), cbTurnos.getValue(), dpFecha.getValue());
            }
        });
    }

    /**
     * Método que mueve la carga pesada de la BD a un hilo secundario
     *
     * @param sel campo del formulario del usuario
     * @param tipo campo del formulario del tipo de turno
     * @param fecha campo del formulario de la fecha del turno
     */
    private void ejecutarGuardadoAsync(Usuario sel, String tipo, LocalDate fecha) {
        if (sel == null || tipo == null || fecha == null) {
            AlertaUtil.mostrarAdvertencia("Campos incompletos", "Rellene todos los campos.");
            return;
        }

        Task<Boolean> saveTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return turnoDAO.insertarTurno(sel.getId(), tipo, fecha);
            }
        };

        saveTask.setOnSucceeded(e -> {
            if (saveTask.getValue()) {
                dibujarCalendario(); // Refresca la UI
            } else {
                AlertaUtil.mostrarError("Error", "No se pudo guardar en la base de datos.");
            }
        });

        new Thread(saveTask).start();
    }

    /**
     * Método que crea y configura el ComboBox de empleados de forma limpia
     */
    private ComboBox<Usuario> crearComboBoxEmpleados() {
        // Obtenemos los usuarios filtrados por el negocio del administrador actual
        int negocioId = SesionUtil.getUsuario().getNegocioId();
        ComboBox<Usuario> cb = new ComboBox<>(FXCollections.observableArrayList(usuarioDAO.obtenerPorNegocio(negocioId)));

        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty ? null : u.getNombre() + " " + u.getApellidos());
            }
        });
        cb.setButtonCell(cb.getCellFactory().call(null));
        return cb;
    }
}