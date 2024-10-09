package com.parqueos.ui.vistas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

    public VistaInspector() {
        super("Panel de Inspector");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        setLayout(new BorderLayout());

        PanelPersonalizado panelPrincipal = new PanelPersonalizado();
        panelPrincipal.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtEspacio = new JTextField(10);
        txtPlaca = new JTextField(10);
        btnRevisarParqueo = new BotonPersonalizado("Revisar Parqueo");
        btnGenerarMulta = new BotonPersonalizado("Generar Multa");
        btnVerReporteEspacios = new BotonPersonalizado("Ver Reporte de Espacios");
        btnVerReporteMultas = new BotonPersonalizado("Ver Reporte de Multas");
        txtResultadoRevision = new JTextArea(5, 30);
        txtResultadoRevision.setEditable(false);
        JScrollPane scrollResultado = new JScrollPane(txtResultadoRevision);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        panelPrincipal.add(new JLabel("Número de Espacio:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        panelPrincipal.add(txtEspacio, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panelPrincipal.add(new JLabel("Placa del Vehículo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        panelPrincipal.add(txtPlaca, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panelPrincipal.add(btnRevisarParqueo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        panelPrincipal.add(btnGenerarMulta, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        panelPrincipal.add(new JLabel("Resultado de la revisión:"), gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        panelPrincipal.add(scrollResultado, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        panelPrincipal.add(btnVerReporteEspacios, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        panelPrincipal.add(btnVerReporteMultas, gbc);

        add(panelPrincipal, BorderLayout.NORTH);

        tblMultasGeneradas = new JTable();
        JScrollPane scrollTabla = new JScrollPane(tblMultasGeneradas);
        add(scrollTabla, BorderLayout.CENTER);

        setPreferredSize(new Dimension(600, 500));
        pack();
        setLocationRelativeTo(null);
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