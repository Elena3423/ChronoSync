package org.chronosync.proyecto.util;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.chronosync.proyecto.modelo.Incidencia;
import org.chronosync.proyecto.modelo.Turno;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorPDF {

    // Definimos el color azul de la app y los formatos de fecha y hora
    private static final DeviceRgb AZUL_CHRONO = new DeviceRgb(58, 123, 213);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Método que crea un PDF con la lista de turnos (entrada y salida)
     *
     * @param destino
     * @param periodo
     * @param nombreEmpleado
     * @param turnos
     * @throws Exception
     */
    public static void generarInformeTurnos(File destino, String periodo, String nombreEmpleado, List<Turno> turnos) throws Exception {
        // Preparamos el archivo
        PdfWriter writer = new PdfWriter(destino);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        // Ponemos el título principal azul
        documento.add(new Paragraph("INFORME DE ASISTENCIA Y TURNOS")
                .setFontColor(AZUL_CHRONO)
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER));

        // Datos del empleado y el tiempo que abarca el informe
        documento.add(new Paragraph("Empleado: " + nombreEmpleado));
        documento.add(new Paragraph("Periodo: " + periodo));
        documento.add(new Paragraph("\n"));

        // Preparamos una tabla de 4 columnas con un 25% de espacio cada una
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();

        // Ponemos los títulos de la tabla
        String[] cabeceras = {"Fecha", "Entrada", "Salida", "Tipo"};
        for (String h : cabeceras) {
            tabla.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(DeviceRgb.WHITE))
                    .setBackgroundColor(AZUL_CHRONO));
        }

        // Metemos los datos de cada turno en la tabla
        if (turnos == null || turnos.isEmpty()) {
            tabla.addCell(new Cell(1, 4).add(new Paragraph("No hay turnos registrados en este periodo.")));
        } else {
            for (Turno t : turnos) {
                // Si falla algún dato ponemos guiones
                String fecha = (t.getFechaInicio() != null) ? t.getFechaInicio().format(DATE_FMT) : "---";
                String entrada = (t.getFechaInicio() != null) ? t.getFechaInicio().format(TIME_FMT) : "---";
                String salida = (t.getFechaFin() != null) ? t.getFechaFin().format(TIME_FMT) : "---";
                String tipo = (t.getTipo() != null) ? t.getTipo() : "N/A";

                tabla.addCell(new Cell().add(new Paragraph(fecha)));
                tabla.addCell(new Cell().add(new Paragraph(entrada)));
                tabla.addCell(new Cell().add(new Paragraph(salida)));
                tabla.addCell(new Cell().add(new Paragraph(tipo)));
            }
        }

        documento.add(tabla);
        // Cerramos el documento para que se guarde el archivo
        documento.close();
    }

    /**
     * Genera un informe especial solo para las incidencias
     *
     * @param destino
     * @param periodo
     * @param nombreEmpleado
     * @param lista
     * @throws Exception
     */
    public static void generarInformeIncidencias(File destino, String periodo, String nombreEmpleado, List<Incidencia> lista) throws Exception {
        PdfWriter writer = new PdfWriter(destino);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        documento.add(new Paragraph("INFORME DE INCIDENCIAS")
                .setFontSize(20)
                .setFontColor(AZUL_CHRONO)
                .setTextAlignment(TextAlignment.CENTER));

        documento.add(new Paragraph("Empleado: " + nombreEmpleado));
        documento.add(new Paragraph("Periodo: " + periodo));
        documento.add(new Paragraph("\n"));

        // Aquí la tabla tiene una columna más ancha para los comentarios
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{15, 20, 15, 50})).useAllAvailableWidth();

        String[] cabeceras = {"ID Turno", "Tipo", "Estado", "Comentarios"};
        for (String h : cabeceras) {
            tabla.addHeaderCell(new Cell().add(new Paragraph(h).setFontColor(DeviceRgb.WHITE))
                    .setBackgroundColor(AZUL_CHRONO));
        }

        if (lista.isEmpty()) {
            tabla.addCell(new Cell(1, 4).add(new Paragraph("No se encontraron incidencias.")));
        } else {
            for (Incidencia i : lista) {
                tabla.addCell(new Cell().add(new Paragraph(String.valueOf(i.getTurnoId()))));
                tabla.addCell(new Cell().add(new Paragraph(i.getTipo())));
                tabla.addCell(new Cell().add(new Paragraph(i.getEstado())));
                tabla.addCell(new Cell().add(new Paragraph(i.getComentarios() != null ? i.getComentarios() : "")));
            }
        }

        documento.add(tabla);
        documento.close();
    }

    /**
     * Método que crea un informe con el timepo total trabajado
     *
     * @param destino
     * @param periodo
     * @param nombreEmpleado
     * @param turnos
     * @throws Exception
     */
    public static void generarInformeHoras(File destino, String periodo, String nombreEmpleado, List<Turno> turnos) throws Exception {
        PdfWriter writer = new PdfWriter(destino);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        documento.add(new Paragraph("INFORME DE HORAS TRABAJADAS")
                .setFontSize(20).setFontColor(AZUL_CHRONO).setTextAlignment(TextAlignment.CENTER));
        documento.add(new Paragraph("Empleado: " + nombreEmpleado));
        documento.add(new Paragraph("Periodo: " + periodo + "\n\n"));

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();
        String[] cabeceras = {"Fecha", "Entrada", "Salida", "Total Tiempo"};

        for (String h : cabeceras) {
            tabla.addHeaderCell(new Cell().add(new Paragraph(h).setFontColor(DeviceRgb.WHITE)).setBackgroundColor(AZUL_CHRONO));
        }

        for (Turno t : turnos) {
            if (t.getFechaFin() != null) {
                tabla.addCell(t.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                tabla.addCell(t.getFechaInicio().format(DateTimeFormatter.ofPattern("HH:mm")));
                tabla.addCell(t.getFechaFin().format(DateTimeFormatter.ofPattern("HH:mm")));

                // Calculamos cuanto tiempo ha pasado entre la entrada y la salida
                java.time.Duration d = java.time.Duration.between(t.getFechaInicio(), t.getFechaFin());
                tabla.addCell(String.format("%02d:%02d h", d.toHours(), d.toMinutesPart()));
            }
        }

        documento.add(tabla);
        documento.close();
    }

    /**
     * Método que crea un informe que junta los turnos y las incidencias
     *
     * @param destino
     * @param periodo
     * @param nombre
     * @param turnos
     * @param incidencias
     * @throws Exception
     */
    public static void generarInformeCompleto(File destino, String periodo, String nombre, List<Turno> turnos, List<Incidencia> incidencias) throws Exception {
        PdfWriter writer = new PdfWriter(destino);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        documento.add(new Paragraph("INFORME CONSOLIDADO DE ACTIVIDAD")
                .setFontSize(22)
                .setFontColor(AZUL_CHRONO)
                .setTextAlignment(TextAlignment.CENTER));

        documento.add(new Paragraph("Empleado: " + nombre + " | Periodo: " + periodo)
                .setFontSize(12)
                .setMarginBottom(20));

        // PARTE 1: TURNOS
        documento.add(new Paragraph("1. REGISTRO DE TURNOS")
                .setFontSize(16)
                .setFontColor(AZUL_CHRONO));

        Table tTurnos = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 20, 20})).useAllAvailableWidth();
        String[] hT = {"Fecha", "Entrada", "Salida", "Tipo", "Estado"};

        for (String h : hT) {
            tTurnos.addHeaderCell(new Cell().add(new Paragraph(h).setFontColor(DeviceRgb.WHITE))
                    .setBackgroundColor(AZUL_CHRONO));
        }

        if (turnos.isEmpty()) {
            tTurnos.addCell(new Cell(1, 5).add(new Paragraph("No hay turnos registrados.")));
        } else {
            for (Turno t : turnos) {
                tTurnos.addCell(new Cell().add(new Paragraph(t.getFechaInicio().toLocalDate().toString())));
                tTurnos.addCell(new Cell().add(new Paragraph(t.getFechaInicio().toLocalTime().toString())));
                tTurnos.addCell(new Cell().add(new Paragraph(t.getFechaFin() != null ? t.getFechaFin().toLocalTime().toString() : "--:--")));
                tTurnos.addCell(new Cell().add(new Paragraph(t.getTipo() != null ? t.getTipo() : "")));
                tTurnos.addCell(new Cell().add(new Paragraph(t.getEstado() != null ? t.getEstado() : "")));
            }
        }
        documento.add(tTurnos.setMarginBottom(30));

        // PARTE 2: INCIDENCIAS
        documento.add(new Paragraph("2. REGISTRO DE INCIDENCIAS")
                .setFontSize(16)
                .setFontColor(AZUL_CHRONO));

        Table tInc = new Table(UnitValue.createPercentArray(new float[]{15, 20, 15, 50})).useAllAvailableWidth();
        String[] hI = {"ID Turno", "Tipo", "Estado", "Comentarios"};

        for(String h : hI) {
            tInc.addHeaderCell(new Cell().add(new Paragraph(h).setFontColor(DeviceRgb.WHITE))
                    .setBackgroundColor(AZUL_CHRONO));
        }

        if (incidencias.isEmpty()) {
            tInc.addCell(new Cell(1, 4).add(new Paragraph("No hay incidencias registradas.")));
        } else {
            for (Incidencia in : incidencias) {
                tInc.addCell(new Cell().add(new Paragraph(String.valueOf(in.getTurnoId()))));
                tInc.addCell(new Cell().add(new Paragraph(in.getTipo() != null ? in.getTipo() : "")));
                tInc.addCell(new Cell().add(new Paragraph(in.getEstado() != null ? in.getEstado() : "")));
                tInc.addCell(new Cell().add(new Paragraph(in.getComentarios() != null ? in.getComentarios() : "")));
            }
        }

        documento.add(tInc);
        documento.close();
    }
}