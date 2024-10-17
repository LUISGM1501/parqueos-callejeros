package com.parqueos.ui.controladores;

import java.time.LocalDate;

import javax.swing.JOptionPane;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.reportes.Reporte;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaAdministrador;
import com.parqueos.ui.vistas.VistaConfiguracionParqueo;
import com.parqueos.ui.vistas.VistaGestionUsuarios;
import com.parqueos.util.GeneradorPDF;

public class ControladorAdministrador extends ControladorBase {
    private final VistaAdministrador vista;
    private final SistemaParqueo sistemaParqueo;
    private final String token;

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
        ConfiguracionParqueo configuracionActual = ConfiguracionParqueo.obtenerInstancia();
        VistaConfiguracionParqueo vistaConfig = new VistaConfiguracionParqueo(vista, configuracionActual);
        
        vistaConfig.getBtnGuardar().addActionListener(e -> {
            ConfiguracionParqueo nuevaConfiguracion = vistaConfig.getConfiguracion();
            try {
                sistemaParqueo.setConfiguracion(token, nuevaConfiguracion);
                JOptionPane.showMessageDialog(vista, "Configuración actualizada con éxito.");
                vistaConfig.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(vista, "Error al actualizar la configuración: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(vista, "Error de autorización: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        vistaConfig.getBtnCancelar().addActionListener(e -> vistaConfig.dispose());

        vistaConfig.setVisible(true);
    }

    private void gestionarUsuarios() {
        VistaGestionUsuarios vistaGestionUsuarios = new VistaGestionUsuarios(vista);
        ControladorGestionUsuarios controladorGestionUsuarios = new ControladorGestionUsuarios(vistaGestionUsuarios, sistemaParqueo, token);
        vistaGestionUsuarios.setVisible(true);
    }

    private void gestionarEspacios() {
        // Implementación pendiente
        JOptionPane.showMessageDialog(vista, "Funcionalidad de gestión de espacios no implementada aún.", "En desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarReporteIngresos() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteIngresos(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());
        mostrarReporte(reporte, "Reporte de Ingresos");
    }

    private void generarReporteMultas() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteMultas(fechaInicio, fechaFin, sistemaParqueo.getGestorMultas().getMultas());
        mostrarReporte(reporte, "Reporte de Multas");
    }

    private void generarReporteEspacios() {
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteEspacios(sistemaParqueo.getGestorEspacios().getEspacios());
        mostrarReporte(reporte, "Reporte de Espacios");
    }

    private void generarReporteHistorial() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteHistorial(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());
        mostrarReporte(reporte, "Reporte de Historial");
    }

    private void generarReporteEstadisticas() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteEstadisticas(fechaInicio, fechaFin, 
            sistemaParqueo.getGestorEspacios().getEspacios(), 
            sistemaParqueo.getGestorReservas().getReservas());
        mostrarReporte(reporte, "Reporte de Estadísticas");
    }

    private void mostrarReporte(Reporte reporte, String titulo) {
        LocalDate fechaInicio = LocalDate.now().minusMonths(1);
        LocalDate fechaFin = LocalDate.now();
        String contenidoReporte = reporte.generarReporte(fechaInicio, fechaFin);
        
        try {
            String nombreArchivo = titulo.toLowerCase().replace(" ", "_") + ".pdf";
            GeneradorPDF.generarPDF(nombreArchivo, contenidoReporte);
            JOptionPane.showMessageDialog(vista, 
                "Reporte generado con éxito.\nArchivo: " + nombreArchivo, 
                "Reporte Generado", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, 
                "Error al generar el PDF: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            // Si falla la generacion del PDF, mostramos el reporte en un diálogo
            JOptionPane.showMessageDialog(vista, contenidoReporte, titulo, JOptionPane.PLAIN_MESSAGE);
        }
    }
}