package org.chronosync.proyecto.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.chronosync.proyecto.modelo.Incidencia;
import org.chronosync.proyecto.modelo.Turno;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorExcel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static void generarExcelTurnos(File destino, String periodo, String nombreEmpleado, List<Turno> turnos) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

            Sheet sheet = workbook.createSheet("Informe de Turnos");

            // 1. Estilo para la cabecera (Azul)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Fuente blanca para la cabecera
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            // 2. Crear encabezados
            String[] columnas = {"Fecha", "Hora Entrada", "Hora Salida", "Tipo", "Estado"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. Llenar datos reales
            int rowIdx = 1;
            if (turnos != null) {
                for (Turno t : turnos) {
                    Row row = sheet.createRow(rowIdx++);

                    // Fecha (usando getFechaInicio)
                    row.createCell(0).setCellValue(t.getFechaInicio() != null ? t.getFechaInicio().format(DATE_FMT) : "---");

                    // Horas
                    row.createCell(1).setCellValue(t.getFechaInicio() != null ? t.getFechaInicio().format(TIME_FMT) : "---");
                    row.createCell(2).setCellValue(t.getFechaFin() != null ? t.getFechaFin().format(TIME_FMT) : "---");

                    // Otros datos
                    row.createCell(3).setCellValue(t.getTipo() != null ? t.getTipo() : "N/A");
                    row.createCell(4).setCellValue(t.getEstado() != null ? t.getEstado() : "N/A");
                }
            }

            // 4. Auto-ajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 5. Guardar el archivo
            try (FileOutputStream fileOut = new FileOutputStream(destino)) {
                workbook.write(fileOut);
            }
        }
    }

    public static void generarExcelIncidencias(File destino, String periodo, String nombreEmpleado, List<Incidencia> lista) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Silenciamos el log de POI igual que en turnos
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

            Sheet sheet = workbook.createSheet("Informe de Incidencias");

            // 1. Estilo para la cabecera (Azul Cornflower)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Fuente blanca y negrita para la cabecera
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 2. Crear encabezados basados en tu clase Incidencia
            String[] columnas = {"ID Turno", "Tipo", "Estado", "Comentarios"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. Llenar datos reales
            int rowIdx = 1;
            if (lista != null) {
                for (Incidencia i : lista) {
                    Row row = sheet.createRow(rowIdx++);

                    // ID Turno (numérico)
                    row.createCell(0).setCellValue(i.getTurnoId() != null ? i.getTurnoId() : 0);

                    // Tipo
                    row.createCell(1).setCellValue(i.getTipo() != null ? i.getTipo() : "N/A");

                    // Estado
                    row.createCell(2).setCellValue(i.getEstado() != null ? i.getEstado() : "N/A");

                    // Comentarios (pueden ser largos)
                    row.createCell(3).setCellValue(i.getComentarios() != null ? i.getComentarios() : "Sin comentarios");
                }
            }

            // 4. Auto-ajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 5. Guardar el archivo
            try (FileOutputStream fileOut = new FileOutputStream(destino)) {
                workbook.write(fileOut);
            }
        }
    }

    public static void generarExcelHoras(File destino, String periodo, String nombreEmpleado, List<Turno> lista) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");
            Sheet sheet = workbook.createSheet("Resumen de Horas");

            // Estilo cabecera (Azul)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            String[] columnas = {"Fecha", "Entrada", "Salida", "Total Horas", "Tipo"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Turno t : lista) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(1).setCellValue(t.getFechaInicio().format(DateTimeFormatter.ofPattern("HH:mm")));
                row.createCell(2).setCellValue(t.getFechaFin().format(DateTimeFormatter.ofPattern("HH:mm")));

                // Cálculo de duración
                Duration duracion = Duration.between(t.getFechaInicio(), t.getFechaFin());
                long horas = duracion.toHours();
                long minutos = duracion.toMinutesPart();
                row.createCell(3).setCellValue(String.format("%02d:%02d", horas, minutos));

                row.createCell(4).setCellValue(t.getTipo());
            }

            for (int i = 0; i < columnas.length; i++) sheet.autoSizeColumn(i);
            try (FileOutputStream fileOut = new FileOutputStream(destino)) { workbook.write(fileOut); }
        }
    }

    public static void generarExcelCompleto(File destino, String periodo, String nombre, List<Turno> turnos, List<Incidencia> incidencias) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

            // Estilo común para cabeceras
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            // --- HOJA 1: TURNOS Y HORAS ---
            Sheet sheetTurnos = workbook.createSheet("Turnos");
            String[] colTurnos = {"Fecha", "Entrada", "Salida", "Tipo", "Estado"};
            Row rowH1 = sheetTurnos.createRow(0);
            for(int i=0; i<colTurnos.length; i++) {
                Cell c = rowH1.createCell(i); c.setCellValue(colTurnos[i]); c.setCellStyle(headerStyle);
            }
            int r1 = 1;
            for (Turno t : turnos) {
                Row row = sheetTurnos.createRow(r1++);
                row.createCell(0).setCellValue(t.getFechaInicio().toLocalDate().toString());
                row.createCell(1).setCellValue(t.getFechaInicio().toLocalTime().toString());
                row.createCell(2).setCellValue(t.getFechaFin() != null ? t.getFechaFin().toLocalTime().toString() : "--:--");
                row.createCell(3).setCellValue(t.getTipo());
                row.createCell(4).setCellValue(t.getEstado());
            }

            // --- HOJA 2: INCIDENCIAS ---
            Sheet sheetInc = workbook.createSheet("Incidencias");
            String[] colInc = {"ID Turno", "Tipo", "Estado", "Comentarios"};
            Row rowH2 = sheetInc.createRow(0);
            for(int i=0; i<colInc.length; i++) {
                Cell c = rowH2.createCell(i); c.setCellValue(colInc[i]); c.setCellStyle(headerStyle);
            }
            int r2 = 1;
            for (Incidencia in : incidencias) {
                Row row = sheetInc.createRow(r2++);
                row.createCell(0).setCellValue(in.getTurnoId());
                row.createCell(1).setCellValue(in.getTipo());
                row.createCell(2).setCellValue(in.getEstado());
                row.createCell(3).setCellValue(in.getComentarios());
            }

            for (int i=0; i<5; i++) { sheetTurnos.autoSizeColumn(i); sheetInc.autoSizeColumn(i); }

            try (FileOutputStream out = new FileOutputStream(destino)) { workbook.write(out); }
        }
    }
}