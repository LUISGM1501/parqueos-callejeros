package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.List;

import com.parqueos.modelo.multa.Multa;

public class ReporteMultas implements Reporte {
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<Multa> multas;

    public ReporteMultas(LocalDate fechaInicio, LocalDate fechaFin, List<Multa> multas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.multas = multas;
    }

    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Multas\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        double total = 0;
        for (Multa multa : multas) {
            if (!multa.getFechaHora().toLocalDate().isBefore(this.fechaInicio) && !multa.getFechaHora().toLocalDate().isAfter(this.fechaFin)) {
                sb.append(multa.getFechaHora().toLocalDate())
                  .append(": Placa: ").append(multa.getVehiculo().getPlaca())
                  .append(", Monto: $").append(String.format("%.2f", multa.getMonto()))
                  .append("\n");
                total += multa.getMonto();
            }
        }

        sb.append("\nTotal de multas: $").append(String.format("%.2f", total));
        return sb.toString();
    }
}