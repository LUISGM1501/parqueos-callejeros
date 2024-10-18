package com.parqueos.servicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.reportes.Reporte;
import com.parqueos.reportes.ReporteFactory;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar los reportes
public class GestorReportes {
    // Archivo de reportes
    private static final String ARCHIVO_REPORTES = "reportes.json";
    private List<Reporte> reportes;

    // Constructor para cargar los reportes
    public GestorReportes() {
        cargarReportes();
    }

    // Metodo para cargar los reportes
    public void cargarReportes() {
        // Cargar los reportes del archivo json
        reportes = GestorArchivos.cargarTodosLosElementos(ARCHIVO_REPORTES, Reporte.class);
    }

    // Metodo para generar un reporte de ingresos
    public Reporte generarReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, List<Reserva> reservas) {
        // Crear un reporte de ingresos
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.INGRESOS, fechaInicio, fechaFin, null, reservas, null, null);
        // Guardar el reporte
        guardarReporte(reporte);
        // Retornar el reporte
        return reporte;
    }

    // Metodo para generar un reporte de multas
    public Reporte generarReporteMultas(LocalDate fechaInicio, LocalDate fechaFin, List<Multa> multas) {
        // Crear un reporte de multas
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.MULTAS, fechaInicio, fechaFin, null, null, null, multas);
        // Guardar el reporte
        guardarReporte(reporte);
        // Retornar el reporte
        return reporte;
    }

    // Metodo para generar un reporte de espacios
    public Reporte generarReporteEspacios(List<EspacioParqueo> espacios) {
        // Crear un reporte de espacios
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.ESPACIOS, LocalDate.now(), LocalDate.now(), espacios, null, null, null);
        // Guardar el reporte
        guardarReporte(reporte);
        // Retornar el reporte
        return reporte;
    }

    // Metodo para generar un reporte de historial
    public Reporte generarReporteHistorial(LocalDate fechaInicio, LocalDate fechaFin, List<Reserva> reservas) {
        // Crear un reporte de historial
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.HISTORIAL, fechaInicio, fechaFin, null, reservas, null, null);
        // Guardar el reporte
        guardarReporte(reporte);
        // Retornar el reporte
        return reporte;
    }

    // Metodo para generar un reporte de estadisticas
    public Reporte generarReporteEstadisticas(LocalDate fechaInicio, LocalDate fechaFin, List<EspacioParqueo> espacios, List<Reserva> reservas) {
        // Crear un reporte de estadisticas
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.ESTADISTICAS, fechaInicio, fechaFin, espacios, reservas, null, null);
        // Guardar el reporte
        guardarReporte(reporte);
        // Retornar el reporte
        return reporte;
    }

    // Metodo para guardar un reporte
    private void guardarReporte(Reporte reporte) {
        // Agregar el reporte a la lista de reportes
        reportes.add(reporte);
        // Guardar los reportes en el archivo json
        GestorArchivos.guardarTodo(reportes, ARCHIVO_REPORTES);
    }

    // Metodo para obtener los reportes entre dos fechas
    public List<Reporte> obtenerReportesEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear una lista para almacenar los reportes filtrados
        List<Reporte> reportesFiltrados = new ArrayList<>();

        // Recorrer la lista de reportes
        for (Reporte reporte : reportes) {
            // Generar el contenido del reporte
            String contenido = reporte.generarReporte(fechaInicio, fechaFin);
            // Verificar si el contenido del reporte esta entre dos fechas
            if (contenidoEstaEntreFechas(contenido, fechaInicio, fechaFin)) {
                // Agregar el reporte a la lista de reportes filtrados
                reportesFiltrados.add(reporte);
            }
        }
        // Retornar la lista de reportes filtrados
        return reportesFiltrados;
    }

    // Metodo para verificar si el contenido del reporte esta entre dos fechas
    private boolean contenidoEstaEntreFechas(String contenido, LocalDate fechaInicio, LocalDate fechaFin) {
        // Verificar si el contenido del reporte contiene las fechas
        return contenido.contains(fechaInicio.toString()) && contenido.contains(fechaFin.toString());
    }

    // Getter para obtener los reportes
    public List<Reporte> getReportes() {
        return reportes;
    }
}