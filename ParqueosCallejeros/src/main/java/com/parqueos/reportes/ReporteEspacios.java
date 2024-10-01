package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.List;  

import com.parqueos.modelo.parqueo.EspacioParqueo;

public class ReporteEspacios implements Reporte {
    private final List<EspacioParqueo> espacios;

    public ReporteEspacios(List<EspacioParqueo> espacios) {
        this.espacios = espacios;
    }

    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Espacios de Parqueo\n");
        sb.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        int ocupados = 0;
        int vacios = 0;

        for (EspacioParqueo espacio : espacios) {
            sb.append("Espacio ").append(espacio.getNumero()).append(": ");
            if (espacio.estaOcupado()) {
                sb.append("Ocupado");
                if (espacio.getVehiculoActual() != null) {
                    sb.append(" (Placa: ").append(espacio.getVehiculoActual().getPlaca()).append(")");
                }
                ocupados++;
            } else {
                sb.append("Vacío");
                vacios++;
            }
            sb.append("\n");
        }

        sb.append("\nResumen:\n");
        sb.append("Total de espacios: ").append(espacios.size()).append("\n");
        sb.append("Espacios ocupados: ").append(ocupados).append("\n");
        sb.append("Espacios vacíos: ").append(vacios);

        return sb.toString();
    }
}