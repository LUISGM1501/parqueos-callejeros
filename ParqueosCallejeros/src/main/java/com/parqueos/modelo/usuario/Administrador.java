package com.parqueos.modelo.usuario;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.reportes.Reporte;

import java.time.LocalDate;
public class Administrador extends Usuario {
    
    public Administrador(String nombre, String apellidos, int telefono, String email, String direccion, String idUsuario, String pin) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin);
    }

    public void configurarParqueo(ConfiguracionParqueo configuracion) {
        // Falta implementar la logica para configurar el parqueo
    }

    public Reporte generarReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin) {
        // Falta implementar la logica para generar el reporte de ingresos
        return null;
    }

    public Reporte generarReporteMultas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Falta implementar la logica para generar el reporte de multas
        return null;
    }

    public Reporte generarReporteEspacios(LocalDate fechaInicio, LocalDate fechaFin) {
        // Falta implementar la logica para generar el reporte de espacios
        return null;
    }

    public Reporte generarReporteHistorial(LocalDate fechaInicio, LocalDate fechaFin) {
        // Falta implementar la logica para generar el reporte de historial
        return null;
    }

    public Reporte generarReporteEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Falta implementar la logica para generar el reporte de estadisticas
        return null;
    }
}