package com.parqueos.reportes;

import java.time.LocalDate;

// Interface para los reportes
public interface Reporte {

    // Metodo para generar un reporte el cual debe ser implementado en las clases 
    // que implementen esta interface
    String generarReporte(LocalDate fechaInicio, LocalDate fechaFin);
}