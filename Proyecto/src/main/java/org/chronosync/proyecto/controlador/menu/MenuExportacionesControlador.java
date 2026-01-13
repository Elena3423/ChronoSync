package org.chronosync.proyecto.controlador.menu;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.chronosync.proyecto.dao.ExportacionDAO;
import org.chronosync.proyecto.dao.IncidenciaDAO;
import org.chronosync.proyecto.dao.TurnoDAO;
import org.chronosync.proyecto.dao.UsuarioDAO;
import org.chronosync.proyecto.modelo.Exportacion;
import org.chronosync.proyecto.modelo.Incidencia;
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

    @FXML private Label txtNombre, txtRol;

    @FXML private Label lblEmpleado1;
    @FXML private Label lblEmpleado3;

    @FXML private ChoiceBox<String> choicePeriodo1;
    @FXML private ChoiceBox<Usuario> choiceEmpleado1;
    @FXML private Button btnPDF1, btnExcel1;

    @FXML private ChoiceBox<String> choicePeriodo2;
    @FXML private ChoiceBox<String> choiceEstado2;
    @FXML private Button btnPDF2, btnExcel2;

    @FXML private ChoiceBox<String> choicePeriodo3;
    @FXML private ChoiceBox<Usuario> choiceEmpleado3;
    @FXML private Button btnPDF3, btnExcel3;

    @FXML private ChoiceBox<String> choicePeriodo4;
    @FXML private Button btnPDF4, btnExcel4;

    // Referencias a DAOs
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ExportacionDAO exportacionDAO = new ExportacionDAO();
    private TurnoDAO turnoDAO = new TurnoDAO();
    private IncidenciaDAO incidenciaDAO = new IncidenciaDAO();

    @FXML
    public void initialize() {
        btnPanelPrincipal.setOnMouseClicked(this::navegar);
        btnEmpleados.setOnMouseClicked(this::navegar);
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);

        mostrarDatosUsuario();

        configurarInformeTurnos();
        configurarInformeIncidencias();
        configurarInformeHoras();
        configurarInformeCompleto();
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

    private void mostrarDatosUsuario() {
        txtNombre.setText(SesionUtil.getUsuario().getNombre());
        if (SesionUtil.getUsuario().getRolId().equals(1)) {
            txtRol.setText("Administrador");
        } else if (SesionUtil.getUsuario().getRolId().equals(2)) {
            txtRol.setText("Empleado");
        }
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
            List<Usuario> empleados = usuarioDAO.obtenerTodosLosEmpleados();
            if (empleados != null && !empleados.isEmpty()) {
                choiceEmpleado1.setItems(FXCollections.observableArrayList(empleados));
            }
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

    private void configurarInformeIncidencias() {
        // 1. Llenar Periodos y Estados
        choicePeriodo2.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo2.setValue("Mes actual");

        choiceEstado2.getItems().addAll("Todos", "Pendiente", "Validada", "Denegada");
        choiceEstado2.setValue("Todos");

        // 2. Seguridad: ¿Quién está logueado?
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        // 3. Asignar Eventos a los botones
        btnPDF2.setOnAction(e -> exportarIncidencias("PDF"));
        btnExcel2.setOnAction(e -> exportarIncidencias("EXCEL"));
    }

    private void configurarInformeHoras() {
        // 1. Llenar Periodos
        choicePeriodo3.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo3.setValue("Mes actual");

        // 2. Configurar cómo se ven los empleados en el ChoiceBox (Nombre y Apellido)
        choiceEmpleado3.setConverter(new javafx.util.StringConverter<Usuario>() {
            @Override
            public String toString(Usuario u) {
                return (u == null) ? "" : u.getNombre(); // O u.getNombre() + " " + u.getApellidos()
            }
            @Override
            public Usuario fromString(String string) { return null; }
        });

        // 3. Seguridad y Carga de datos
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        if (usuarioLogueado.getRolId() == 1) { // ADMIN
            List<Usuario> empleados = usuarioDAO.obtenerTodosLosEmpleados();
            if (empleados != null && !empleados.isEmpty()) {
                choiceEmpleado3.setItems(FXCollections.observableArrayList(empleados));
            }
        } else { // EMPLEADO
            choiceEmpleado3.setVisible(false);
            choiceEmpleado3.setManaged(false);
            choiceEmpleado3.setValue(usuarioLogueado);
            lblEmpleado3.setVisible(false);
            lblEmpleado3.setManaged(false);
        }

        // 4. Eventos
        btnPDF3.setOnAction(e -> exportarInformeHoras("PDF"));
        btnExcel3.setOnAction(e -> exportarInformeHoras("EXCEL"));
    }

    private void configurarInformeCompleto() {
        choicePeriodo4.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo4.setValue("Mes actual");

        btnPDF4.setOnAction(e -> exportarInformeCompleto("PDF"));
        btnExcel4.setOnAction(e -> exportarInformeCompleto("EXCEL"));
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

    private void exportarIncidencias(String formato) {
        // 1. Configurar el diálogo de guardado
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe de Incidencias");
        String ext = formato.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Incidencias_" + choicePeriodo2.getValue().replace(" ", "_") + ext);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato, "*" + ext));

        File destino = fc.showSaveDialog(btnPDF2.getScene().getWindow());

        if (destino != null) {
            try {
                Usuario usuarioActual = SesionUtil.getUsuario();

                // 2. Consultar datos (Usando tu IncidenciaDAO con el JOIN a turnos)
                List<Incidencia> lista = incidenciaDAO.obtenerIncidenciasPorFiltro(
                        usuarioActual.getId(),
                        choicePeriodo2.getValue(),
                        choiceEstado2.getValue()
                );

                // 3. Generar el archivo según el formato
                if (formato.equalsIgnoreCase("PDF")) {
                    ExportadorPDF.generarInformeIncidencias(destino, choicePeriodo2.getValue(), usuarioActual.getNombre(), lista);
                } else {
                    ExportadorExcel.generarExcelIncidencias(destino, choicePeriodo2.getValue(), usuarioActual.getNombre(), lista);
                }

                // 4. Registrar la exportación en la base de datos para auditoría
                Exportacion reg = new Exportacion();
                reg.setTipoFormato(formato);
                reg.setFechaGeneracion(LocalDateTime.now());
                reg.setUsuarioId(usuarioActual.getId());
                reg.setNegocioId(usuarioActual.getNegocioId());
                exportacionDAO.insertar(reg);

                System.out.println("Exportación de incidencias realizada con éxito.");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void exportarInformeHoras(String formato) {
        // 1. Configurar el guardado
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe de Horas");
        String ext = formato.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Horas_" + choicePeriodo3.getValue().replace(" ", "_") + ext);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato, "*" + ext));

        File destino = fc.showSaveDialog(btnPDF3.getScene().getWindow());

        if (destino != null) {
            try {
                // 2. Obtener el empleado (Admin elige de la lista, Empleado es él mismo)
                Usuario emp = (SesionUtil.getUsuario().getRolId() == 1)
                        ? choiceEmpleado3.getValue()
                        : SesionUtil.getUsuario();

                // 3. Obtener datos del DAO (Asegúrate de tener obtenerResumenHoras en turnoDAO)
                List<Turno> datos = turnoDAO.obtenerResumenHoras(emp.getId(), choicePeriodo3.getValue());

                // 4. Exportar
                if (formato.equalsIgnoreCase("PDF")) {
                    // Si aún no tienes este método en ExportadorPDF, lo crearemos luego
                    ExportadorPDF.generarInformeHoras(destino, choicePeriodo3.getValue(), emp.getNombre(), datos);
                } else {
                    ExportadorExcel.generarExcelHoras(destino, choicePeriodo3.getValue(), emp.getNombre(), datos);
                }

                // 5. REGISTRO MANUAL (Igual que en tus otros métodos)
                Exportacion reg = new Exportacion();
                reg.setTipoFormato(formato);
                reg.setFechaGeneracion(LocalDateTime.now());
                reg.setUsuarioId(SesionUtil.getUsuario().getId()); // ID del que pulsa el botón
                reg.setNegocioId(SesionUtil.getUsuario().getNegocioId());

                exportacionDAO.insertar(reg);

                System.out.println("Exportación de horas completada.");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void exportarInformeCompleto(String formato) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe Completo");
        String ext = formato.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Completo_" + choicePeriodo4.getValue().replace(" ", "_") + ext);

        File destino = fc.showSaveDialog(btnPDF4.getScene().getWindow());

        if (destino != null) {
            try {
                Usuario usuario = SesionUtil.getUsuario();
                String periodo = choicePeriodo4.getValue();

                // 1. Recopilar todos los datos
                List<Turno> turnos = turnoDAO.obtenerTurnosPorFiltro(usuario.getId(), periodo);
                List<Incidencia> incidencias = incidenciaDAO.obtenerIncidenciasPorFiltro(usuario.getId(), periodo, "Todos");

                // 2. Exportar según formato
                if (formato.equalsIgnoreCase("PDF")) {
                    ExportadorPDF.generarInformeCompleto(destino, periodo, usuario.getNombre(), turnos, incidencias);
                } else {
                    ExportadorExcel.generarExcelCompleto(destino, periodo, usuario.getNombre(), turnos, incidencias);
                }

                // 3. Registro de auditoría
                Exportacion reg = new Exportacion();
                reg.setTipoFormato(formato + "_COMPLETO");
                reg.setFechaGeneracion(LocalDateTime.now());
                reg.setUsuarioId(usuario.getId());
                reg.setNegocioId(usuario.getNegocioId());
                exportacionDAO.insertar(reg);

                System.out.println("Informe consolidado generado con éxito.");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
