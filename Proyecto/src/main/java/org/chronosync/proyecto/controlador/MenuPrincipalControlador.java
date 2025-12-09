package org.chronosync.proyecto.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import org.chronosync.proyecto.dao.NegocioDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.util.SesionUtil;

public class MenuPrincipalControlador {
    @FXML private Label txtNombre;
    @FXML private Label txtRol;

    @FXML private Label tituloPrincipal;
    @FXML private Label subtituloPrincipal;

    @FXML private VBox caja1;

    @FXML private Label txt1;
    @FXML private Label txtNum1;
    @FXML private Label txtSub1;

    @FXML private Label txt2;
    @FXML private Label txtNum2;
    @FXML private Label txtSub2;

    @FXML private Label txt3;
    @FXML private Label txtNum3;
    @FXML private Label txtSub3;

    @FXML private Label txt4;
    @FXML private Label txtNum4;
    @FXML private Label txtSub4;

    @FXML private Label txtTitulo2;
    @FXML private Label txtSubtitulo2;
    @FXML private Button btnPanelPrincipal1;

    @FXML private Label txtTitulo3;
    @FXML private Label txtSubtitulo3;
    @FXML private Button btnPanelPrincipal2;

    UsuarioDAO usuarioDAO = new UsuarioDAO();

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
            tituloPrincipal.setText("Panel de Administración");
            subtituloPrincipal.setText("Bienvenido " + SesionUtil.getUsuario().getNombre() + ". Aquí puedes gestionar todos los aspectos del sistema. ");
            caja1.setVisible(false);

            txt1.setText("Empleados Activos");
            txtNum1.setText(String.valueOf(usuarioDAO.contarUsuariosActivosPorNegocio(SesionUtil.getUsuario().getNegocioId())));
            txtSub1.setText(usuarioDAO.contarUsuariosInactivosPorNegocio(SesionUtil.getUsuario().getNegocioId()) + " inactivos");

            txt2.setText("Turnos este Mes");

            txt3.setText("Incidencias Pendientes");
            txtSub3.setText("Requieren atención");

            txt4.setText("Informes Generados");
            txtSub4.setText("Este mes");

            txtTitulo2.setText("Gestión de Empleados");
            txtSubtitulo2.setText("Listado completo del personal");
            btnPanelPrincipal1.setText("Nuevo Empleado");

            txtTitulo3.setText("Incidencias Recientes");
            txtSubtitulo3.setText("Últimas solicitudes y reportes");
            btnPanelPrincipal2.setText("Ver Todas");
        }
    }
}
