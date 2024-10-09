package com.parqueos.ui.controladores;

import java.time.LocalDate;

import javax.swing.JOptionPane;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.reportes.Reporte;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaAdministrador;
import com.parqueos.ui.vistas.VistaConfiguracionParqueo;

public class ControladorAdministrador extends ControladorBase {
    private VistaAdministrador vista;
    private SistemaParqueo sistemaParqueo;
    private String token;

    public ControladorAdministrador(VistaAdministrador vista, SistemaParqueo sistemaParqueo, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.token = token;
        inicializar();
    }

    @Override
    protected void inicializar() {
        vista.getBtnConfigurarParqueo().addActionListener(e -> configurarParqueo());
        vista.getBtnGestionarUsuarios().addActionListener(e -> gestionarUsuarios());
        vista.getBtnGestionarEspacios().addActionListener(e -> gestionarEspacios());
        vista.getBtnGenerarReporteIngresos().addActionListener(e -> generarReporteIngresos());
        vista.getBtnGenerarReporteMultas().addActionListener(e -> generarReporteMultas());
        vista.getBtnGenerarReporteEspacios().addActionListener(e -> generarReporteEspacios());
        vista.getBtnGenerarReporteHistorial().addActionListener(e -> generarReporteHistorial());
        vista.getBtnGenerarReporteEstadisticas().addActionListener(e -> generarReporteEstadisticas());
    }

    private void configurarParqueo() {
        ConfiguracionParqueo configuracionActual = sistemaParqueo.getConfiguracion(token);
        VistaConfiguracionParqueo vistaConfig = new VistaConfiguracionParqueo(vista, configuracionActual);
        
        vistaConfig.getBtnGuardar().addActionListener(e -> {
            ConfiguracionParqueo nuevaConfiguracion = vistaConfig.getConfiguracion();
            sistemaParqueo.setConfiguracion(token, nuevaConfiguracion);
            JOptionPane.showMessageDialog(vista, "Configuración actualizada con éxito.");
            vistaConfig.dispose();
        });

        vistaConfig.getBtnCancelar().addActionListener(e -> vistaConfig.dispose());

        vistaConfig.setVisible(true);
    }

    private void gestionarUsuarios() {
        // Aquí deberías abrir una nueva vista para gestionar usuarios
        JOptionPane.showMessageDialog(vista, "Funcionalidad de gestión de usuarios no implementada aún.");
    }

    private void gestionarEspacios() {
        // Aquí deberías abrir una nueva vista para gestionar espacios
        JOptionPane.showMessageDialog(vista, "Funcionalidad de gestión de espacios no implementada aún.");
    }

    private void generarReporteIngresos() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteIngresos(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());
        mostrarReporte(reporte);
    }

    private void generarReporteMultas() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteMultas(fechaInicio, fechaFin, sistemaParqueo.getGestorMultas().getMultas());
        mostrarReporte(reporte);
    }

    private void generarReporteEspacios() {
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteEspacios(sistemaParqueo.getGestorEspacios().getEspacios());
        mostrarReporte(reporte);
    }

    private void generarReporteHistorial() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteHistorial(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());
        mostrarReporte(reporte);
    }

    private void generarReporteEstadisticas() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteEstadisticas(fechaInicio, fechaFin, 
            sistemaParqueo.getGestorEspacios().getEspacios(), 
            sistemaParqueo.getGestorReservas().getReservas());
        mostrarReporte(reporte);
    }

    private void mostrarReporte(Reporte reporte) {
        // Aquí deberías mostrar el reporte en una nueva ventana o guardarlo como PDF
        LocalDate fechaInicio = LocalDate.now().minusMonths(1); // Ejemplo: último mes
        LocalDate fechaFin = LocalDate.now();
        JOptionPane.showMessageDialog(vista, "Reporte generado:\n\n" + reporte.generarReporte(fechaInicio, fechaFin));
    }
}