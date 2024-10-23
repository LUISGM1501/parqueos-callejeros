package com.parqueos.ui.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import com.parqueos.servicios.SistemaParqueo;
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

    public VistaUsuarioParqueo(SistemaParqueo sistemaParqueo, String token) {
        super("Panel de Usuario de Parqueo", sistemaParqueo, token);
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(new Color(41, 128, 185));
        JLabel lblTitulo = new JLabel("Panel de Usuario de Parqueo");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
        
        // Agregar el botón de cerrar sesión
        JPanel panelCerrarSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        agregarBotonCerrarSesion(panelCerrarSesion);
        add(panelCerrarSesion, BorderLayout.SOUTH);

        // Panel de Parqueo
        tabbedPane.addTab("Parqueo", crearPanelParqueo());

        // Panel de Reservas Activas
        tabbedPane.addTab("Reservas Activas", crearPanelReservasActivas());

        // Panel de Historial
        tabbedPane.addTab("Historial", crearPanelHistorial());

        // Panel de Multas
        tabbedPane.addTab("Multas", crearPanelMultas());

        inicializarTablas();
        configurarEventos();
        agregarTooltips();

        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelParqueo() {
        PanelPersonalizado panel = new PanelPersonalizado();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbVehiculos = new JComboBox<>();
        txtEspacio = new JTextField(10);
        spnTiempo = new JSpinner(new SpinnerNumberModel(30, 30, 1440, 30));
        btnParquear = new BotonPersonalizado("Parquear");
        btnAgregarTiempo = new BotonPersonalizado("Agregar Tiempo");
        btnDesaparcar = new BotonPersonalizado("Desaparcar");
        btnVerEspaciosDisponibles = new BotonPersonalizado("Ver Espacios Disponibles");
        lblTiempoGuardado = new JLabel("Tiempo guardado: 0 minutos");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Vehículo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(cmbVehiculos, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Espacio:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(txtEspacio, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Tiempo (min):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(spnTiempo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(btnParquear, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(btnAgregarTiempo, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; panel.add(btnDesaparcar, gbc);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; panel.add(btnVerEspaciosDisponibles, gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; panel.add(lblTiempoGuardado, gbc);

        return panel;
    }

    private JPanel crearPanelReservasActivas() {
        PanelPersonalizado panel = new PanelPersonalizado();
        panel.setLayout(new BorderLayout());
        tblReservasActivas = new JTable();
        JScrollPane scrollReservas = new JScrollPane(tblReservasActivas);
        panel.add(scrollReservas, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelHistorial() {
        PanelPersonalizado panel = new PanelPersonalizado();
        panel.setLayout(new BorderLayout());
        btnVerHistorial = new BotonPersonalizado("Ver Historial");
        panel.add(btnVerHistorial, BorderLayout.NORTH);
        return panel;
    }

    private JPanel crearPanelMultas() {
        PanelPersonalizado panel = new PanelPersonalizado();
        panel.setLayout(new BorderLayout());
        tblMultas = new JTable();
        JScrollPane scrollMultas = new JScrollPane(tblMultas);
        panel.add(scrollMultas, BorderLayout.CENTER);
        
        JPanel panelBotonesMultas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVerMultas = new BotonPersonalizado("Actualizar Multas");
        btnPagarMulta = new BotonPersonalizado("Pagar Multa Seleccionada");
        panelBotonesMultas.add(btnVerMultas);
        panelBotonesMultas.add(btnPagarMulta);
        panel.add(panelBotonesMultas, BorderLayout.SOUTH);
        
        return panel;
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

    private void inicializarTablas() {
        // Configurar tabla de reservas activas
        tblReservasActivas = new JTable();
        tblReservasActivas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReservasActivas.getTableHeader().setReorderingAllowed(false);
        
        // Configurar tabla de multas
        tblMultas = new JTable();
        tblMultas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMultas.getTableHeader().setReorderingAllowed(false);
    }

    private void configurarEventos() {
        // Agregar listeners para los cambios de selección en las tablas
        tblReservasActivas.getSelectionModel().addListSelectionListener(e -> {
            boolean reservaSeleccionada = tblReservasActivas.getSelectedRow() != -1;
            btnAgregarTiempo.setEnabled(reservaSeleccionada);
            btnDesaparcar.setEnabled(reservaSeleccionada);
        });

        tblMultas.getSelectionModel().addListSelectionListener(e -> {
            boolean multaSeleccionada = tblMultas.getSelectedRow() != -1;
            btnPagarMulta.setEnabled(multaSeleccionada);
        });

        // Deshabilitar botones inicialmente
        btnAgregarTiempo.setEnabled(false);
        btnDesaparcar.setEnabled(false);
        btnPagarMulta.setEnabled(false);
    }

    // Agregar tooltips a los componentes
    private void agregarTooltips() {
        cmbVehiculos.setToolTipText("Seleccione el vehículo que desea parquear");
        txtEspacio.setToolTipText("Ingrese el número del espacio de parqueo");
        spnTiempo.setToolTipText("Seleccione el tiempo en minutos");
        btnParquear.setToolTipText("Registrar el parqueo del vehículo");
        btnAgregarTiempo.setToolTipText("Agregar más tiempo a un parqueo activo");
        btnDesaparcar.setToolTipText("Finalizar un parqueo activo");
        btnVerEspaciosDisponibles.setToolTipText("Ver lista de espacios disponibles");
        btnVerHistorial.setToolTipText("Ver historial de parqueos");
        btnVerMultas.setToolTipText("Ver multas pendientes");
        btnPagarMulta.setToolTipText("Pagar la multa seleccionada");
    }
}