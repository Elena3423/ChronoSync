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

    // Formatos para que las fechas y horas se vean como deseamos
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Método que crea un Excel simple con el listado de turnos de un empleado
     *
     * @param destino
     * @param periodo
     * @param nombreEmpleado
     * @param turnos
     * @throws Exception
     */
    public static void generarExcelTurnos(File destino, String periodo, String nombreEmpleado, List<Turno> turnos) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Quitamos los mensajes internos de la librería para que la consola esté limpia
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

            Sheet sheet = workbook.createSheet("Informe de Turnos");

            // Creamos un estilo azul con letras blancas para la cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            // Cabecera
            String[] columnas = {"Fecha", "Hora Entrada", "Hora Salida", "Tipo", "Estado"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = 1;
            if (turnos != null) {
                for (Turno t : turnos) {
                    Row row = sheet.createRow(rowIdx++);
                    // Si el dato existe lo ponemos, si no ponemos guiones para rellenar
                    row.createCell(0).setCellValue(t.getFechaInicio() != null ? t.getFechaInicio().format(DATE_FMT) : "---");
                    row.createCell(1).setCellValue(t.getFechaInicio() != null ? t.getFechaInicio().format(TIME_FMT) : "---");
                    row.createCell(2).setCellValue(t.getFechaFin() != null ? t.getFechaFin().format(TIME_FMT) : "---");
                    row.createCell(3).setCellValue(t.getTipo() != null ? t.getTipo() : "N/A");
                    row.createCell(4).setCellValue(t.getEstado() != null ? t.getEstado() : "N/A");
                }
            }

            // Ajustamos el ancho de las columnas según el contenido
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardamos el resultado en el archivo que nos han pedido
            try (FileOutputStream fileOut = new FileOutputStream(destino)) {
                workbook.write(fileOut);
            }
        }
    }

    /**
     * Método que genera un informe específico para las incidencias
     *
     * @param destino
     * @param periodo
     * @param nombreEmpleado
     * @param lista
     * @throws Exception
     */
    public static void generarExcelIncidencias(File destino, String periodo, String nombreEmpleado, List<Incidencia> lista) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Quitamos los mensajes internos de la librería para que la consola esté limpia
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");
            Sheet sheet = workbook.createSheet("Informe de Incidencias");

            // Creamos un estilo azul con letras blancas para la cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Creamos los encabezados según la clase Incidencia
            String[] columnas = {"ID Turno", "Tipo", "Estado", "Comentarios"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = 1;
            if (lista != null) {
                for (Incidencia i : lista) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(i.getTurnoId() != null ? i.getTurnoId() : 0);
                    row.createCell(1).setCellValue(i.getTipo() != null ? i.getTipo() : "N/A");
                    row.createCell(2).setCellValue(i.getEstado() != null ? i.getEstado() : "N/A");
                    row.createCell(3).setCellValue(i.getComentarios() != null ? i.getComentarios() : "Sin comentarios");
                }
            }

            // Ajustamos el ancho de las columnas según el contenido
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardamos el resultado en el archivo que nos han pedido
            try (FileOutputStream fileOut = new FileOutputStream(destino)) {
                workbook.write(fileOut);
            }
        }
    }

    /**
     * Método que genera un informe que calcula automáticamente el total de hroas trabajadas
     *
     * @param destino
     * @param periodo
     * @param nombreEmpleado
     * @param lista
     * @throws Exception
     */
    public static void generarExcelHoras(File destino, String periodo, String nombreEmpleado, List<Turno> lista) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Quitamos los mensajes internos de la librería para que la consola esté limpia
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");
            Sheet sheet = workbook.createSheet("Resumen de Horas");

            // Creamos un estilo azul con letras blancas para la cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            // Creamos los encabezados según la clase Turno
            String[] columnas = {"Fecha", "Entrada", "Salida", "Total Horas", "Tipo"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = 1;
            for (Turno t : lista) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(1).setCellValue(t.getFechaInicio().format(DateTimeFormatter.ofPattern("HH:mm")));
                row.createCell(2).setCellValue(t.getFechaFin().format(DateTimeFormatter.ofPattern("HH:mm")));

                // Calculamos la diferencia de tiempo entre la entrada y la saldia
                Duration duracion = Duration.between(t.getFechaInicio(), t.getFechaFin());
                long horas = duracion.toHours();
                long minutos = duracion.toMinutesPart();
                // Formateamos la hora para que sea comprensible
                row.createCell(3).setCellValue(String.format("%02d:%02d", horas, minutos));

                row.createCell(4).setCellValue(t.getTipo());
            }

            // Ajustamos el ancho de las columnas según el contenido
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardamos el resultado en el archivo que nos han pedido
            try (FileOutputStream fileOut = new FileOutputStream(destino)) {
                workbook.write(fileOut);
            }
        }
    }

    /**
     * Método que genera un solo informe con dos pestañas (una para Turnos y otro para Incidencias)
     *
     * @param destino
     * @param periodo
     * @param nombre
     * @param turnos
     * @param incidencias
     * @throws Exception
     */
    public static void generarExcelCompleto(File destino, String periodo, String nombre, List<Turno> turnos, List<Incidencia> incidencias) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Quitamos los mensajes internos de la librería para que la consola esté limpia
            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

            // Creamos un estilo azul con letras blancas para la cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            // HOJA 1: Turnos
            // Creamos los encabezados según la clase Turno
            Sheet sheetTurnos = workbook.createSheet("Turnos");
            String[] colTurnos = {"Fecha", "Entrada", "Salida", "Tipo", "Estado"};
            Row rowH1 = sheetTurnos.createRow(0);
            for (int i=0; i<colTurnos.length; i++) {
                Cell c = rowH1.createCell(i); c.setCellValue(colTurnos[i]); c.setCellStyle(headerStyle);
            }

            // Datos
            int r1 = 1;
            for (Turno t : turnos) {
                Row row = sheetTurnos.createRow(r1++);
                row.createCell(0).setCellValue(t.getFechaInicio().toLocalDate().toString());
                row.createCell(1).setCellValue(t.getFechaInicio().toLocalTime().toString());
                row.createCell(2).setCellValue(t.getFechaFin() != null ? t.getFechaFin().toLocalTime().toString() : "--:--");
                row.createCell(3).setCellValue(t.getTipo());
                row.createCell(4).setCellValue(t.getEstado());
            }

            // HOJA 1: Incidencias
            Sheet sheetInc = workbook.createSheet("Incidencias");
            String[] colInc = {"ID Turno", "Tipo", "Estado", "Comentarios"};
            Row rowH2 = sheetInc.createRow(0);
            for(int i=0; i<colInc.length; i++) {
                Cell c = rowH2.createCell(i); c.setCellValue(colInc[i]); c.setCellStyle(headerStyle);
            }

            // Datos
            int r2 = 1;
            for (Incidencia in : incidencias) {
                Row row = sheetInc.createRow(r2++);
                row.createCell(0).setCellValue(in.getTurnoId());
                row.createCell(1).setCellValue(in.getTipo());
                row.createCell(2).setCellValue(in.getEstado());
                row.createCell(3).setCellValue(in.getComentarios());
            }

            // Ajustamos el ancho de las columnas según el contenido
            for (int i=0; i<5; i++) {
                sheetTurnos.autoSizeColumn(i);
                sheetInc.autoSizeColumn(i);
            }

            // Guardamos el resultado en el archivo que nos han pedido
            try (FileOutputStream fileOut = new FileOutputStream(destino)) {
                workbook.write(fileOut);
            }
        }
    }
}