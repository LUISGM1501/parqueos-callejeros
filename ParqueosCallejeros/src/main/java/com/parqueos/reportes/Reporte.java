package com.parqueos.reportes;

import java.time.LocalDate;

public interface Reporte {
    String generarReporte(LocalDate fechaInicio, LocalDate fechaFin);
}