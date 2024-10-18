package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.List;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;

public class ReporteFactory {

    // Metodo Factory para crear un reporte 
    public static Reporte crearReporte(TipoReporte tipo, LocalDate fechaInicio, LocalDate fechaFin, 
                                       List<EspacioParqueo> espacios, List<Reserva> reservas, 
                                       List<Double> ingresos, List<Multa> multas) {
        return switch (tipo) {
            case INGRESOS -> new ReporteIngresos(fechaInicio, fechaFin, ingresos);
            case MULTAS -> new ReporteMultas(fechaInicio, fechaFin, multas);
            case ESPACIOS -> new ReporteEspacios(espacios);
            case HISTORIAL -> new ReporteHistorial(fechaInicio, fechaFin, reservas);
            case ESTADISTICAS -> new ReporteEstadisticas(fechaInicio, fechaFin, espacios, reservas);
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipo);
        };
    }

    // Enum para los tipos de reportes
    public enum TipoReporte {
        INGRESOS, MULTAS, ESPACIOS, HISTORIAL, ESTADISTICAS
    }
}
