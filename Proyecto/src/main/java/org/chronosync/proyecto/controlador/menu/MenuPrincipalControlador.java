package org.chronosync.proyecto.controlador.menu;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.ExportacionDAO;
import org.chronosync.proyecto.dao.IncidenciaDAO;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.CargarImagenUtil;
import org.chronosync.proyecto.util.SesionUtil;

public class MenuPrincipalControlador {
    // Botones de navegación lateral
    @FXML private Button btnEmpleados, btnTurnos, btnIncidencias, btnExportaciones, btnConfiguracion, btnCerrarSesion;

    // Etiquetas de texto para el saludo y el cargo
    @FXML private Label txtNombre, txtRol, titulo, subtitulo;

    // Elementos de la caja de turno actual (solo para empleados)
    @FXML private HBox hboxTurnos;
    @FXML private Label infoCaja1;

    // Etiquetas y contenedores de las 4 tarjetas de estadísticas
    @FXML private Label tarjetaTit1, tarjetaDato1, tarjetaSubdato1;
    @FXML private Label tarjetaTit2, tarjetaDato2, tarjetaSubdato2;
    @FXML private Label tarjetaTit3, tarjetaDato3, tarjetaSubdato3;
    @FXML private VBox tarjeta4;
    @FXML private Label tarjetaTit4, tarjetaDato4, tarjetaSubdato4;

    // Iconos de las tarjetas
    @FXML private ImageView imgTarjeta1, imgTarjeta2, imgTarjeta3, imgTarjeta4;

    UsuarioDAO usuarioDAO = new UsuarioDAO();
    NegocioDAO negocioDAO = new NegocioDAO();
    IncidenciaDAO incidenciaDAO = new IncidenciaDAO();
    ExportacionDAO exportacionDAO = new ExportacionDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        mostrarDatosUsuario();
        menuSegunRol();
        cargarDatos();
        configurarNavegacion();
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
     * Método que muestra un menú distinto en función del rol del usuario
     */
    private void menuSegunRol() {
        switch (SesionUtil.getUsuario().getRolId()) {
            // Si es administrador:
            case 1:
                titulo.setText("Menú de Administración");
                subtitulo.setText("Bienvenido, " + SesionUtil.getUsuario().getNombre() + ". Aquí puedes gestionar todos los aspectos del sistema.");

                // El administrador no necesita ver el turno de hoy
                hboxTurnos.setVisible(false);
                hboxTurnos.setManaged(false);

                // Configuramos las 4 tarjetas para el jefe
                tarjetaTit1.setText("Empleados Activos");
                tarjetaDato1.setText("0");
                CargarImagenUtil.establecerImagen(imgTarjeta1, "/img/monigoteAzul.png");
                tarjetaSubdato1.setText("Empleados inactivos");

                tarjetaTit2.setText("Turnos");
                tarjetaDato2.setText("0");
                CargarImagenUtil.establecerImagen(imgTarjeta2, "/img/calendarioIcono.png");
                tarjetaSubdato2.setText("Este mes");

                tarjetaTit3.setText("Incidencias Pendientes");
                tarjetaDato3.setText("0");
                CargarImagenUtil.establecerImagen(imgTarjeta3, "/img/alertaIcono.png");
                tarjetaSubdato3.setText("Requieren atención");

                tarjetaTit4.setText("Informes Generados");
                tarjetaDato4.setText("0");
                CargarImagenUtil.establecerImagen(imgTarjeta4, "/img/archivoIcono.png");
                tarjetaSubdato4.setText("Este mes");
                break;

            // Si es empleado:
            case 2:
                titulo.setText("Mi Panel");
                subtitulo.setText("Bienvenido, " + SesionUtil.getUsuario().getNombre() + ". Aquí puedes ver tu información personal y turnos asignados.");

                // Configuramos las 3 tarjetas para el trabajador
                tarjetaTit1.setText("Próximos Turnos");
                tarjetaDato1.setText("0");
                CargarImagenUtil.establecerImagen(imgTarjeta1, "/img/calendarioIconoAzul.png");
                tarjetaSubdato1.setText("Esta semana");

                tarjetaTit2.setText("Incidencias");
                tarjetaDato2.setText("0");
                CargarImagenUtil.establecerImagen(imgTarjeta2, "/img/alertaIcono.png");
                tarjetaSubdato2.setText("Registradas");

                tarjetaTit3.setText("Horas Trabajadas");
                tarjetaDato3.setText("0");
                CargarImagenUtil.establecerImagen(imgTarjeta3, "/img/relojIcono.png");
                tarjetaSubdato3.setText("Este mes");

                // El empleado no ve la cuarta tarjeta
                tarjeta4.setVisible(false);
                tarjeta4.setManaged(false);
                break;
        }
    }

    /**
     * Método que carga los datos
     */
    private void cargarDatos() {
        Usuario user = SesionUtil.getUsuario();
        int negocioId = user.getNegocioId();

        // Creamos una tarea para ejecutar fuera del hilo principal
        Task<Void> tarea = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (user.getRolId() == 1) {

                    // Consultas para el administrador
                    int activos = usuarioDAO.contarUsuariosActivosPorNegocio(negocioId);
                    int inactivos = usuarioDAO.contarUsuariosInactivosPorNegocio(negocioId);
                    int turnos = negocioDAO.contarTurnosMesActual(negocioId);
                    int incidencias = incidenciaDAO.contarInformesPendientes(negocioId);
                    int exportaciones = exportacionDAO.contarExportacionesMesActual(negocioId);

                    // Platform.runLater actualiza los textos en la pantalla cuando los datos están listos
                    javafx.application.Platform.runLater(() -> {
                        tarjetaDato1.setText(String.valueOf(activos));
                        tarjetaSubdato1.setText(inactivos + " empleados inactivos");
                        tarjetaDato2.setText(String.valueOf(turnos));
                        tarjetaDato3.setText(String.valueOf(incidencias));
                        tarjetaDato4.setText(String.valueOf(exportaciones));
                    });

                } else {

                    // Consultas para el empleado
                    final int usuarioId = SesionUtil.getUsuario().getId();
                    int turnos = negocioDAO.contarTurnosSemanaUsuario(usuarioId);
                    int incidencias = incidenciaDAO.contarIncidenciasUsuario(usuarioId);
                    double horas = usuarioDAO.calcularHorasTrabajadasMes(usuarioId);
                    final String turnoHoy = negocioDAO.obtenerTurnoHoyUsuario(usuarioId);

                    javafx.application.Platform.runLater(() -> {
                        tarjetaDato1.setText(String.valueOf(turnos));
                        tarjetaDato2.setText(String.valueOf(incidencias));
                        tarjetaDato3.setText(String.format("%.1f", horas));
                        infoCaja1.setText(turnoHoy);
                    });
                }
                return null;
            }
        };

        // Iniciamos el hilo de la tarea
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true); // Cerramos el hilo automáticamente si cerramos la app
        hilo.start();
    }

    /**
     * Método que cambia la escena en función del botón pulsado
     *
     * @param e evento del ratón
     */
    private void navegar(MouseEvent e) {
        Button btn = (Button) e.getSource();

        String fxmlRuta;
        if (btn == btnEmpleados) {
            fxmlRuta = "/fxml/menuEmpleados.fxml";
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
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }
}