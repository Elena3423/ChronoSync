package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuEmpleadosControlador {
    // Botones del menú lateral
    @FXML private Button btnPanelPrincipal, btnTurnos, btnIncidencias, btnExportaciones, btnConfiguracion, btnCerrarSesion;

    // Etiquetas de perfil de usuario
    @FXML private Label txtNombre, txtRol;

    // Componentes de la tabla
    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, Integer> colPuesto;
    @FXML private TableColumn<Usuario, Boolean> colEstado;
    @FXML private TableColumn<Usuario, Integer> colTurnos;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private TurnoDAO turnoDAO = new TurnoDAO();

    private Map<Integer, Integer> cacheTurnos = new HashMap<>();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        mostrarDatosUsuario();
        configurarNavegacion();

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("rolId"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colTurnos.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPuesto.setCellFactory(column -> new TableCell<Usuario, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
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

        colEstado.setCellFactory(column -> new TableCell<Usuario, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
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

        colTurnos.setCellFactory(column -> new TableCell<Usuario, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Integer total = cacheTurnos.getOrDefault(item, 0);
                    setText(total + " turnos");
                    setStyle("-fx-text-fill: #333333;");
                }
            }
        });

        actualizarTabla();
    }

    /**
     * Método que actualiza la tabla
     */
    private void actualizarTabla() {
        // Añadimos el icono de carga como placeholder de la tabla
        ProgressIndicator cargando = new ProgressIndicator();
        cargando.setMaxSize(50, 50);
        tabla.setPlaceholder(cargando);

        // Creamos la tarea para no bloquear la interfaz
        Task<List<Usuario>> tarea = new Task<>() {
            @Override
            protected List<Usuario> call() throws Exception {
                List<Usuario> lista = usuarioDAO.obtenerTodos();
                // Llenamos el caché en segundo plano
                for (Usuario u : lista) {
                    int conteo = turnoDAO.contarTurnosMesUsuario(u.getId());
                    cacheTurnos.put(u.getId(), conteo);
                }
                return lista;
            }
        };

        // Cuando termina, cargamos los datos en la tabla
        tarea.setOnSucceeded(e -> {
            List<Usuario> lista = tarea.getValue();
            tabla.setItems(FXCollections.observableArrayList(lista));

            // Si la lista está vacía después de la carga, mostramos un mensaje
            if (lista.isEmpty()) {
                tabla.setPlaceholder(new Label("No hay empleados registrados."));
            }
        });

        // En caso de fallo, informamos al usuario en la tabla
        tarea.setOnFailed(e -> {
            tabla.setPlaceholder(new Label("Error al cargar los datos de empleados."));
        });

        new Thread(tarea).start();
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
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }
}