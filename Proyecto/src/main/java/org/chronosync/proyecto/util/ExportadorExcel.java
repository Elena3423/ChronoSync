package org.chronosync.proyecto.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.chronosync.proyecto.modelo.Turno;

import java.io.File;
import java.io.FileOutputStream;
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
}