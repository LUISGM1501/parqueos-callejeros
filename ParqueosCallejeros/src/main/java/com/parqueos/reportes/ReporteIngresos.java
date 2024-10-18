package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.List;

// ReporteIngresos implementa la interface Reporte
public class ReporteIngresos implements Reporte {
    // Atributos para la fecha de inicio, fecha de fin y los ingresos por dia
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<Double> ingresosPorDia;

    // Constructor para el reporte de ingresos
    public ReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, List<Double> ingresosPorDia) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ingresosPorDia = ingresosPorDia;
    }

    // Metodo para generar un reporte de ingresos
    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear un StringBuilder para construir el reporte
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Ingresos\n");
        sb.append("Desde: ").append(this.fechaInicio).append(" Hasta: ").append(this.fechaFin).append("\n\n");

        // Calcular el total de ingresos
        double total = 0;

        // Recorrer los ingresos por dia
        for (int i = 0; i < ingresosPorDia.size(); i++) {
            // Calcular la fecha
            LocalDate fecha = this.fechaInicio.plusDays(i);

            // Obtener el ingreso del dia
            double ingreso = ingresosPorDia.get(i);

            // Agregar la fecha y el ingreso al reporte
            sb.append(fecha).append(": $").append(String.format("%.2f", ingreso)).append("\n");

            // Sumar el ingreso al total
            total += ingreso;
        }

        // Agregar el total de ingresos al reporte
        sb.append("\nTotal: $").append(String.format("%.2f", total));

        // Retornar el reporte
        return sb.toString();
    }
}