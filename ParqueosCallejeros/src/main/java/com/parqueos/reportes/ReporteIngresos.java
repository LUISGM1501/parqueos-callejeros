package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// ReporteIngresos implementa la interface Reporte
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteIngresos implements Reporte {
    // Atributos para la fecha de inicio, fecha de fin y los ingresos por dia
    @JsonProperty("fechaInicio")
    private final LocalDate fechaInicio;
    
    @JsonProperty("fechaFin")
    private final LocalDate fechaFin;
    
    @JsonProperty("ingresosPorDia") 
    private final List<Double> ingresosPorDia;

    // Constructor principal
    public ReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, List<Double> ingresosPorDia) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ingresosPorDia = ingresosPorDia != null ? ingresosPorDia : new ArrayList<>();
    }

    // Constructor sin argumentos para Jackson
    @JsonCreator
    public ReporteIngresos() {
        this.fechaInicio = LocalDate.now();
        this.fechaFin = LocalDate.now();
        this.ingresosPorDia = new ArrayList<>();
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

        if (ingresosPorDia != null && !ingresosPorDia.isEmpty()) {
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
        } else {
            sb.append("No hay ingresos registrados para este período.\n");
        }

        // Agregar el total de ingresos al reporte
        sb.append("\nTotal: $").append(String.format("%.2f", total));

        // Retornar el reporte
        return sb.toString();
    }

    // Getters
    @JsonProperty("fechaInicio")
    public LocalDate getFechaInicio() { return fechaInicio; }

    @JsonProperty("fechaFin")
    public LocalDate getFechaFin() { return fechaFin; }

    @JsonProperty("ingresosPorDia")
    public List<Double> getIngresosPorDia() { return ingresosPorDia; }
}