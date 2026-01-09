package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.ExportacionDAO;
import org.chronosync.proyecto.dao.TurnoDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Exportacion;
import org.chronosync.proyecto.modelo.Turno;
import org.chronosync.proyecto.modelo.Usuario;
import org.chronosync.proyecto.util.CargadorUtil;
import org.chronosync.proyecto.util.ExportadorExcel;
import org.chronosync.proyecto.util.ExportadorPDF;
import org.chronosync.proyecto.util.SesionUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class MenuExportacionesControlador {
    @FXML private Button btnPanelPrincipal;
    @FXML private Button btnEmpleados;
    @FXML private Button btnTurnos;
    @FXML private Button btnIncidencias;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;

    @FXML private Label lblEmpleado1;

    @FXML private ChoiceBox<String> choicePeriodo1;
    @FXML private ChoiceBox<Usuario> choiceEmpleado1; // Asumiendo que tienes una clase Usuario
    @FXML private CheckBox checkHoras1, checkTipos1;
    @FXML private Button btnPDF1, btnExcel1;

    // Referencias a DAOs
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ExportacionDAO exportacionDAO = new ExportacionDAO();
    private TurnoDAO turnoDAO = new TurnoDAO();

    @FXML
    public void initialize() {
        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);

        configurarInformeTurnos();
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
        } else if (btn == btnIncidencias) {
            fxmlRuta = "/fxml/menuIncidencias.fxml";
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

    private void configurarInformeTurnos() {
        // 1. Llenar Periodos
        choicePeriodo1.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo1.setValue("Mes actual");

        // 2. Seguridad: ¿Quién está logueado?
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        if (usuarioLogueado.getRolId() == 1) { // ADMIN
            // Cargamos todos los empleados en el choice
            choiceEmpleado1.setItems(FXCollections.observableArrayList(usuarioDAO.obtenerTodosLosEmpleados()));
        } else { // EMPLEADO
            // Ocultamos el selector de empleados y el label previo si fuera necesario
            choiceEmpleado1.setVisible(false);
            choiceEmpleado1.setManaged(false);
            lblEmpleado1.setVisible(false);
            lblEmpleado1.setManaged(false);
            // Podrías ponerle un valor por defecto para que la lógica interna no falle
            choiceEmpleado1.setValue(usuarioLogueado);
        }

        // 3. Asignar Eventos a los botones
        btnPDF1.setOnAction(e -> exportarInformeTurnos("PDF"));
        btnExcel1.setOnAction(e -> exportarInformeTurnos("EXCEL"));
    }

    private void registrarExportacionEnBD(String formato) {
        Exportacion exp = new Exportacion();
        exp.setTipoFormato(formato);
        exp.setFechaGeneracion(LocalDateTime.now());
        exp.setUsuarioId(SesionUtil.getUsuario().getId());
        exp.setNegocioId(SesionUtil.getUsuario().getNegocioId());

        exportacionDAO.insertar(exp);
    }

    private void exportarInformeTurnos(String formato) {
        // 1. Configurar el guardado de archivo
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe de Turnos");
        String ext = formato.equals("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Turnos_" + choicePeriodo1.getValue().replace(" ", "_") + ext);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato, "*" + ext));

        File destino = fc.showSaveDialog(btnPDF1.getScene().getWindow());

        if (destino != null) {
            try {
                // 2. Obtener datos según el usuario (Admin elige, Empleado es él mismo)
                Usuario emp = (SesionUtil.getUsuario().getRolId() == 1)
                        ? choiceEmpleado1.getValue()
                        : SesionUtil.getUsuario();

                // Aquí llamas a tu DAO (Asegúrate de tener este método)
                List<Turno> lista = turnoDAO.obtenerTurnosPorFiltro(emp.getId(), choicePeriodo1.getValue());

                // 3. Llamar al exportador correspondiente
                if (formato.equals("PDF")) {
                    ExportadorPDF.generarInformeTurnos(destino, choicePeriodo1.getValue(), emp.getNombre(), lista);
                } else {
                    ExportadorExcel.generarExcelTurnos(destino, choicePeriodo1.getValue(), emp.getNombre(), lista);
                }

                // 4. Registrar en BD con tu DAO
                Exportacion exp = new Exportacion();
                exp.setTipoFormato(formato);
                exp.setFechaGeneracion(LocalDateTime.now());
                exp.setUsuarioId(SesionUtil.getUsuario().getId());
                exp.setNegocioId(SesionUtil.getUsuario().getNegocioId());

                exportacionDAO.insertar(exp);

                System.out.println("Exportación completada con éxito.");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
