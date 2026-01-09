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
import org.chronosync.proyecto.modelo.Turno;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorPDF {

    private static final DeviceRgb AZUL_CHRONO = new DeviceRgb(58, 123, 213);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static void generarInformeTurnos(File destino, String periodo, String nombreEmpleado, List<Turno> turnos) throws Exception {
        PdfWriter writer = new PdfWriter(destino);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        // 1. Título (Sin .setBold())
        documento.add(new Paragraph("INFORME DE ASISTENCIA Y TURNOS")
                .setFontColor(AZUL_CHRONO)
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER));

        // 2. Información de cabecera
        documento.add(new Paragraph("Empleado: " + nombreEmpleado));
        documento.add(new Paragraph("Periodo: " + periodo));
        documento.add(new Paragraph("\n"));

        // 3. Tabla de datos
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();

        // Estilo de la cabecera (Azul con texto blanco)
        String[] cabeceras = {"Fecha", "Entrada", "Salida", "Tipo"};
        for (String h : cabeceras) {
            tabla.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(DeviceRgb.WHITE))
                    .setBackgroundColor(AZUL_CHRONO));
        }

        // 4. Llenar la tabla con tus métodos: getFechaInicio() y getFechaFin()
        if (turnos == null || turnos.isEmpty()) {
            tabla.addCell(new Cell(1, 4).add(new Paragraph("No hay turnos registrados en este periodo.")));
        } else {
            for (Turno t : turnos) {
                // Verificamos nulos antes de formatear
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
        documento.close();
    }
}