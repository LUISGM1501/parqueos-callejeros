package com.parqueos.reportes;

import java.time.LocalDate;
import java.util.List;  

import com.parqueos.modelo.parqueo.EspacioParqueo;

// ResporteEspacios implementa la interface Reporte
public class ReporteEspacios implements Reporte {
    private final List<EspacioParqueo> espacios;

    // Constructor para el reporte de espacios de parqueo
    public ReporteEspacios(List<EspacioParqueo> espacios) {
        this.espacios = espacios;
    }

    // Metodo para generar un reporte de los espacios de parqueo
    @Override
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        // Crear un StringBuilder para construir el reporte
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Espacios de Parqueo\n");
        sb.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        // Contadores para los espacios ocupados y vacios
        int ocupados = 0;
        int vacios = 0;

        // Recorrer los espacios de parqueo
        for (EspacioParqueo espacio : espacios) {
            // Agregar el numero del espacio de parqueo
            sb.append("Espacio ").append(espacio.getNumero()).append(": ");

            // Verificar si el espacio de parqueo esta ocupado
            if (espacio.estaOcupado()) {

                // Agregar que el espacio de parqueo esta ocupado
                sb.append("Ocupado");

                // Verificar si el vehiculo actual no es null
                if (espacio.getVehiculoActual() != null) {
                    // Agregar la placa del vehiculo que esta en el espacio de parqueo
                    sb.append(" (Placa: ").append(espacio.getVehiculoActual().getPlaca()).append(")");
                }
                
                // Incrementar el contador de espacios ocupados
                ocupados++;
            } else {
                // Agregar que el espacio de parqueo esta vacio
                sb.append("Vacío");
                
                // Incrementar el contador de espacios vacios
                vacios++;
            }

            // Salto de linea para mayor claridad
            sb.append("\n");
        }

        // Salto de linea para mayor claridad
        sb.append("\nResumen:\n");

        // Agregar el total de espacios
        sb.append("Total de espacios: ").append(espacios.size()).append("\n");

        // Agregar el total de espacios ocupados
        sb.append("Espacios ocupados: ").append(ocupados).append("\n");

        // Agregar el total de espacios vacios
        sb.append("Espacios vacíos: ").append(vacios);

        // Retornar el reporte
        return sb.toString();
    }
}