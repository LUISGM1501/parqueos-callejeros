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
        LocalTime horaInicio = nuevaConfiguracion.getHorarioInicio();
        LocalTime horaFin = nuevaConfiguracion.getHorarioFin();

        // Verificación de que la hora de inicio no sea posterior a la hora final
        if (horaInicio.isAfter(horaFin)) {
            JOptionPane.showMessageDialog(vista, "La hora de inicio no puede ser posterior a la hora de finalización.", "Error de configuración", JOptionPane.WARNING_MESSAGE);
        } else {
            sistemaParqueo.setConfiguracion(token, nuevaConfiguracion);
            JOptionPane.showMessageDialog(vista, "Configuración actualizada con éxito.");
            vistaConfig.dispose();
        }
    });

        vistaConfig.getBtnCancelar().addActionListener(e -> vistaConfig.dispose());

        vistaConfig.setVisible(true);
    }

    private void gestionarUsuarios() {
        // Aquí deberías abrir una nueva vista para gestionar usuarios
        JOptionPane.showMessageDialog(vista, "Funcionalidad de gestión de usuarios no implementada aún.");
    }

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