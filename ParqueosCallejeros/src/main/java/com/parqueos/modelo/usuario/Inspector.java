package com.parqueos.modelo.usuario;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.multa.Multa;
import com.parqueos.reportes.Reporte;

import java.time.LocalDate;

public class Inspector extends Usuario {
    private String terminalId;

    public Inspector(String nombre, String apellidos, int telefono, String email, String direccion, 
                     String idUsuario, String pin, String terminalId) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin);
        this.terminalId = terminalId;
    }

    public Multa revisarParqueo(EspacioParqueo espacio) {
        if (espacio.estaOcupado() && !espacio.estaPagado()) {
            return generarMulta(espacio);
        }
        return null;
    }

    private Multa generarMulta(EspacioParqueo espacio) {
        // Falta implementar la logica para generar una multa
        Multa multa = new Multa(espacio.getVehiculoActual(), espacio, this);
        // Falta implementar la logica para guardar la multa en el sistema
        return multa;
    }

    public Reporte generarReporteEspacios() {
        // Falta implementar la logica para generar el reporte de espacios
        return null; 
    }

    public Reporte generarReporteMultas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Falta implementar la logica para generar el reporte de multas
        return null;
    }

    // Getter y setter para terminalId
    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}