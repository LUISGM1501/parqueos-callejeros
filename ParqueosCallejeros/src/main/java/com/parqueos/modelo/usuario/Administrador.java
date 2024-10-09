package com.parqueos.modelo.usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.reportes.Reporte;
import com.parqueos.reportes.ReporteFactory;
import com.parqueos.reportes.ReporteFactory.TipoReporte;

public class Administrador extends Usuario {

    public Administrador(String nombre, String apellidos, int telefono, String email, String direccion, String idUsuario, String pin) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin, TipoUsuario.ADMINISTRADOR);
    }

    // Constructor sin argumentos para Jackson
    public Administrador() {
        super("", "", 0, "", "", "", "", TipoUsuario.ADMINISTRADOR);
    }

    public static List<Administrador> cargarTodosAdminis() {
        return Usuario.cargarTodos().stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.ADMINISTRADOR)
                .map(u -> (Administrador) u)
                .collect(Collectors.toList());
    }

    public void configurarParqueo(ConfiguracionParqueo configuracion) {
        ConfiguracionParqueo.setInstancia(configuracion);
        configuracion.guardar();
    }

    public Reporte generarReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reserva> reservas = Reserva.cargarTodas().stream()
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        //List<Double> ingresos = reservas.stream().map(configuracion::getPrecioHora).collect(Collectors.toList());
        
        return ReporteFactory.crearReporte(TipoReporte.INGRESOS, fechaInicio, fechaFin, null, reservas, null, null);
    }

    public Reporte generarReporteMultas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Multa> multas = Multa.cargarTodas().stream()
            .filter(m -> !m.getFechaHora().toLocalDate().isBefore(fechaInicio) && !m.getFechaHora().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        return ReporteFactory.crearReporte(TipoReporte.MULTAS, fechaInicio, fechaFin, null, null, null, multas);
    }

    public Reporte generarReporteEspacios(LocalDate fechaInicio, LocalDate fechaFin) {
        List<EspacioParqueo> espacios = EspacioParqueo.cargarTodos();
        
        return ReporteFactory.crearReporte(TipoReporte.ESPACIOS, fechaInicio, fechaFin, espacios, null, null, null);
    }

    public Reporte generarReporteHistorial(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reserva> reservas = Reserva.cargarTodas().stream()
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        return ReporteFactory.crearReporte(TipoReporte.HISTORIAL, fechaInicio, fechaFin, null, reservas, null, null);
    }

    public Reporte generarReporteEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reserva> reservas = Reserva.cargarTodas().stream()
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        List<EspacioParqueo> espacios = EspacioParqueo.cargarTodos();
        
        return ReporteFactory.crearReporte(TipoReporte.ESTADISTICAS, fechaInicio, fechaFin, espacios, reservas, null, null);
    }
}