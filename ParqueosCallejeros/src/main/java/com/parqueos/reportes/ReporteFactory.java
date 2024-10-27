package com.parqueos.reportes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;

public class ReporteFactory {

    // Metodo Factory para crear un reporte 
    public static Reporte crearReporte(TipoReporte tipo, LocalDate fechaInicio, LocalDate fechaFin, 
                                       List<EspacioParqueo> espacios, List<Reserva> reservas, 
                                       List<Double> ingresos, List<Multa> multas) {
        return switch (tipo) {
            case INGRESOS -> {
                List<Double> ingresosPorDia = calcularIngresosPorDia(reservas, fechaInicio, fechaFin);
                yield new ReporteIngresos(fechaInicio, fechaFin, ingresosPorDia);
            }
            case MULTAS -> new ReporteMultas(fechaInicio, fechaFin, multas);
            case ESPACIOS -> new ReporteEspacios(espacios);
            case HISTORIAL -> new ReporteHistorial(fechaInicio, fechaFin, reservas);
            case ESTADISTICAS -> new ReporteEstadisticas(fechaInicio, fechaFin, espacios, reservas);
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipo);
        };
    }

    // Metodo para calcular los ingresos por dia
    private static List<Double> calcularIngresosPorDia(List<Reserva> reservas, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Double> ingresosPorDia = new ArrayList<>();
        if (reservas == null || reservas.isEmpty()) {
            return ingresosPorDia;
        }

        LocalDate fecha = fechaInicio;
        while (!fecha.isAfter(fechaFin)) {
            final LocalDate fechaActual = fecha;
            double ingresoDia = reservas.stream()
                .filter(r -> r.getHoraInicio().toLocalDate().equals(fechaActual))
                .mapToDouble(r -> {
                    long minutos = ChronoUnit.MINUTES.between(r.getHoraInicio(), r.getHoraFin());
                    return (minutos / 60.0) * ConfiguracionParqueo.obtenerInstancia().getPrecioHora();
                })
                .sum();
            
            ingresosPorDia.add(ingresoDia);
            fecha = fecha.plusDays(1);
        }
        
        return ingresosPorDia;
    }

    // Enum para los tipos de reportes
    public enum TipoReporte {
        INGRESOS, MULTAS, ESPACIOS, HISTORIAL, ESTADISTICAS
    }
}
