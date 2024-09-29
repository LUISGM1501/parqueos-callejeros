package com.parqueos.ui.controladores;

import com.parqueos.ui.vistas.VistaInspector;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.parqueo.EspacioParqueo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorInspector extends ControladorBase {
    private VistaInspector vista;
    private SistemaParqueo sistemaParqueo;
    private Inspector inspector;

    public ControladorInspector(VistaInspector vista, SistemaParqueo sistemaParqueo, Inspector inspector) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.inspector = inspector;
        inicializar();
    }

    @Override
    protected void inicializar() {
        vista.getBtnRevisarParqueo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revisarParqueo();
            }
        });

        vista.getBtnGenerarMulta().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarMulta();
            }
        });

        vista.getBtnVerReporteEspacios().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verReporteEspacios();
            }
        });

        vista.getBtnVerReporteMultas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verReporteMultas();
            }
        });
    }

    private void revisarParqueo() {
        String numeroEspacio = vista.getEspacioIngresado();
        // Falta la logica para revisar el parqueo
        System.out.println("Revisando el espacio: " + numeroEspacio);
    }

    private void generarMulta() {
        String numeroEspacio = vista.getEspacioIngresado();
        // Falta la logica para generar una multa
        System.out.println("Generando multa para el espacio: " + numeroEspacio);
    }

    private void verReporteEspacios() {
        // Falta la logica para mostrar el reporte de espacios
        System.out.println("Mostrando reporte de espacios");
    }

    private void verReporteMultas() {
        // Falta la logica para mostrar el reporte de multas
        System.out.println("Mostrando reporte de multas");
    }
}