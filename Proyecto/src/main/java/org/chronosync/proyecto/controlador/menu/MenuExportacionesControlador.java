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
    // Botones de navegación lateral
    @FXML private Button btnPanelPrincipal, btnEmpleados, btnTurnos, btnIncidencias, btnConfiguracion, btnCerrarSesion;
    @FXML private Label txtNombre, txtRol;

    // Elementos visuales de los 4 tipos de informes (Turnos, Incidencias, Horas, Completo)
    @FXML private Label lblEmpleado1, lblEmpleado3;
    @FXML private ChoiceBox<String> choicePeriodo1, choicePeriodo2, choicePeriodo3, choicePeriodo4, choiceEstado2;
    @FXML private ChoiceBox<Usuario> choiceEmpleado1, choiceEmpleado3;
    @FXML private Button btnPDF1, btnExcel1, btnPDF2, btnExcel2, btnPDF3, btnExcel3, btnPDF4, btnExcel4;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ExportacionDAO exportacionDAO = new ExportacionDAO();
    private TurnoDAO turnoDAO = new TurnoDAO();
    private IncidenciaDAO incidenciaDAO = new IncidenciaDAO();

    /**
     * Método que se ejecuta al abrir la pantalla
     */
    @FXML
    public void initialize() {
        mostrarDatosUsuario();
        configurarNavegacion();

        configurarInformeTurnos();
        configurarInformeIncidencias();
        configurarInformeHoras();
        configurarInformeCompleto();
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
        btnTurnos.setOnMouseClicked(this::navegar);
        btnIncidencias.setOnMouseClicked(this::navegar);
        btnConfiguracion.setOnMouseClicked(this::navegar);
        btnCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }

    /**
     * Método que configura el informe orientado a los turnos de un usuario
     */
    private void configurarInformeTurnos() {
        // 1. Llenar Periodos
        choicePeriodo1.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo1.setValue("Mes actual");

        // 2. Obtener usuario
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        if (usuarioLogueado.getRolId() == 1) { // ADMIN
            // CORRECCIÓN: Filtrar empleados por negocio
            List<Usuario> empleados = usuarioDAO.obtenerPorNegocio(usuarioLogueado.getNegocioId());
            if (empleados != null && !empleados.isEmpty()) {
                choiceEmpleado1.setItems(FXCollections.observableArrayList(empleados));
            }
        } else { // EMPLEADO
            // Ocultamos el selector de empleados y el label previo
            choiceEmpleado1.setVisible(false);
            choiceEmpleado1.setManaged(false);
            lblEmpleado1.setVisible(false);
            lblEmpleado1.setManaged(false);
            choiceEmpleado1.setValue(usuarioLogueado);
        }

        // 3. Asignar Eventos a los botones
        btnPDF1.setOnAction(e -> exportarInformeTurnos("PDF"));
        btnExcel1.setOnAction(e -> exportarInformeTurnos("EXCEL"));
    }

    /**
     * Método que configura el informe orientado a las incidencias registradas de un usuario
     */
    private void configurarInformeIncidencias() {
        // 1. Llenar Periodos y Estados
        choicePeriodo2.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo2.setValue("Mes actual");

        choiceEstado2.getItems().addAll("Todos", "Pendiente", "Validada", "Denegada");
        choiceEstado2.setValue("Todos");

        // 2. Obtener usuario
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        // 3. Asignar Eventos a los botones
        btnPDF2.setOnAction(e -> exportarIncidencias("PDF"));
        btnExcel2.setOnAction(e -> exportarIncidencias("EXCEL"));
    }

    /**
     * Método que configura el informe orientado al total de horas trabajadas de un usuario
     */
    private void configurarInformeHoras() {
        // 1. Llenar Periodos
        choicePeriodo3.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo3.setValue("Mes actual");

        // 2. Configurar cómo se ven los empleados en el ChoiceBox (Nombre y Apellido)
        choiceEmpleado3.setConverter(new javafx.util.StringConverter<Usuario>() {
            @Override
            public String toString(Usuario u) {
                return (u == null) ? "" : u.getNombre() + " " + u.getApellidos();
            }
            @Override
            public Usuario fromString(String string) { return null; }
        });

        // 3. Seguridad y Carga de datos
        Usuario usuarioLogueado = SesionUtil.getUsuario();

        if (usuarioLogueado.getRolId() == 1) { // ADMIN
            List<Usuario> empleados = usuarioDAO.obtenerPorNegocio(usuarioLogueado.getNegocioId());
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

    /**
     * Método que configura el informe completo con todos los datos de trabajo de un usuario
     */
    private void configurarInformeCompleto() {
        choicePeriodo4.getItems().addAll("Semana actual", "Mes actual", "Mes anterior");
        choicePeriodo4.setValue("Mes actual");

        btnPDF4.setOnAction(e -> exportarInformeCompleto("PDF"));
        btnExcel4.setOnAction(e -> exportarInformeCompleto("EXCEL"));
    }

    /**
     * Método que exporta el informe orientado a los turnos de un usuario en un formato determinado
     *
     * @param formato formato en el que queremos exportar
     */
    private void exportarInformeTurnos(String formato) {
        // Configuramos el guardado
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe de Turnos");
        String ext = formato.equals("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Turnos_" + choicePeriodo1.getValue().replace(" ", "_") + ext);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato, "*" + ext));

        File destino = fc.showSaveDialog(btnPDF1.getScene().getWindow());

        if (destino != null) {
            try {
                // Obtenemos el empleado
                Usuario usuarioLogueado = SesionUtil.getUsuario();
                Usuario emp;

                if (usuarioLogueado.getRolId() == 1) {
                    emp = choiceEmpleado1.getValue();
                } else {
                    emp = usuarioLogueado;
                }

                if (emp == null) return;

                // Obtenemos los datos del DAO
                List<Turno> lista = turnoDAO.obtenerTurnosPorFiltro(emp.getId(), choicePeriodo1.getValue());

                // Llamamos al exportador que corresponda
                if (formato.equals("PDF")) {
                    ExportadorPDF.generarInformeTurnos(destino, choicePeriodo1.getValue(), emp.getNombre(), lista);
                } else {
                    ExportadorExcel.generarExcelTurnos(destino, choicePeriodo1.getValue(), emp.getNombre(), lista);
                }

                // Inserción en la BD
                Exportacion exp = new Exportacion();
                exp.setTipoFormato(formato);
                exp.setFechaGeneracion(LocalDateTime.now());
                exp.setUsuarioId(SesionUtil.getUsuario().getId());
                exp.setNegocioId(SesionUtil.getUsuario().getNegocioId());

                exportacionDAO.insertar(exp);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Método que exporta el informe orientado a las incidencias de un usuario en un formato determinado
     *
     * @param formato formato en el que queremos exportar
     */
    private void exportarIncidencias(String formato) {
        // Configuramos el guardado
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe de Incidencias");
        String ext = formato.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Incidencias_" + choicePeriodo2.getValue().replace(" ", "_") + ext);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato, "*" + ext));

        File destino = fc.showSaveDialog(btnPDF2.getScene().getWindow());

        if (destino != null) {
            try {
                // Usamos el empleado del primer selector para ser consistentes.
                Usuario usuarioActual = SesionUtil.getUsuario();
                Usuario emp;

                if (usuarioActual.getRolId() == 1) {
                    emp = choiceEmpleado1.getValue();
                } else {
                    emp = usuarioActual;
                }

                if (emp == null) return;

                // Consultamos los datos del DAO
                List<Incidencia> lista = incidenciaDAO.obtenerIncidenciasPorFiltro(
                        emp.getId(),
                        choicePeriodo2.getValue(),
                        choiceEstado2.getValue()
                );

                // Generamos según el formato
                if (formato.equalsIgnoreCase("PDF")) {
                    ExportadorPDF.generarInformeIncidencias(destino, choicePeriodo2.getValue(), emp.getNombre(), lista);
                } else {
                    ExportadorExcel.generarExcelIncidencias(destino, choicePeriodo2.getValue(), emp.getNombre(), lista);
                }

                // Inserción en la BD (quien genera el reporte)
                Exportacion reg = new Exportacion();
                reg.setTipoFormato(formato);
                reg.setFechaGeneracion(LocalDateTime.now());
                reg.setUsuarioId(SesionUtil.getUsuario().getId());
                reg.setNegocioId(SesionUtil.getUsuario().getNegocioId());
                exportacionDAO.insertar(reg);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Método que exporta el informe orientado al total de horas trabajadas de un usuario en un formato determinado
     *
     * @param formato formato en el que queremos exportar
     */
    private void exportarInformeHoras(String formato) {
        // Configuramos el guardado
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe de Horas");
        String ext = formato.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Horas_" + choicePeriodo3.getValue().replace(" ", "_") + ext);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato, "*" + ext));

        File destino = fc.showSaveDialog(btnPDF3.getScene().getWindow());

        if (destino != null) {
            try {
                Usuario usuarioSesion = SesionUtil.getUsuario();
                Usuario emp;

                if (usuarioSesion.getRolId() == 1) {
                    emp = choiceEmpleado3.getValue();
                } else {
                    emp = usuarioSesion;
                }

                if (emp == null) return;

                List<Turno> datos = turnoDAO.obtenerResumenHoras(emp.getId(), choicePeriodo3.getValue());

                if (formato.equalsIgnoreCase("PDF")) {
                    ExportadorPDF.generarInformeHoras(destino, choicePeriodo3.getValue(), emp.getNombre(), datos);
                } else {
                    ExportadorExcel.generarExcelHoras(destino, choicePeriodo3.getValue(), emp.getNombre(), datos);
                }

                Exportacion reg = new Exportacion();
                reg.setTipoFormato(formato);
                reg.setFechaGeneracion(LocalDateTime.now());
                reg.setUsuarioId(SesionUtil.getUsuario().getId());
                reg.setNegocioId(SesionUtil.getUsuario().getNegocioId());

                exportacionDAO.insertar(reg);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Método que exporta el informe completo de un usuario en un formato determinado
     *
     * @param formato formato en el que queremos exportar
     */
    private void exportarInformeCompleto(String formato) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Informe Completo");
        String ext = formato.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        fc.setInitialFileName("Informe_Completo_" + choicePeriodo4.getValue().replace(" ", "_") + ext);

        File destino = fc.showSaveDialog(btnPDF4.getScene().getWindow());

        if (destino != null) {
            try {
                Usuario usuarioSesion = SesionUtil.getUsuario();
                Usuario emp;

                if (usuarioSesion.getRolId() == 1) {
                    emp = choiceEmpleado1.getValue();
                } else {
                    emp = usuarioSesion;
                }

                if (emp == null) return;

                String periodo = choicePeriodo4.getValue();

                // Recopilamos los datos del empleado seleccionado
                List<Turno> turnos = turnoDAO.obtenerTurnosPorFiltro(emp.getId(), periodo);
                List<Incidencia> incidencias = incidenciaDAO.obtenerIncidenciasPorFiltro(emp.getId(), periodo, "Todos");

                // Exportamos según el formato
                if (formato.equalsIgnoreCase("PDF")) {
                    ExportadorPDF.generarInformeCompleto(destino, periodo, emp.getNombre(), turnos, incidencias);
                } else {
                    ExportadorExcel.generarExcelCompleto(destino, periodo, emp.getNombre(), turnos, incidencias);
                }

                // Inserción en la BD
                Exportacion reg = new Exportacion();
                reg.setTipoFormato(formato + "_COMPLETO");
                reg.setFechaGeneracion(LocalDateTime.now());
                reg.setUsuarioId(SesionUtil.getUsuario().getId());
                reg.setNegocioId(SesionUtil.getUsuario().getNegocioId());
                exportacionDAO.insertar(reg);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}