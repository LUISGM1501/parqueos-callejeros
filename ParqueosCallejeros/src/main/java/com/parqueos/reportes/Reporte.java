package com.parqueos.reportes;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Interface para los reportes
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Reporte {

    // Metodo para generar un reporte el cual debe ser implementado en las clases 
    // que implementen esta interface
    String generarReporte(LocalDate fechaInicio, LocalDate fechaFin);
}