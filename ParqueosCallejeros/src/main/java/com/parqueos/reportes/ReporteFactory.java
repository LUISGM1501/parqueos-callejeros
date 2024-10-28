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
            // Crear un reporte de ingresos
            case INGRESOS -> {
                // Calcular los ingresos por dia
                List<Double> ingresosPorDia = calcularIngresosPorDia(reservas, fechaInicio, fechaFin);
                // Crear un reporte de ingresos
                yield new ReporteIngresos(fechaInicio, fechaFin, ingresosPorDia);
            }
            // Crear un reporte de multas
            case MULTAS -> new ReporteMultas(fechaInicio, fechaFin, multas);
            // Crear un reporte de espacios
            case ESPACIOS -> new ReporteEspacios(espacios);
            // Crear un reporte historial
            case HISTORIAL -> new ReporteHistorial(fechaInicio, fechaFin, reservas);
            // Crear un reporte de estadisticas
            case ESTADISTICAS -> new ReporteEstadisticas(fechaInicio, fechaFin, espacios, reservas);
            // Lanzar una excepcion si el tipo de reporte no es soportado
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipo);
        };
    }

    // Metodo para calcular los ingresos por dia
    private static List<Double> calcularIngresosPorDia(List<Reserva> reservas, LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear una lista para almacenar los ingresos por dia
        List<Double> ingresosPorDia = new ArrayList<>();
        // Verificar si la lista de reservas es nula o esta vacia
        if (reservas == null || reservas.isEmpty()) {
            return ingresosPorDia;
        }

        LocalDate fecha = fechaInicio;
        // Iterar sobre todas las fechas desde la fecha de inicio hasta la fecha de fin
        while (!fecha.isAfter(fechaFin)) {
            // Obtener la fecha actual
            final LocalDate fechaActual = fecha;
            double ingresoDia = reservas.stream()
                // Filtrar las reservas que coinciden con la fecha actual
                .filter(r -> r.getHoraInicio().toLocalDate().equals(fechaActual))
                // Calcular el ingreso para cada reserva
                .mapToDouble(r -> {
                    // Calcular la duracion en minutos entre la hora de inicio y fin de la reserva
                    long minutos = ChronoUnit.MINUTES.between(r.getHoraInicio(), r.getHoraFin());
                    return (minutos / 60.0) * ConfiguracionParqueo.obtenerInstancia().getPrecioHora();
                })
                .sum();
            
            // Agregar el ingreso del dia a la lista
            ingresosPorDia.add(ingresoDia);
            // Incrementar la fecha en un dia
            fecha = fecha.plusDays(1);
        }
        
        return ingresosPorDia;
    }

    // Enum para los tipos de reportes
    public enum TipoReporte {
        INGRESOS, MULTAS, ESPACIOS, HISTORIAL, ESTADISTICAS
    }
}
