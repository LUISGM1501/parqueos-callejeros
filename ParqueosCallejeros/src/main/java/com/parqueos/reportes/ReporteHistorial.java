package com.parqueos.reportes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.parqueos.modelo.parqueo.Reserva;

public class ReporteHistorial implements Reporte {
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<Reserva> reservas;

    public ReporteHistorial(LocalDate fechaInicio, LocalDate fechaFin, List<Reserva> reservas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.reservas = reservas;
    }

    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte Historial de Espacios Usados\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        // Agrupar reservas por fecha
        Map<LocalDate, List<Reserva>> reservasPorDia = reservas.stream()
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(this.fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(this.fechaFin))
            .collect(Collectors.groupingBy(r -> r.getHoraInicio().toLocalDate()));

        // Ordenar por fecha descendente
        reservasPorDia.entrySet().stream()
            .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
            .forEach(entry -> {
                LocalDate fecha = entry.getKey();
                List<Reserva> reservasDelDia = entry.getValue();

                sb.append("Fecha: ").append(fecha).append("\n");
                for (Reserva reserva : reservasDelDia) {
                    sb.append("  Espacio: ").append(reserva.getEspacio().getNumero())
                      .append(", Vehículo: ").append(reserva.getVehiculo().getPlaca())
                      .append(", Inicio: ").append(reserva.getHoraInicio().toLocalTime())
                      .append(", Fin: ").append(reserva.getHoraFin().toLocalTime())
                      .append(", Duración: ").append(ChronoUnit.MINUTES.between(reserva.getHoraInicio(), reserva.getHoraFin()))
                      .append(" minutos\n");
                }
                sb.append("\n");
            });

        return sb.toString();
    }
}