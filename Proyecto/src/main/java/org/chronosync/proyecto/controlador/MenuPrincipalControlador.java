package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.chronosync.proyecto.dao.ExportacionDAO;
import org.chronosync.proyecto.dao.IncidenciaDAO;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.util.SesionUtil;

public class MenuPrincipalControlador {
    @FXML private Label txtNombre;
    @FXML private Label txtRol;

    @FXML private Label titulo;
    @FXML private Label subtitulo;

    @FXML private Label tarjetaTit1;
    @FXML private Label tarjetaDato1;
    @FXML private Label tarjetaSubdato1;

    @FXML private Label tarjetaTit2;
    @FXML private Label tarjetaDato2;
    @FXML private Label tarjetaSubdato2;

    @FXML private Label tarjetaTit3;
    @FXML private Label tarjetaDato3;
    @FXML private Label tarjetaSubdato3;

    @FXML private Label tarjetaTit4;
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
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            titulo.setText("Menú de Administración");
            subtitulo.setText("Bienvenido, " + SesionUtil.getUsuario().getNombre() + ". Aquí puedes gestionar todos los aspectos del sistema.");

            tarjetaTit1.setText("Empleados Activos");
            tarjetaDato1.setText(String.valueOf(usuarioDAO.contarUsuariosActivosPorNegocio(SesionUtil.getUsuario().getNegocioId())));
            tarjetaSubdato1.setText(usuarioDAO.contarUsuariosInactivosPorNegocio(SesionUtil.getUsuario().getNegocioId()) + " inactivos");

            tarjetaTit2.setText("Turnos");
            tarjetaDato2.setText(String.valueOf(negocioDAO.contarTurnosMesActual(SesionUtil.getUsuario().getNegocioId())));
            tarjetaSubdato2.setText("Este mes");

            tarjetaTit3.setText("Incidencias Pendientes");
            tarjetaDato3.setText(String.valueOf(incidenciaDAO.contarInformesPendientes(SesionUtil.getUsuario().getNegocioId())));
            tarjetaSubdato3.setText("Requieren atención");

            tarjetaTit4.setText("Informes Generados");
            tarjetaDato4.setText(String.valueOf(exportacionDAO.contarExportacionesMesActual(SesionUtil.getUsuario().getNegocioId())));
            tarjetaSubdato4.setText("Este mes");

            titCaja1.setText("Gestión de Empleados");
            subtitCaja1.setText("Listado completo del personal");
            btnCaja1.setText("Nuevo Empleado");

            titCaja2.setText("Incidencias Recientes");
            subtitCaja2.setText("Últimas solicitudes y reportes");
            btnCaja2.setText("Ver Todas");
        }
    }
}
