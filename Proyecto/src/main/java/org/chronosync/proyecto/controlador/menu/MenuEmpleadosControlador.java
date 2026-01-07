package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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

    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, Integer> colPuesto;
    @FXML private TableColumn<Usuario, Boolean> colEstado;
    @FXML private TableColumn<Usuario, Integer> colTurnos;
    @FXML private TableColumn<Usuario, Void> colAcciones;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private TurnoDAO turnoDAO = new TurnoDAO();

    @FXML
    public void initialize() {
        configurarNavegacion();

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
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Traducción de ID a Texto
                    if (item == 1) {
                        setText("Administrador");
                        setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;"); // Azul para admin
                    } else if (item == 2) {
                        setText("Empleado");
                        setStyle("-fx-text-fill: #7f8c8d;"); // Gris para empleado
                    } else {
                        setText("Rol " + item);
                    }
                }
            }
        });

        // Formatear columna estado
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
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // Verde
                    } else {
                        setText("Inactivo");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Rojo
                    }
                }
            }
        });

        // Formatear columna turnos
        colTurnos.setCellFactory(column -> new TableCell<Usuario, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // 'item' es el ID del usuario
                    int totalTurnos = turnoDAO.contarTurnosMesUsuario(item);

                    setText(totalTurnos + " turnos");
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        configurarColumnaAcciones();

        actualizarTabla();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnVerTurnos = new Button("Ver Turnos");
            private final HBox pane = new HBox(btnEditar, btnVerTurnos);

            {
                pane.setSpacing(10);
                btnEditar.getStyleClass().add("boton-editar"); // Puedes definir este estilo en tu CSS
                btnVerTurnos.getStyleClass().add("boton-blancoNego"); // Reutilizando tu clase CSS

                // Acción Editar
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    manejarEditar(usuario);
                });

                // Acción Ver Turnos
                btnVerTurnos.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    manejarVerTurnos(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void manejarEditar(Usuario usuario) {
        System.out.println("Editando a: " + usuario.getNombre());
        // Aquí abrirías el JDialog o ventana de edición
        // Ejemplo: FormularioEmpleado.abrir(usuario);
    }

    private void manejarVerTurnos(Usuario usuario) {
        // Redirigir a la vista de turnos
        Stage stage = (Stage) tabla.getScene().getWindow();
        CargadorUtil.cambiarEscena(stage, "/fxml/menuTurnos.fxml");
        // Nota: Si quieres filtrar, deberías pasar el ID del usuario a través de una clase de utilidad o Singleton
    }

    private void actualizarTabla() {
        List<Usuario> lista = usuarioDAO.obtenerTodos();
        tabla.setItems(FXCollections.observableArrayList(lista));
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
