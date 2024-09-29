package com.parqueos.ui.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.parqueos.reportes.ReporteFactory;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaAdministrador;

public class ControladorAdministrador extends ControladorBase {
    private VistaAdministrador vista;
    private SistemaParqueo sistemaParqueo;

    public ControladorAdministrador(VistaAdministrador vista, SistemaParqueo sistemaParqueo) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        inicializar();
    }

    @Override
    protected void inicializar() {
        vista.getBtnConfigurarParqueo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Falta la logica para abrir la ventana de configuracion de parqueo
            }
        });

        vista.getBtnGenerarReporteIngresos().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte(ReporteFactory.TipoReporte.INGRESOS);
            }
        });

        vista.getBtnGenerarReporteMultas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte(ReporteFactory.TipoReporte.MULTAS);
            }
        });

        vista.getBtnGenerarReporteEspacios().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte(ReporteFactory.TipoReporte.ESPACIOS);
            }
        });

        vista.getBtnGenerarReporteHistorial().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte(ReporteFactory.TipoReporte.HISTORIAL);
            }
        });

        vista.getBtnGenerarReporteEstadisticas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte(ReporteFactory.TipoReporte.ESTADISTICAS);
            }
        });
    }

    private void generarReporte(ReporteFactory.TipoReporte tipoReporte) {
        // Falta la logica para generar el reporte correspondiente
        // Utilizando el ReporteFactory y el SistemaParqueo
        System.out.println("Generando reporte de tipo: " + tipoReporte);
    }
}