package com.parqueos.ui.controladores;

import javax.swing.JOptionPane;

import java.time.LocalDate;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.reportes.Reporte;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaAdministrador;
import com.parqueos.ui.vistas.VistaConfiguracionParqueo;
import com.parqueos.ui.vistas.VistaGestionUsuarios;
import com.parqueos.ui.vistas.VistaGestionarEspacios;
import com.parqueos.ui.vistas.DialogoFecha;
import com.parqueos.util.GeneradorPDF;

// Controlador para el administrador
public class ControladorAdministrador extends ControladorBase {

    private final VistaAdministrador vista;
    private final SistemaParqueo sistemaParqueo;
    private final String token;

    // Constructor para inicializar el controlador
    public ControladorAdministrador(VistaAdministrador vista, SistemaParqueo sistemaParqueo, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.token = token;
        inicializar();
    }

    @Override
    // Metodo para inicializar el controlador
    protected void inicializar() {
        // Setear el boton de configurar parqueo
        vista.getBtnConfigurarParqueo().addActionListener(e -> configurarParqueo());
        // Setear el boton de gestionar usuarios
        vista.getBtnGestionarUsuarios().addActionListener(e -> gestionarUsuarios());
        // Setear el boton de gestionar espacios
        vista.getBtnGestionarEspacios().addActionListener(e -> gestionarEspacios());
        // Setear el boton de generar reporte de ingresos
        vista.getBtnGenerarReporteIngresos().addActionListener(e -> generarReporteIngresos());
        // Setear el boton de generar reporte de multas
        vista.getBtnGenerarReporteMultas().addActionListener(e -> generarReporteMultas());
        // Setear el boton de generar reporte de espacios
        vista.getBtnGenerarReporteEspacios().addActionListener(e -> generarReporteEspacios());
        // Setear el boton de generar reporte de historial
        vista.getBtnGenerarReporteHistorial().addActionListener(e -> generarReporteHistorial());
        // Setear el boton de generar reporte de estadisticas
        vista.getBtnGenerarReporteEstadisticas().addActionListener(e -> generarReporteEstadisticas());
    }

    // Metodo para configurar el parqueo
    private void configurarParqueo() {
        // Obtener la configuracion actual
        ConfiguracionParqueo configuracionActual = ConfiguracionParqueo.obtenerInstancia();
        // Crear la vista de configuracion
        VistaConfiguracionParqueo vistaConfig = new VistaConfiguracionParqueo(vista, configuracionActual);

        // Setear el boton de guardar
        vistaConfig.getBtnGuardar().addActionListener(e -> {
            // Obtener la nueva configuracion
            ConfiguracionParqueo nuevaConfiguracion = vistaConfig.getConfiguracion();
            try {
                // Setear la nueva configuracion
                sistemaParqueo.setConfiguracion(token, nuevaConfiguracion);
                //enviar correo
                sistemaParqueo.getGestorNotificaciones().notificarCambioConfiguracion(sistemaParqueo, nuevaConfiguracion, token);
                JOptionPane.showMessageDialog(vista, "Configuración actualizada con éxito.");
                vistaConfig.dispose();
            } catch (IllegalArgumentException ex) {
                // Error al actualizar la configuracion
                JOptionPane.showMessageDialog(vista, "Error al actualizar la configuración: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                // Error de autorizacion
                JOptionPane.showMessageDialog(vista, "Error de autorización: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }           
        });

        // Setear el boton de cancelar
        vistaConfig.getBtnCancelar().addActionListener(e -> vistaConfig.dispose());

        // Mostrar la vista
        vistaConfig.setVisible(true);
    }

    // Metodo para gestionar usuarios
    private void gestionarUsuarios() {
        // Crear la vista de gestion de usuarios
        VistaGestionUsuarios vistaGestionUsuarios = new VistaGestionUsuarios(vista);
        // Crear el controlador de gestion de usuarios
        ControladorGestionUsuarios controladorGestionUsuarios = new ControladorGestionUsuarios(vistaGestionUsuarios, sistemaParqueo, token);
        // Mostrar la vista
        vistaGestionUsuarios.setVisible(true);
    }

    // Metodo para gestionar espacios
    private void gestionarEspacios() {
        //Crear la vista de gestionar espacios
        VistaGestionarEspacios vistaGestEspacios = new VistaGestionarEspacios(vista);
        //Crear el controlador de getion de espacios
        ControladorGestionarEspacios controladorGestionarEspacios = new ControladorGestionarEspacios(vistaGestEspacios, sistemaParqueo, token);
        //Mostrar la vista       
        vistaGestEspacios.setVisible(true);
    }

    //Me´todo para generar reporte de ingresos
    private void generarReporteIngresos() {
        // Crear el diálogo para seleccionar las fechas
        DialogoFecha dialogoFecha = new DialogoFecha();
        LocalDate[] fechas = dialogoFecha.mostrarDialogo();

        // Verificar que se hayan seleccionado las fechas
        if (fechas != null) {
            LocalDate fechaInicio = fechas[0];
            LocalDate fechaFin = fechas[1];

            // Generar el reporte
            Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteIngresos(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());

            // Mostrar el reporte
            mostrarReporte(reporte, "Reporte de Ingresos");
        }
    }


    // Metodo para generar reporte de multas
    private void generarReporteMultas() {
        // Crear el diálogo para seleccionar las fechas
        DialogoFecha dialogoFecha = new DialogoFecha();
        LocalDate[] fechas = dialogoFecha.mostrarDialogo();

        if (fechas != null) {
            LocalDate fechaInicio = fechas[0];
            LocalDate fechaFin = fechas[1];

            // Generar el reporte
            Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteMultas(fechaInicio, fechaFin, sistemaParqueo.getGestorMultas().getMultas());

            // Mostrar el reporte
            mostrarReporte(reporte, "Reporte de Multas");
        }
    }

    // Metodo para generar reporte de espacios
    private void generarReporteEspacios() {
        // Generar el reporte
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteEspacios(sistemaParqueo.getGestorEspacios().getEspacios());
        // Mostrar el reporte
        mostrarReporte(reporte, "Reporte de Espacios");
    }

    // Metodo para generar reporte de historial
    private void generarReporteHistorial() {
        // Crear el diálogo para seleccionar las fechas
        DialogoFecha dialogoFecha = new DialogoFecha();
        LocalDate[] fechas = dialogoFecha.mostrarDialogo();

        // Verificar que se hayan seleccionado las fechas
        if (fechas != null) {
            LocalDate fechaInicio = fechas[0];
            LocalDate fechaFin = fechas[1];

            // Generar el reporte
            Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteHistorial(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());

            // Mostrar el reporte
            mostrarReporte(reporte, "Reporte de Historial");
        }
    }

    // Metodo para generar reporte de estadisticas
    private void generarReporteEstadisticas() {
        // Crear el diálogo para seleccionar las fechas
        DialogoFecha dialogoFecha = new DialogoFecha();
        LocalDate[] fechas = dialogoFecha.mostrarDialogo();

        if (fechas != null) {
            LocalDate fechaInicio = fechas[0];
            LocalDate fechaFin = fechas[1];

            // Generar el reporte
            Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteEstadisticas(fechaInicio, fechaFin,
                    sistemaParqueo.getGestorEspacios().getEspacios(),
                    sistemaParqueo.getGestorReservas().getReservas());

            // Mostrar el reporte
            mostrarReporte(reporte, "Reporte de Estadísticas");
        }
    }

    // Metodo para mostrar el reporte
    private void mostrarReporte(Reporte reporte, String titulo) {
        // Obtener la fecha de inicio y fin
        LocalDate fechaInicio = LocalDate.now().minusMonths(1);
        LocalDate fechaFin = LocalDate.now();
        // Generar el contenido del reporte
        String contenidoReporte = reporte.generarReporte(fechaInicio, fechaFin);

        try {
            // Generar el nombre del archivo
            String nombreArchivo = titulo.toLowerCase().replace(" ", "_") + ".pdf";
            // Generar el PDF
            GeneradorPDF.generarPDF(nombreArchivo, contenidoReporte);
            // Mostrar el mensaje de confirmacion
            JOptionPane.showMessageDialog(vista,
                    "Reporte generado con éxito.\nArchivo: " + nombreArchivo,
                    "Reporte Generado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista,
                    "Error al generar el PDF: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            // Si falla la generacion del PDF, mostramos el reporte en un diálogo
            JOptionPane.showMessageDialog(vista, contenidoReporte, titulo, JOptionPane.PLAIN_MESSAGE);
        }
    }
}
