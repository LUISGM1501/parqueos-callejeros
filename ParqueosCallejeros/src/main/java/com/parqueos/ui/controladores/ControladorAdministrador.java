package com.parqueos.ui.controladores;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JOptionPane;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.reportes.Reporte;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaAdministrador;
import com.parqueos.ui.vistas.VistaConfiguracionParqueo;
import com.parqueos.ui.vistas.VistaGestionarEspacios;
import java.util.ArrayList;
import java.util.List;
import com.parqueos.ui.vistas.VistaGestionUsuarios;
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
        VistaGestionarEspacios vistaGestEspacios = new VistaGestionarEspacios(vista);
        
        //Funcionalidad boton agregar
        vistaGestEspacios.getBtnAgregar().addActionListener(e->{
            int e1, e2;
            String txtEspacio1, txtEspacio2;
            EspacioParqueo espacio;
            List<Integer> listaNums = new ArrayList<>();
            if (vistaGestEspacios.getRdbUnEspacio().isSelected()){
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                //Validacion de existencia del espacio
                if (sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1) == null){
                    espacio = new EspacioParqueo(txtEspacio1);
                    sistemaParqueo.getGestorEspacios().agregarEspacio(espacio);
                    JOptionPane.showMessageDialog(vista, "Espacio agregado con éxito.");
                }else{
                    JOptionPane.showMessageDialog(vista, "El espacio " + txtEspacio1 + " ya existe.");
                }
                
            } else if(vistaGestEspacios.getRdbVariosEspacios().isSelected()){
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                txtEspacio2 = vistaGestEspacios.getTxtLimiteEspacios().getText();
                
                //longitud de los caracters de espacio
                int longitud = txtEspacio1.length();
                String formato = "%0" + Integer.toString(longitud) + "d";
                
                //Transformar strings a números
                e1 = Integer.valueOf(txtEspacio1);
                e2 = Integer.valueOf(txtEspacio2);
                
                //agregar espacios del rango convertidos en números
                for(int i = e1; i <= e2; i++){
                    listaNums.add(i);
                }
                //tranformar devuelta a espacios
                for (int num : listaNums){
                    txtEspacio1 = String.format(formato, num);
                    if (sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1) == null){
                        espacio = new EspacioParqueo(txtEspacio1);
                        sistemaParqueo.getGestorEspacios().agregarEspacio(espacio);                        
                    }else{
                        JOptionPane.showMessageDialog(vista, "El espacio " + txtEspacio1 + " ya existe.");
                    }                    
                } 
                JOptionPane.showMessageDialog(vista, "Espacios agregados con éxito.");
            }
        });
        
        //Funcionalidad boton eliminar
        vistaGestEspacios.getBtnEliminar().addActionListener(e ->{
            int e1, e2;
            String txtEspacio1, txtEspacio2;
            EspacioParqueo espacio;
            List<Integer> listaNums = new ArrayList<>();
            if (vistaGestEspacios.getRdbUnEspacio().isSelected()){
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();  
                espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1);
                //Validacion de existencia del espacio
                if (espacio != null){
                    if(!espacio.estaOcupado()){
                            sistemaParqueo.getGestorEspacios().eliminarEspacio(txtEspacio1);
                            JOptionPane.showMessageDialog(vista, "El espacio fue eliminado con éxito.");
                        }else{
                            JOptionPane.showMessageDialog(vista, "El espacio " + txtEspacio1 + " está ocupado.");
                        }                     
                }else{
                    JOptionPane.showMessageDialog(vista, "El espacio " + txtEspacio1 + " no existe.");
                }
                
            } else if(vistaGestEspacios.getRdbVariosEspacios().isSelected()){
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                txtEspacio2 = vistaGestEspacios.getTxtLimiteEspacios().getText();
                
                //longitud de los caracters de espacio
                int longitud = txtEspacio1.length();
                String formato = "%0" + Integer.toString(longitud) + "d";
                
                //Transformar strings a números
                e1 = Integer.valueOf(txtEspacio1);
                e2 = Integer.valueOf(txtEspacio2);
                
                //agregar espacios del rango convertidos en números
                for(int i = e1; i <= e2; i++){
                    listaNums.add(i);
                }
                //tranformar devuelta a espacios
                for (int num : listaNums){
                    txtEspacio1 = String.format(formato, num);
                    espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1);
                    if (espacio != null){
                        if(!espacio.estaOcupado()){
                            sistemaParqueo.getGestorEspacios().eliminarEspacio(txtEspacio1); 
                        }else{
                            JOptionPane.showMessageDialog(vista, "El espacio " + txtEspacio1 + " está ocupado.");
                        }                                                                    
                    }else{
                        JOptionPane.showMessageDialog(vista, "El espacio " + txtEspacio1 + " no existe.");
                    }                    
                } 
                JOptionPane.showMessageDialog(vista, "Espacios eliminados con éxito.");
            } else{
                JOptionPane.showMessageDialog(vista, "Seleccione una opción antes de iniciar.");
            }
        });
        
        //boton cancelar5 para salir
        vistaGestEspacios.getBtnCancelar().addActionListener(e -> vistaGestEspacios.dispose());
        
        vistaGestEspacios.setVisible(true);
    }

    // Metodo para generar reporte de ingresos
    private void generarReporteIngresos() {
        // Obtener la fecha de inicio y fin
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        // Generar el reporte
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteIngresos(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());
        // Mostrar el reporte
        mostrarReporte(reporte, "Reporte de Ingresos");
    }

    // Metodo para generar reporte de multas
    private void generarReporteMultas() {
        // Obtener la fecha de inicio y fin
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        // Generar el reporte
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteMultas(fechaInicio, fechaFin, sistemaParqueo.getGestorMultas().getMultas());
        // Mostrar el reporte
        mostrarReporte(reporte, "Reporte de Multas");
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
        // Obtener la fecha de inicio y fin
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        // Generar el reporte
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteHistorial(fechaInicio, fechaFin, sistemaParqueo.getGestorReservas().getReservas());
        // Mostrar el reporte
        mostrarReporte(reporte, "Reporte de Historial");
    }

    // Metodo para generar reporte de estadisticas
    private void generarReporteEstadisticas() {
        // Obtener la fecha de inicio y fin
        LocalDate fechaInicio = LocalDate.now().minusDays(30); // Último mes
        LocalDate fechaFin = LocalDate.now();
        // Generar el reporte
        Reporte reporte = sistemaParqueo.getGestorReportes().generarReporteEstadisticas(fechaInicio, fechaFin, 
            sistemaParqueo.getGestorEspacios().getEspacios(), 
            sistemaParqueo.getGestorReservas().getReservas());
        // Mostrar el reporte
        mostrarReporte(reporte, "Reporte de Estadísticas");
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