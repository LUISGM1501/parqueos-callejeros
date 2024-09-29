package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

import javax.swing.*;
import java.awt.*;

public class VistaAdministrador extends VistaBase {
    private BotonPersonalizado btnConfigurarParqueo;
    private BotonPersonalizado btnGenerarReporteIngresos;
    private BotonPersonalizado btnGenerarReporteMultas;
    private BotonPersonalizado btnGenerarReporteEspacios;
    private BotonPersonalizado btnGenerarReporteHistorial;
    private BotonPersonalizado btnGenerarReporteEstadisticas;

    public VistaAdministrador() {
        super("Panel de Administrador");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        btnConfigurarParqueo = new BotonPersonalizado("Configurar Parqueo");
        btnGenerarReporteIngresos = new BotonPersonalizado("Reporte de Ingresos");
        btnGenerarReporteMultas = new BotonPersonalizado("Reporte de Multas");
        btnGenerarReporteEspacios = new BotonPersonalizado("Reporte de Espacios");
        btnGenerarReporteHistorial = new BotonPersonalizado("Reporte de Historial");
        btnGenerarReporteEstadisticas = new BotonPersonalizado("Reporte de Estadísticas");

        panel.add(btnConfigurarParqueo);
        panel.add(btnGenerarReporteIngresos);
        panel.add(btnGenerarReporteMultas);
        panel.add(btnGenerarReporteEspacios);
        panel.add(btnGenerarReporteHistorial);
        panel.add(btnGenerarReporteEstadisticas);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para los botones
    public BotonPersonalizado getBtnConfigurarParqueo() { return btnConfigurarParqueo; }
    public BotonPersonalizado getBtnGenerarReporteIngresos() { return btnGenerarReporteIngresos; }
    public BotonPersonalizado getBtnGenerarReporteMultas() { return btnGenerarReporteMultas; }
    public BotonPersonalizado getBtnGenerarReporteEspacios() { return btnGenerarReporteEspacios; }
    public BotonPersonalizado getBtnGenerarReporteHistorial() { return btnGenerarReporteHistorial; }
    public BotonPersonalizado getBtnGenerarReporteEstadisticas() { return btnGenerarReporteEstadisticas; }
}