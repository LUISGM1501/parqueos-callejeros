package com.parqueos.reportes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;

public class ReporteEstadisticas implements Reporte {
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<EspacioParqueo> espacios;
    private final List<Reserva> reservas;

    public ReporteEstadisticas(LocalDate fechaInicio, LocalDate fechaFin, List<EspacioParqueo> espacios, List<Reserva> reservas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.espacios = espacios;
        this.reservas = reservas;
    }

    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Estadísticas de Uso\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        long totalMinutos = ChronoUnit.MINUTES.between(
            LocalDateTime.of(this.fechaInicio, LocalTime.MIN),
            LocalDateTime.of(this.fechaFin, LocalTime.MAX)
        );

        for (EspacioParqueo espacio : espacios) {
            sb.append("Espacio: ").append(espacio.getNumero()).append("\n");

            long minutosOcupado = reservas.stream()
                .filter(r -> r.getEspacio().getNumero().equals(espacio.getNumero()))
                .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(this.fechaInicio) && !r.getHoraFin().toLocalDate().isAfter(this.fechaFin))
                .mapToLong(r -> ChronoUnit.MINUTES.between(r.getHoraInicio(), r.getHoraFin()))
                .sum();

            double porcentajeOcupado = (double) minutosOcupado / totalMinutos * 100;
            double porcentajeVacio = 100 - porcentajeOcupado;

            sb.append("  Tiempo ocupado: ").append(String.format("%.2f", porcentajeOcupado)).append("%\n");
            sb.append("  Tiempo vacío: ").append(String.format("%.2f", porcentajeVacio)).append("%\n\n");
        }

        // Estadística resumida
        sb.append("Estadística Resumida:\n");
        for (LocalDate fecha = this.fechaInicio; !fecha.isAfter(this.fechaFin); fecha = fecha.plusDays(1)) {
            LocalDate fechaFinal = fecha;
            long minutosOcupadosDia = reservas.stream()
                .filter(r -> r.getHoraInicio().toLocalDate().equals(fechaFinal))
                .mapToLong(r -> ChronoUnit.MINUTES.between(
                    r.getHoraInicio().toLocalDate().atStartOfDay(),
                    r.getHoraFin().toLocalDate().equals(fechaFinal) ? r.getHoraFin() : fechaFinal.atTime(LocalTime.MAX)
                ))
                .sum();

            double porcentajeOcupadoDia = (double) minutosOcupadosDia / (24 * 60 * espacios.size()) * 100;
            double porcentajeVacioDia = 100 - porcentajeOcupadoDia;

            sb.append(fecha).append(":\n");
            sb.append("  Ocupación: ").append(String.format("%.2f", porcentajeOcupadoDia)).append("%\n");
            sb.append("  Vacío: ").append(String.format("%.2f", porcentajeVacioDia)).append("%\n\n");
        }

        return sb.toString();
    }
}
