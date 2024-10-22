package com.parqueos.ui.vistas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaGestionarEspacios extends JDialog {
    private JRadioButton rdbUnEspacio;
    private JRadioButton rdbVariosEspacios;
    private JTextField txtNumeroParqueo;
    private JTextField txtLimiteEspacios;
    private BotonPersonalizado btnAgregar;
    private BotonPersonalizado btnEliminar;
    private BotonPersonalizado btnCancelar;
    private JTable tblEspacios;  // Tabla para mostrar los espacios

    public VistaGestionarEspacios(JFrame parent) {
        super(parent, "Gestionar Espacios", true);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        PanelPersonalizado panel = new PanelPersonalizado();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tabla para mostrar los espacios - se coloca en la parte superior
        tblEspacios = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblEspacios);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(scrollPane, gbc);

        // Tipo de espacio (RadioButtons)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0;  // Resetear el peso para los siguientes componentes
        panel.add(new JLabel("Seleccionar tipo de espacio:"), gbc);
        
        rdbUnEspacio = new JRadioButton("Espacio específico");
        rdbVariosEspacios = new JRadioButton("Varios espacios");
        ButtonGroup grupoSeleccion = new ButtonGroup();
        grupoSeleccion.add(rdbUnEspacio);
        grupoSeleccion.add(rdbVariosEspacios);
                
        gbc.gridx = 1;
        panel.add(rdbUnEspacio, gbc);
        gbc.gridx = 2;
        panel.add(rdbVariosEspacios, gbc);

        // Campo para número de parqueo inicial
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Número de Parqueo:"), gbc);
        txtNumeroParqueo = new JTextField(10);
        txtNumeroParqueo.setToolTipText("Ingrese número de parqueo (1-5 caracteres)");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(txtNumeroParqueo, gbc);

        // Campo para el límite de espacios (visible solo si se selecciona "Varios espacios")
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Hasta número:"), gbc);
        txtLimiteEspacios = new JTextField(10);
        txtLimiteEspacios.setVisible(true);  // Visible si "Varios espacios" está seleccionado
        txtLimiteEspacios.setToolTipText("Ingrese número de parqueo (1-5 caracteres)");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(txtLimiteEspacios, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAgregar = new BotonPersonalizado("Agregar");
        btnEliminar = new BotonPersonalizado("Eliminar");
        btnCancelar = new BotonPersonalizado("Cancelar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(panelBotones, gbc);

        // Lógica para mostrar/ocultar campo de límite de espacios
        rdbUnEspacio.addActionListener(e -> txtLimiteEspacios.setVisible(false));
        rdbVariosEspacios.addActionListener(e -> txtLimiteEspacios.setVisible(true));

        // Configurar ventana
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    // Getters para los componentes
    public JRadioButton getRdbUnEspacio() { return rdbUnEspacio; }
    public JRadioButton getRdbVariosEspacios() { return rdbVariosEspacios; }
    public JTextField getTxtNumeroParqueo() { return txtNumeroParqueo; }
    public JTextField getTxtLimiteEspacios() { return txtLimiteEspacios; }
    public BotonPersonalizado getBtnAgregar() { return btnAgregar; }
    public BotonPersonalizado getBtnEliminar() { return btnEliminar; }
    public BotonPersonalizado getBtnCancelar() { return btnCancelar; }
    public JTable getTblEspacios() { return tblEspacios; }  // Nuevo getter para la tabla
}
