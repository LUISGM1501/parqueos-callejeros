package com.parqueos.ui.vistas;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaAdministrador extends VistaBase {
    private BotonPersonalizado btnConfigurarParqueo;
    private BotonPersonalizado btnGestionarUsuarios;
    private BotonPersonalizado btnGestionarEspacios;
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
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        btnConfigurarParqueo = new BotonPersonalizado("Configurar Parqueo");
        btnGestionarUsuarios = new BotonPersonalizado("Gestionar Usuarios");
        btnGestionarEspacios = new BotonPersonalizado("Gestionar Espacios");
        btnGenerarReporteIngresos = new BotonPersonalizado("Reporte de Ingresos");
        btnGenerarReporteMultas = new BotonPersonalizado("Reporte de Multas");
        btnGenerarReporteEspacios = new BotonPersonalizado("Reporte de Espacios");
        btnGenerarReporteHistorial = new BotonPersonalizado("Reporte de Historial");
        btnGenerarReporteEstadisticas = new BotonPersonalizado("Reporte de Estadísticas");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnConfigurarParqueo, gbc);

        gbc.gridx = 1;
        panel.add(btnGestionarUsuarios, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(btnGestionarEspacios, gbc);

        gbc.gridx = 1;
        panel.add(btnGenerarReporteIngresos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnGenerarReporteMultas, gbc);

        gbc.gridx = 1;
        panel.add(btnGenerarReporteEspacios, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(btnGenerarReporteHistorial, gbc);

        gbc.gridx = 1;
        panel.add(btnGenerarReporteEstadisticas, gbc);

        setPreferredSize(new Dimension(600, 400));
        pack();
        setLocationRelativeTo(null);
    }

    // Getters para los botones
    public BotonPersonalizado getBtnConfigurarParqueo() { return btnConfigurarParqueo; }
    public BotonPersonalizado getBtnGestionarUsuarios() { return btnGestionarUsuarios; }
    public BotonPersonalizado getBtnGestionarEspacios() { return btnGestionarEspacios; }
    public BotonPersonalizado getBtnGenerarReporteIngresos() { return btnGenerarReporteIngresos; }
    public BotonPersonalizado getBtnGenerarReporteMultas() { return btnGenerarReporteMultas; }
    public BotonPersonalizado getBtnGenerarReporteEspacios() { return btnGenerarReporteEspacios; }
    public BotonPersonalizado getBtnGenerarReporteHistorial() { return btnGenerarReporteHistorial; }
    public BotonPersonalizado getBtnGenerarReporteEstadisticas() { return btnGenerarReporteEstadisticas; }
}