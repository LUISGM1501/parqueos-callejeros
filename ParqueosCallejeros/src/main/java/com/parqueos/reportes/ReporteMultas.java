package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parqueos.modelo.multa.Multa;

// ReporteMultas implementa la interface Reporte
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteMultas implements Reporte {
    @JsonProperty("fechaInicio")
    private final LocalDate fechaInicio;
    
    @JsonProperty("fechaFin")
    private final LocalDate fechaFin;
    
    @JsonProperty("multas")
    private final List<Multa> multas;

    // Constructor para el reporte de multas
    public ReporteMultas(LocalDate fechaInicio, LocalDate fechaFin, List<Multa> multas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.multas = multas != null ? multas : new ArrayList<>();
    }

    // Constructor sin argumentos para Jackson
    @JsonCreator
    public ReporteMultas() {
        this.fechaInicio = LocalDate.now();
        this.fechaFin = LocalDate.now();
        this.multas = new ArrayList<>();
    }

    // Metodo para generar un reporte de multas
    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear un StringBuilder para construir el reporte
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Multas\n");
        sb.append("Desde: ").append(fechaInicio).append(" Hasta: ").append(fechaFin).append("\n\n");

        double total = 0;
        for (Multa multa : multas) {
            // Filtrar las multas que estan dentro del rango de fechas de inicio y fin
            if (!multa.getFechaHora().toLocalDate().isBefore(fechaInicio) && 
                !multa.getFechaHora().toLocalDate().isAfter(fechaFin)) {
                // Agregar la fecha, la placa, el monto y una linea de separacion
                sb.append(multa.getFechaHora().toLocalDate())
                  .append(": Placa: ").append(multa.getVehiculo().getPlaca())
                  .append(", Monto: ₡").append(multa.getMonto())
                  .append("\n");

                // Sumar el monto de la multa al total
                total += multa.getMonto();
            }
        }

        // Agregar el total de multas al reporte
        sb.append("\nTotal de multas: ₡").append(String.format("%d", (int)total));

        // Retornar el reporte
        return sb.toString();
    }

    // Getters para Jackson
    @JsonProperty("fechaInicio")
    public LocalDate getFechaInicio() { return fechaInicio; }

    @JsonProperty("fechaFin")
    public LocalDate getFechaFin() { return fechaFin; }

    @JsonProperty("multas")
    public List<Multa> getMultas() { return multas; }
}