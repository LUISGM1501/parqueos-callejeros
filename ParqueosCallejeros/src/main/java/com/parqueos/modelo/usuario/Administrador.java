package com.parqueos.modelo.usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.reportes.Reporte;
import com.parqueos.reportes.ReporteFactory;
import com.parqueos.reportes.ReporteFactory.TipoReporte;

public class Administrador extends Usuario {

    // Constructor con argumentos
    public Administrador(String nombre, String apellidos, int telefono, String email, String direccion, String idUsuario, String pin) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin, TipoUsuario.ADMINISTRADOR);
    }

    // Constructor sin argumentos para el JSON
    @JsonCreator
    public Administrador() {
        super("", "", 0, "", "", "", "", TipoUsuario.ADMINISTRADOR);
    }

    // Metodo para cargar todos los administradores
    public static List<Administrador> cargarTodosAdminis() {
        // Cargar todos los usuarios
        return Usuario.cargarTodos().stream()
                // Filtrar los administradores
                .filter(u -> u.getTipoUsuario() == TipoUsuario.ADMINISTRADOR)
                .map(u -> (Administrador) u)
                .collect(Collectors.toList());
    }

    // Metodo para configurar el parqueo
    public void configurarParqueo(ConfiguracionParqueo configuracion) {
        // Configurar el parqueo
        ConfiguracionParqueo.obtenerInstancia().setHorarioInicio(configuracion.getHorarioInicio());
        ConfiguracionParqueo.obtenerInstancia().setHorarioFin(configuracion.getHorarioFin());
        ConfiguracionParqueo.obtenerInstancia().setPrecioHora(configuracion.getPrecioHora());
        ConfiguracionParqueo.obtenerInstancia().setTiempoMinimo(configuracion.getTiempoMinimo());
        ConfiguracionParqueo.obtenerInstancia().setCostoMulta(configuracion.getCostoMulta());
        
        // Guardar la configuracion
        configuracion.guardar();
    }

    // Metodo para generar un reporte de ingresos
    public Reporte generarReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin) {
        // Cargar todas las reservas 
        List<Reserva> reservas = Reserva.cargarTodas().stream()
            // Filtrar las reservas por fecha
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        //List<Double> ingresos = reservas.stream().map(configuracion::getPrecioHora).collect(Collectors.toList());
        
        // Retornar el reporte de ingresos
        return ReporteFactory.crearReporte(TipoReporte.INGRESOS, fechaInicio, fechaFin, null, reservas, null, null);
    }

    // Metodo para generar un reporte de multas
    public Reporte generarReporteMultas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Cargar todas las multas
        List<Multa> multas = Multa.cargarTodas().stream()
            // Filtrar las multas por fecha
            .filter(m -> !m.getFechaHora().toLocalDate().isBefore(fechaInicio) && !m.getFechaHora().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        // Retornar el reporte de multas
        return ReporteFactory.crearReporte(TipoReporte.MULTAS, fechaInicio, fechaFin, null, null, null, multas);
    }

    // Metodo para generar un reporte de espacios
    public Reporte generarReporteEspacios(LocalDate fechaInicio, LocalDate fechaFin) {
        // Cargar todos los espacios
        List<EspacioParqueo> espacios = EspacioParqueo.cargarTodos();
        
        // Retornar el reporte de espacios
        return ReporteFactory.crearReporte(TipoReporte.ESPACIOS, fechaInicio, fechaFin, espacios, null, null, null);
    }

    // Metodo para generar un reporte de historial
    public Reporte generarReporteHistorial(LocalDate fechaInicio, LocalDate fechaFin) {
        // Cargar todas las reservas
        List<Reserva> reservas = Reserva.cargarTodas().stream()
            // Filtrar las reservas por fecha
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        // Retornar el reporte de historial
        return ReporteFactory.crearReporte(TipoReporte.HISTORIAL, fechaInicio, fechaFin, null, reservas, null, null);
    }

    // Metodo para generar un reporte de estadisticas
    public Reporte generarReporteEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Cargar todas las reservas
        List<Reserva> reservas = Reserva.cargarTodas().stream()
            // Filtrar las reservas por fecha
            .filter(r -> !r.getHoraInicio().toLocalDate().isBefore(fechaInicio) && !r.getHoraInicio().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        // Cargar todos los espacios
        List<EspacioParqueo> espacios = EspacioParqueo.cargarTodos();
        
        // Retornar el reporte de estadisticas
        return ReporteFactory.crearReporte(TipoReporte.ESTADISTICAS, fechaInicio, fechaFin, espacios, reservas, null, null);
    }
}