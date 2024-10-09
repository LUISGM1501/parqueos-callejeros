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

public class GestorReportes {
    private static final String ARCHIVO_REPORTES = "reportes.json";
    private List<Reporte> reportes;

    public GestorReportes() {
        cargarReportes();
    }

    public void cargarReportes() {
        reportes = GestorArchivos.cargarTodosLosElementos(ARCHIVO_REPORTES, Reporte.class);
    }

    public Reporte generarReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, List<Reserva> reservas) {
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.INGRESOS, fechaInicio, fechaFin, null, reservas, null, null);
        guardarReporte(reporte);
        return reporte;
    }

    public Reporte generarReporteMultas(LocalDate fechaInicio, LocalDate fechaFin, List<Multa> multas) {
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.MULTAS, fechaInicio, fechaFin, null, null, null, multas);
        guardarReporte(reporte);
        return reporte;
    }

    public Reporte generarReporteEspacios(List<EspacioParqueo> espacios) {
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.ESPACIOS, LocalDate.now(), LocalDate.now(), espacios, null, null, null);
        guardarReporte(reporte);
        return reporte;
    }

    public Reporte generarReporteHistorial(LocalDate fechaInicio, LocalDate fechaFin, List<Reserva> reservas) {
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.HISTORIAL, fechaInicio, fechaFin, null, reservas, null, null);
        guardarReporte(reporte);
        return reporte;
    }

    public Reporte generarReporteEstadisticas(LocalDate fechaInicio, LocalDate fechaFin, List<EspacioParqueo> espacios, List<Reserva> reservas) {
        Reporte reporte = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.ESTADISTICAS, fechaInicio, fechaFin, espacios, reservas, null, null);
        guardarReporte(reporte);
        return reporte;
    }

    private void guardarReporte(Reporte reporte) {
        reportes.add(reporte);
        GestorArchivos.guardarTodo(reportes, ARCHIVO_REPORTES);
    }

    public List<Reporte> obtenerReportesEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reporte> reportesFiltrados = new ArrayList<>();
        for (Reporte reporte : reportes) {
            // Aquí asumimos que el reporte tiene información sobre las fechas en su contenido
            String contenido = reporte.generarReporte(fechaInicio, fechaFin);
            if (contenidoEstaEntreFechas(contenido, fechaInicio, fechaFin)) {
                reportesFiltrados.add(reporte);
            }
        }
        return reportesFiltrados;
    }

    private boolean contenidoEstaEntreFechas(String contenido, LocalDate fechaInicio, LocalDate fechaFin) {
        // Esta es una implementación simplificada. Deberías ajustar esto según el formato real de tus reportes.
        return contenido.contains(fechaInicio.toString()) && contenido.contains(fechaFin.toString());
    }

    public List<Reporte> getReportes() {
        return reportes;
    }
}