package com.parqueos.ui.vistas;

import javax.swing.*;
import java.awt.*;

import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.componentes.BotonPersonalizado;

public class VistaAdministrador extends VistaBase {
    private BotonPersonalizado btnConfigurarParqueo;
    private BotonPersonalizado btnGestionarUsuarios;
    private BotonPersonalizado btnGestionarEspacios;
    private BotonPersonalizado btnGenerarReporteIngresos;
    private BotonPersonalizado btnGenerarReporteMultas;
    private BotonPersonalizado btnGenerarReporteEspacios;
    private BotonPersonalizado btnGenerarReporteHistorial;
    private BotonPersonalizado btnGenerarReporteEstadisticas;

    public VistaAdministrador(SistemaParqueo sistemaParqueo, String token) {
        super("Panel de Administrador", sistemaParqueo, token);
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(new Color(41, 128, 185));
        JLabel lblTitulo = new JLabel("Panel de Administrador");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(Color.WHITE);
        add(panelPrincipal, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Agregar el botón de cerrar sesión
        JPanel panelCerrarSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        agregarBotonCerrarSesion(panelCerrarSesion);
        add(panelCerrarSesion, BorderLayout.SOUTH);

        // Panel de Configuración y Gestión
        JPanel panelConfigGestion = crearPanelSeccion("Configuración y Gestión");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panelPrincipal.add(panelConfigGestion, gbc);

        // Panel de Reportes
        JPanel panelReportes = crearPanelSeccion("Reportes");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panelPrincipal.add(panelReportes, gbc);

        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2), titulo));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        if (titulo.equals("Configuración y Gestión")) {
            btnConfigurarParqueo = crearBoton("Configurar Parqueo");
            btnGestionarUsuarios = crearBoton("Gestionar Usuarios");
            btnGestionarEspacios = crearBoton("Gestionar Espacios");

            panel.add(btnConfigurarParqueo, gbc);
            panel.add(btnGestionarUsuarios, gbc);
            panel.add(btnGestionarEspacios, gbc);
        } else {
            btnGenerarReporteIngresos = crearBoton("Reporte de Ingresos");
            btnGenerarReporteMultas = crearBoton("Reporte de Multas");
            btnGenerarReporteEspacios = crearBoton("Reporte de Espacios");
            btnGenerarReporteHistorial = crearBoton("Reporte de Historial");
            btnGenerarReporteEstadisticas = crearBoton("Reporte de Estadísticas");

            panel.add(btnGenerarReporteIngresos, gbc);
            panel.add(btnGenerarReporteMultas, gbc);
            panel.add(btnGenerarReporteEspacios, gbc);
            panel.add(btnGenerarReporteHistorial, gbc);
            panel.add(btnGenerarReporteEstadisticas, gbc);
        }

        // Añadir un componente de relleno para empujar los botones hacia arriba
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private BotonPersonalizado crearBoton(String texto) {
        BotonPersonalizado boton = new BotonPersonalizado(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setPreferredSize(new Dimension(200, 40));
        return boton;
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