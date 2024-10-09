package com.parqueos.ui.vistas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaUsuarioParqueo extends VistaBase {
    private JTabbedPane tabbedPane;
    private BotonPersonalizado btnParquear;
    private BotonPersonalizado btnAgregarTiempo;
    private BotonPersonalizado btnDesaparcar;
    private BotonPersonalizado btnVerEspaciosDisponibles;
    private BotonPersonalizado btnVerHistorial;
    private BotonPersonalizado btnVerMultas;
    private BotonPersonalizado btnPagarMulta;
    private JLabel lblTiempoGuardado;
    private JTable tblReservasActivas;
    private JTable tblMultas;
    private JComboBox<String> cmbVehiculos;
    private JTextField txtEspacio;
    private JSpinner spnTiempo;

    public VistaUsuarioParqueo() {
        super("Panel de Usuario de Parqueo");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // Panel de Parqueo
        PanelPersonalizado panelParqueo = new PanelPersonalizado();
        panelParqueo.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        cmbVehiculos = new JComboBox<>();
        txtEspacio = new JTextField(10);
        spnTiempo = new JSpinner(new SpinnerNumberModel(30, 30, 1440, 30)); // 30 min a 24 horas, incrementos de 30 min
        btnParquear = new BotonPersonalizado("Parquear");
        btnAgregarTiempo = new BotonPersonalizado("Agregar Tiempo");
        btnDesaparcar = new BotonPersonalizado("Desaparcar");
        btnVerEspaciosDisponibles = new BotonPersonalizado("Ver Espacios Disponibles");
        lblTiempoGuardado = new JLabel("Tiempo guardado: 0 minutos");

        gbc.gridx = 0; gbc.gridy = 0; panelParqueo.add(new JLabel("Vehículo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panelParqueo.add(cmbVehiculos, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelParqueo.add(new JLabel("Espacio:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panelParqueo.add(txtEspacio, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelParqueo.add(new JLabel("Tiempo (min):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panelParqueo.add(spnTiempo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelParqueo.add(btnParquear, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panelParqueo.add(btnAgregarTiempo, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; panelParqueo.add(btnDesaparcar, gbc);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; panelParqueo.add(btnVerEspaciosDisponibles, gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; panelParqueo.add(lblTiempoGuardado, gbc);

        tabbedPane.addTab("Parqueo", panelParqueo);

        // Panel de Reservas Activas
        PanelPersonalizado panelReservas = new PanelPersonalizado();
        panelReservas.setLayout(new BorderLayout());
        tblReservasActivas = new JTable();
        JScrollPane scrollReservas = new JScrollPane(tblReservasActivas);
        panelReservas.add(scrollReservas, BorderLayout.CENTER);

        tabbedPane.addTab("Reservas Activas", panelReservas);

        // Panel de Historial
        PanelPersonalizado panelHistorial = new PanelPersonalizado();
        panelHistorial.setLayout(new BorderLayout());
        btnVerHistorial = new BotonPersonalizado("Ver Historial");
        panelHistorial.add(btnVerHistorial, BorderLayout.NORTH);

        tabbedPane.addTab("Historial", panelHistorial);

        // Panel de Multas
        PanelPersonalizado panelMultas = new PanelPersonalizado();
        panelMultas.setLayout(new BorderLayout());
        tblMultas = new JTable();
        JScrollPane scrollMultas = new JScrollPane(tblMultas);
        panelMultas.add(scrollMultas, BorderLayout.CENTER);
        
        JPanel panelBotonesMultas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVerMultas = new BotonPersonalizado("Actualizar Multas");
        btnPagarMulta = new BotonPersonalizado("Pagar Multa Seleccionada");
        panelBotonesMultas.add(btnVerMultas);
        panelBotonesMultas.add(btnPagarMulta);
        panelMultas.add(panelBotonesMultas, BorderLayout.SOUTH);

        tabbedPane.addTab("Multas", panelMultas);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para los componentes
    public BotonPersonalizado getBtnParquear() { return btnParquear; }
    public BotonPersonalizado getBtnAgregarTiempo() { return btnAgregarTiempo; }
    public BotonPersonalizado getBtnDesaparcar() { return btnDesaparcar; }
    public BotonPersonalizado getBtnVerEspaciosDisponibles() { return btnVerEspaciosDisponibles; }
    public BotonPersonalizado getBtnVerHistorial() { return btnVerHistorial; }
    public BotonPersonalizado getBtnVerMultas() { return btnVerMultas; }
    public BotonPersonalizado getBtnPagarMulta() { return btnPagarMulta; }
    public JTable getTblReservasActivas() { return tblReservasActivas; }
    public JTable getTblMultas() { return tblMultas; }
    public JComboBox<String> getCmbVehiculos() { return cmbVehiculos; }
    public JTextField getTxtEspacio() { return txtEspacio; }
    public JSpinner getSpnTiempo() { return spnTiempo; }
    
    public void actualizarTiempoGuardado(int minutos) {
        lblTiempoGuardado.setText("Tiempo guardado: " + minutos + " minutos");
    }
}