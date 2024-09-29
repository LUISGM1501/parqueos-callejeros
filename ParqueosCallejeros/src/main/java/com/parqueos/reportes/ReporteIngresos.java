package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.List;

public class ReporteIngresos implements Reporte {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Double> ingresosPorDia;

    public ReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, List<Double> ingresosPorDia) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ingresosPorDia = ingresosPorDia;
    }

    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Ingresos\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        double total = 0;
        for (int i = 0; i < ingresosPorDia.size(); i++) {
            LocalDate fecha = this.fechaInicio.plusDays(i);
            double ingreso = ingresosPorDia.get(i);
            sb.append(fecha).append(": $").append(String.format("%.2f", ingreso)).append("\n");
            total += ingreso;
        }

        sb.append("\nTotal: $").append(String.format("%.2f", total));
        return sb.toString();
    }
}