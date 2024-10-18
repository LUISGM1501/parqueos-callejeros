package com.parqueos.reportes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.parqueos.modelo.parqueo.Reserva;

// ReporteHistorial implementa la interface Reporte
public class ReporteHistorial implements Reporte {
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<Reserva> reservas;

    // Constructor para el reporte de historial
    public ReporteHistorial(LocalDate fechaInicio, LocalDate fechaFin, List<Reserva> reservas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.reservas = reservas;
    }

    // Metodo para generar un reporte de historial
    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear un StringBuilder para construir el reporte
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte Historial de Espacios Usados\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        // Agrupar reservas por fecha
        Map<LocalDate, List<Reserva>> reservasPorDia = reservas.stream()
            // Filtrar las reservas que estan dentro del rango de fechas de inicio y fin
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(this.fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(this.fechaFin))
            
            // Agrupar las reservas por fecha
            .collect(Collectors.groupingBy(r -> r.getHoraInicio().toLocalDate()));

        // Ordenar por fecha descendente
        reservasPorDia.entrySet().stream()
            // Ordenar las fechas de inicio y fin
            .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
            // Recorrer las fechas de inicio y fin
            .forEach(entry -> {
                LocalDate fecha = entry.getKey();
                List<Reserva> reservasDelDia = entry.getValue();

                // Agregar la fecha
                sb.append("Fecha: ").append(fecha).append("\n");

                // Recorrer las reservas del dia
                for (Reserva reserva : reservasDelDia) {
                    // Agregar la informacion de la reserva
                    sb.append("  Espacio: ").append(reserva.getEspacio().getNumero())
                      .append(", Vehículo: ").append(reserva.getVehiculo().getPlaca())
                      .append(", Inicio: ").append(reserva.getHoraInicio().toLocalTime())
                      .append(", Fin: ").append(reserva.getHoraFin().toLocalTime())
                      .append(", Duración: ").append(ChronoUnit.MINUTES.between(reserva.getHoraInicio(), reserva.getHoraFin()))
                      .append(" minutos\n");
                }

                // Salto de linea para mayor claridad
                sb.append("\n");
            });

        // Retornar el reporte
        return sb.toString();
    }
}