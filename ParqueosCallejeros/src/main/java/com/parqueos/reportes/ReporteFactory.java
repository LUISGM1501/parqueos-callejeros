package com.parqueos.reportes;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.multa.Multa;

import java.time.LocalDate;
import java.util.List;

public class ReporteFactory {
    public static Reporte crearReporte(TipoReporte tipo, LocalDate fechaInicio, LocalDate fechaFin, 
                                       List<EspacioParqueo> espacios, List<Reserva> reservas, 
                                       List<Double> ingresos, List<Multa> multas) {
        switch (tipo) {
            case INGRESOS:
                return new ReporteIngresos(fechaInicio, fechaFin, ingresos);
            case MULTAS:
                return new ReporteMultas(fechaInicio, fechaFin, multas);
            case ESPACIOS:
                return new ReporteEspacios(espacios);
            case HISTORIAL:
                return new ReporteHistorial(fechaInicio, fechaFin, reservas);
            case ESTADISTICAS:
                return new ReporteEstadisticas(fechaInicio, fechaFin, espacios, reservas);
            default:
                throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipo);
        }
    }

    public enum TipoReporte {
        INGRESOS, MULTAS, ESPACIOS, HISTORIAL, ESTADISTICAS
    }
}