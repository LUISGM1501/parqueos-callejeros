package com.parqueos.ui.vistas;

import javax.swing.*;
import java.awt.*;

import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaInspector extends VistaBase {
    private JTextField txtEspacio;
    private JTextField txtPlaca;
    private BotonPersonalizado btnRevisarParqueo;
    private BotonPersonalizado btnGenerarMulta;
    private BotonPersonalizado btnVerReporteEspacios;
    private BotonPersonalizado btnVerReporteMultas;
    private JTextArea txtResultadoRevision;
    private JTable tblMultasGeneradas;

    public VistaInspector(SistemaParqueo sistemaParqueo, String token) {
        super("Panel de Inspector", sistemaParqueo, token);
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(new Color(41, 128, 185));
        JLabel lblTitulo = new JLabel("Panel de Inspector");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel principal
        PanelPersonalizado panelPrincipal = new PanelPersonalizado();
        panelPrincipal.setLayout(new GridBagLayout());
        add(panelPrincipal, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Agregar el botón de cerrar sesión
        JPanel panelCerrarSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        agregarBotonCerrarSesion(panelCerrarSesion);
        add(panelCerrarSesion, BorderLayout.SOUTH);

        // Panel de Revisión de Parqueo
        JPanel panelRevision = crearPanelSeccion("Revisión de Parqueo");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        panelPrincipal.add(panelRevision, gbc);

        // Panel de Resultados y Multas
        JPanel panelResultados = crearPanelSeccion("Resultados y Multas");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        panelPrincipal.add(panelResultados, gbc);

        // Panel de Reportes
        JPanel panelReportes = crearPanelSeccion("Reportes");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panelPrincipal.add(panelReportes, gbc);

        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2), titulo));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        if (titulo.equals("Revisión de Parqueo")) {
            txtEspacio = new JTextField(15);
            txtPlaca = new JTextField(15);
            btnRevisarParqueo = new BotonPersonalizado("Revisar Parqueo");
            btnGenerarMulta = new BotonPersonalizado("Generar Multa");

            panel.add(new JLabel("Número de Espacio:"), gbc);
            panel.add(txtEspacio, gbc);
            panel.add(new JLabel("Placa del Vehículo:"), gbc);
            panel.add(txtPlaca, gbc);
            panel.add(btnRevisarParqueo, gbc);
            panel.add(btnGenerarMulta, gbc);
        } else if (titulo.equals("Resultados y Multas")) {
            txtResultadoRevision = new JTextArea(5, 20);
            txtResultadoRevision.setEditable(false);
            JScrollPane scrollResultado = new JScrollPane(txtResultadoRevision);
            tblMultasGeneradas = new JTable();
            JScrollPane scrollTabla = new JScrollPane(tblMultasGeneradas);

            gbc.weighty = 1.0;
            panel.add(new JLabel("Resultado de la revisión:"), gbc);
            panel.add(scrollResultado, gbc);
            panel.add(new JLabel("Multas Generadas:"), gbc);
            panel.add(scrollTabla, gbc);
        } else if (titulo.equals("Reportes")) {
            btnVerReporteEspacios = new BotonPersonalizado("Ver Reporte de Espacios");
            btnVerReporteMultas = new BotonPersonalizado("Ver Reporte de Multas");

            panel.add(btnVerReporteEspacios, gbc);
            panel.add(btnVerReporteMultas, gbc);
        }

        return panel;
    }

    // Getters
    public JTextField getTxtEspacio() { return txtEspacio; }
    public JTextField getTxtPlaca() { return txtPlaca; }
    public BotonPersonalizado getBtnRevisarParqueo() { return btnRevisarParqueo; }
    public BotonPersonalizado getBtnGenerarMulta() { return btnGenerarMulta; }
    public BotonPersonalizado getBtnVerReporteEspacios() { return btnVerReporteEspacios; }
    public BotonPersonalizado getBtnVerReporteMultas() { return btnVerReporteMultas; }
    public JTextArea getTxtResultadoRevision() { return txtResultadoRevision; }
    public JTable getTblMultasGeneradas() { return tblMultasGeneradas; }

    public void setResultadoRevision(String resultado) {
        txtResultadoRevision.setText(resultado);
    }
}