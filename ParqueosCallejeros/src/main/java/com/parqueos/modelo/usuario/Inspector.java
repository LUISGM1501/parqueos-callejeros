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

    public Inspector(String nombre, String apellidos, int telefono, String email, String direccion, 
                     String idUsuario, String pin, String terminalId) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin, TipoUsuario.INSPECTOR);
        this.terminalId = terminalId;
    }

    // Constructor sin argumentos para Jackson
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
        return Usuario.cargarTodos().stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.INSPECTOR)
                .map(u -> (Inspector) u)
                .collect(Collectors.toList());
    }

    public Multa revisarParqueo(EspacioParqueo espacio) {
        if (espacio.estaOcupado() && !espacio.estaPagado()) {
            ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
            return generarMulta(espacio, config.getCostoMulta());
        }
        return null;
    }

    public Multa generarMulta(EspacioParqueo espacio, double monto) {
        Multa multa = new Multa(espacio.getVehiculoActual(), espacio, this, monto);
        multa.guardar();
        return multa;
    }

    public Reporte generarReporteEspacios(List<EspacioParqueo> espacios) {
        LocalDate fechaActual = LocalDate.now();
        return ReporteFactory.crearReporte(TipoReporte.ESPACIOS, fechaActual, fechaActual, espacios, null, null, null);
    }

    public Reporte generarReporteMultas(List<Multa> multas, LocalDate fechaInicio, LocalDate fechaFin) {
        multas = multas.stream()
            .filter(m -> !m.getFechaHora().toLocalDate().isBefore(fechaInicio) && !m.getFechaHora().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        return ReporteFactory.crearReporte(TipoReporte.MULTAS, fechaInicio, fechaFin, null, null, null, multas);
    }

}