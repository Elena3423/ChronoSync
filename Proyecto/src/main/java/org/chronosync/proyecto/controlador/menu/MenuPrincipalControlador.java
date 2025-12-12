package org.chronosync.proyecto.controlador.menu;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.ExportacionDAO;
import org.chronosync.proyecto.dao.IncidenciaDAO;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.CargarImagenUtil;
import org.chronosync.proyecto.util.SesionUtil;

import java.io.InputStream;

public class MenuPrincipalControlador {
    @FXML private Button btnEmpleados;
    @FXML private Button btnTurnos;
    @FXML private Button btnIncidencias;
    @FXML private Button btnExportaciones;
    @FXML private Button btnConfiguracion;

    @FXML private Label txtNombre;
    @FXML private Label txtRol;
    @FXML private Button btnCerrarSesion;

    @FXML private Label titulo;
    @FXML private Label subtitulo;

    @FXML private HBox hboxTurnos;
    @FXML private Label infoCaja1;

    @FXML private Label tarjetaTit1;
    @FXML private ImageView imgTarjeta1;
    @FXML private Label tarjetaDato1;
    @FXML private Label tarjetaSubdato1;

    @FXML private Label tarjetaTit2;
    @FXML private ImageView imgTarjeta2;
    @FXML private Label tarjetaDato2;
    @FXML private Label tarjetaSubdato2;

    @FXML private Label tarjetaTit3;
    @FXML private ImageView imgTarjeta3;
    @FXML private Label tarjetaDato3;
    @FXML private Label tarjetaSubdato3;

    @FXML private VBox tarjeta4;
    @FXML private Label tarjetaTit4;
    @FXML private ImageView imgTarjeta4;
    @FXML private Label tarjetaDato4;
    @FXML private Label tarjetaSubdato4;

    @FXML private Label titCaja1;
    @FXML private Label subtitCaja1;
    @FXML private Button btnCaja1;

    @FXML private Label titCaja2;
    @FXML private Label subtitCaja2;
    @FXML private Button btnCaja2;

    UsuarioDAO usuarioDAO = new UsuarioDAO();
    NegocioDAO negocioDAO = new NegocioDAO();
    IncidenciaDAO incidenciaDAO = new IncidenciaDAO();
    ExportacionDAO exportacionDAO = new ExportacionDAO();

    @FXML
    public void initialize() {
        mostrarDatosUsuario();
        menuSegunRol();
        cargarDatos();

        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnExportaciones.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }

    private void mostrarDatosUsuario() {
        txtNombre.setText(SesionUtil.getUsuario().getNombre());
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            txtRol.setText("Administrador");
        } else if (SesionUtil.getUsuario().getRolId().equals(2)) {
            txtRol.setText("Empleado");
        }
    }

    private void menuSegunRol() {
        switch (SesionUtil.getUsuario().getRolId()) {
            case 1:
                titulo.setText("Menú de Administración");
                subtitulo.setText("Bienvenido, " + SesionUtil.getUsuario().getNombre() + ". Aquí puedes gestionar todos los aspectos del sistema.");

                hboxTurnos.setVisible(false);
                hboxTurnos.setManaged(false);

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
                tarjetaDato4.setText("4");
                CargarImagenUtil.establecerImagen(imgTarjeta4, "/img/archivoIcono.png");
                tarjetaSubdato4.setText("Este mes");

                titCaja1.setText("Gestión de Empleados");
                subtitCaja1.setText("Listado completo del personal");
                btnCaja1.setText("Nuevo Empleado");

                titCaja2.setText("Incidencias Recientes");
                subtitCaja2.setText("Últimas solicitudes y reportes");
                btnCaja2.setText("Ver Todas");

                break;

            case 2:
                titulo.setText("Mi Panel");
                subtitulo.setText("Bienvenido, " + SesionUtil.getUsuario().getNombre() + ". Aquí puedes ver tu información personal y turnos asignados.");

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

                tarjeta4.setVisible(false);
                tarjeta4.setManaged(false);

                titCaja1.setText("Próximos Turnos");
                subtitCaja1.setText("Tu calendario de la semana");
                btnCaja1.setText("Ver calendario completo");

                titCaja2.setText("Mis Incidencias");
                subtitCaja2.setText("Historial de solicitudes");
                btnCaja2.setText("Nueva Incidencia");

                break;

            default:
                txtRol.setText("Rol Desconocido");
        }
    }

    private void cargarDatos() {
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            Task<Void> tarea =  new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    final int empleadosActivos = usuarioDAO.contarUsuariosActivosPorNegocio(SesionUtil.getUsuario().getNegocioId());
                    final int empleadosInactivos = usuarioDAO.contarUsuariosInactivosPorNegocio(SesionUtil.getUsuario().getNegocioId());
                    final int turnosMes = negocioDAO.contarTurnosMesActual(SesionUtil.getUsuario().getNegocioId());
                    final int informesPendientes = incidenciaDAO.contarInformesPendientes(SesionUtil.getUsuario().getNegocioId());
                    final int exportacionesMes = exportacionDAO.contarExportacionesMesActual(SesionUtil.getUsuario().getNegocioId());

                    javafx.application.Platform.runLater(() -> {
                        tarjetaDato1.setText(String.valueOf(empleadosActivos));
                        tarjetaSubdato1.setText(empleadosInactivos + " empleados inactivos");
                        tarjetaDato2.setText(String.valueOf(turnosMes));
                        tarjetaDato3.setText(String.valueOf(informesPendientes));
                        tarjetaDato4.setText(String.valueOf(exportacionesMes));
                    });
                    return null;
                }
            };
            new Thread(tarea).start();
        }
    }

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

    private void cerrarSesion(MouseEvent e) {
        Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
        SesionUtil.cerrarSesion(stage);
    }
}
