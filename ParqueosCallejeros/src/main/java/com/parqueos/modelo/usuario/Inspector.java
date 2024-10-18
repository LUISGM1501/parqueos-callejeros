package com.parqueos.modelo.usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.reportes.Reporte;
import com.parqueos.reportes.ReporteFactory;
import com.parqueos.reportes.ReporteFactory.TipoReporte;

public class Inspector extends Usuario {
    private String terminalId;

    // Constructor de inspector 
    public Inspector(String nombre, String apellidos, int telefono, String email, String direccion, 
                     String idUsuario, String pin, String terminalId) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin, TipoUsuario.INSPECTOR);
        this.terminalId = terminalId;
    }

    // Constructor sin argumentos para el JSON
    public Inspector() {
        super("", "", 0, "", "", "", "", TipoUsuario.INSPECTOR);
    }

    // Getters y setters
    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        this.actualizarEnArchivo();
    }

    // Metodos
    public static List<Inspector> cargarTodosInspectors() {
        // Cargar todos los usuarios
        return Usuario.cargarTodos().stream()
                // Filtrar los inspectores
                .filter(u -> u.getTipoUsuario() == TipoUsuario.INSPECTOR)
                .map(u -> (Inspector) u)
                .collect(Collectors.toList());
    }

    // Metodo para revisar un parqueo
    public Multa revisarParqueo(EspacioParqueo espacio) {
        //  Verificar si el parqueo tiene configuracion
        if (espacio.estaOcupado() && !espacio.estaPagado()) {
            // Obtener la configuracion del parqueo
            ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();

            // Generar la multa
            return generarMulta(espacio, config.getCostoMulta());
        }
        return null;
    }

    // Metodo para generar una multa
    public Multa generarMulta(EspacioParqueo espacio, double monto) {
        // Crear la multa
        Multa multa = new Multa(espacio.getVehiculoActual(), espacio, this, monto);

        // Guardar la multa
        multa.guardar();

        // Retornar la multa
        return multa;
    }

    // Metodo para generar un reporte de espacios
    public Reporte generarReporteEspacios(List<EspacioParqueo> espacios) {
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();

        // Retornar el reporte de espacios
        return ReporteFactory.crearReporte(TipoReporte.ESPACIOS, fechaActual, fechaActual, espacios, null, null, null);
    }

    // Metodo para generar un reporte de multas
    public Reporte generarReporteMultas(List<Multa> multas, LocalDate fechaInicio, LocalDate fechaFin) {
        // Filtrar las multas por fecha
        multas = multas.stream()
            .filter(m -> !m.getFechaHora().toLocalDate().isBefore(fechaInicio) && !m.getFechaHora().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        // Retornar el reporte de multas
        return ReporteFactory.crearReporte(TipoReporte.MULTAS, fechaInicio, fechaFin, null, null, null, multas);
    }

}