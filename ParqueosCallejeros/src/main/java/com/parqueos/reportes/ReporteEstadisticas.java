package com.parqueos.reportes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;

// ReporteEstadisticas implementa la interface Reporte
public class ReporteEstadisticas implements Reporte {
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<EspacioParqueo> espacios;
    private final List<Reserva> reservas;

    // Constructor para el reporte de estadisticas
    public ReporteEstadisticas(LocalDate fechaInicio, LocalDate fechaFin, List<EspacioParqueo> espacios, List<Reserva> reservas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.espacios = espacios;
        this.reservas = reservas;
    }

    // Metodo para generar un reporte de estadisticas
    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear un StringBuilder para construir el reporte
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Estadísticas de Uso\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        // Calcular el total de minutos entre las fechas de inicio y fin
        long totalMinutos = ChronoUnit.MINUTES.between(
            LocalDateTime.of(this.fechaInicio, LocalTime.MIN),
            LocalDateTime.of(this.fechaFin, LocalTime.MAX)
        );

        // Recorrer los espacios de parqueo
        for (EspacioParqueo espacio : espacios) {
            // Agregar el numero del espacio de parqueo
            sb.append("Espacio: ").append(espacio.getNumero()).append("\n");

            // Calcular los minutos ocupados en el espacio de parqueo
            long minutosOcupado = reservas.stream()
                // Filtrar las reservas que pertenecen al espacio de parqueo
                .filter(r -> r.getEspacio().getNumero().equals(espacio.getNumero()))

                // Filtrar las reservas que estan dentro del rango de fechas de inicio y fin
                .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(this.fechaInicio) && !r.getHoraFin().toLocalDate().isAfter(this.fechaFin))
                
                // Calcular los minutos ocupados en el espacio de parqueo
                .mapToLong(r -> ChronoUnit.MINUTES.between(r.getHoraInicio(), r.getHoraFin()))
                .sum();

            // Calcular el porcentaje de ocupacion
            double porcentajeOcupado = (double) minutosOcupado / totalMinutos * 100;
            double porcentajeVacio = 100 - porcentajeOcupado;

            // Agregar el porcentaje de ocupacion y vacio
            sb.append("  Tiempo ocupado: ").append(String.format("%.2f", porcentajeOcupado)).append("%\n");
            sb.append("  Tiempo vacío: ").append(String.format("%.2f", porcentajeVacio)).append("%\n\n");
        }

        // Estadística resumida
        sb.append("Estadística Resumida:\n");

        // Recorrer las fechas de inicio y fin
        for (LocalDate fecha = this.fechaInicio; !fecha.isAfter(this.fechaFin); fecha = fecha.plusDays(1)) {
            LocalDate fechaFinal = fecha;

            // Calcular los minutos ocupados en el espacio de parqueo
            long minutosOcupadosDia = reservas.stream()
                // Filtrar las reservas que pertenecen al espacio de parqueo
                .filter(r -> r.getHoraInicio().toLocalDate().equals(fechaFinal))
                .mapToLong(r -> ChronoUnit.MINUTES.between(
                    r.getHoraInicio().toLocalDate().atStartOfDay(),
                    r.getHoraFin().toLocalDate().equals(fechaFinal) ? r.getHoraFin() : fechaFinal.atTime(LocalTime.MAX)
                ))
                .sum();

            // Calcular el porcentaje de ocupacion
            double porcentajeOcupadoDia = (double) minutosOcupadosDia / (24 * 60 * espacios.size()) * 100;
            double porcentajeVacioDia = 100 - porcentajeOcupadoDia;

            // Agregar la fecha y los porcentajes de ocupacion y vacio
            sb.append(fecha).append(":\n");
            sb.append("  Ocupación: ").append(String.format("%.2f", porcentajeOcupadoDia)).append("%\n");
            sb.append("  Vacío: ").append(String.format("%.2f", porcentajeVacioDia)).append("%\n\n");
        }

        // Retornar el reporte
        return sb.toString();
    }
}
