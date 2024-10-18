package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.List;

import com.parqueos.modelo.multa.Multa;

// ReporteMultas implementa la interface Reporte
public class ReporteMultas implements Reporte {
    // Atributos para la fecha de inicio, fecha de fin y las multas
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<Multa> multas;

    // Constructor para el reporte de multas
    public ReporteMultas(LocalDate fechaInicio, LocalDate fechaFin, List<Multa> multas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.multas = multas;
    }

    // Metodo para generar un reporte de multas
    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear un StringBuilder para construir el reporte
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Multas\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        double total = 0;
        for (Multa multa : multas) {
            // Filtrar las multas que estan dentro del rango de fechas de inicio y fin
            if (!multa.getFechaHora().toLocalDate().isBefore(this.fechaInicio) && !multa.getFechaHora().toLocalDate().isAfter(this.fechaFin)) {
                // Agregar la fecha, la placa, el monto y una linea de separacion
                sb.append(multa.getFechaHora().toLocalDate())
                  .append(": Placa: ").append(multa.getVehiculo().getPlaca())
                  .append(", Monto: $").append(String.format("%.2f", multa.getMonto()))
                  .append("\n");

                // Sumar el monto de la multa al total
                total += multa.getMonto();
            }
        }

        // Agregar el total de multas al reporte
        sb.append("\nTotal de multas: $").append(String.format("%.2f", total));

        // Retornar el reporte
        return sb.toString();
    }
}