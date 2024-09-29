package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

import javax.swing.*;
import java.awt.*;

public class VistaInspector extends VistaBase {
    private BotonPersonalizado btnRevisarParqueo;
    private BotonPersonalizado btnGenerarMulta;
    private BotonPersonalizado btnVerReporteEspacios;
    private BotonPersonalizado btnVerReporteMultas;
    private JTextField txtEspacio;

    public VistaInspector() {
        super("Panel de Inspector");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        txtEspacio = new JTextField();
        btnRevisarParqueo = new BotonPersonalizado("Revisar Parqueo");
        btnGenerarMulta = new BotonPersonalizado("Generar Multa");
        btnVerReporteEspacios = new BotonPersonalizado("Ver Reporte de Espacios");
        btnVerReporteMultas = new BotonPersonalizado("Ver Reporte de Multas");

        panel.add(new JLabel("Número de Espacio:"));
        panel.add(txtEspacio);
        panel.add(btnRevisarParqueo);
        panel.add(btnGenerarMulta);
        panel.add(btnVerReporteEspacios);
        panel.add(btnVerReporteMultas);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para los componentes
    public BotonPersonalizado getBtnRevisarParqueo() { return btnRevisarParqueo; }
    public BotonPersonalizado getBtnGenerarMulta() { return btnGenerarMulta; }
    public BotonPersonalizado getBtnVerReporteEspacios() { return btnVerReporteEspacios; }
    public BotonPersonalizado getBtnVerReporteMultas() { return btnVerReporteMultas; }
    public String getEspacioIngresado() { return txtEspacio.getText(); }
}