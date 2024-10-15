package com.parqueos.ui.vistas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaGestionUsuarios extends JDialog {
    private JTable tblUsuarios;
    private BotonPersonalizado btnAgregar;
    private BotonPersonalizado btnEditar;
    private BotonPersonalizado btnEliminar;
    private BotonPersonalizado btnCerrar;

    public VistaGestionUsuarios(JFrame parent) {
        super(parent, "Gestión de Usuarios", true);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        PanelPersonalizado panelPrincipal = new PanelPersonalizado();
        panelPrincipal.setLayout(new BorderLayout());

        // Tabla de usuarios
        tblUsuarios = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblUsuarios);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAgregar = new BotonPersonalizado("Agregar Usuario");
        btnEditar = new BotonPersonalizado("Editar Usuario");
        btnEliminar = new BotonPersonalizado("Eliminar Usuario");
        btnCerrar = new BotonPersonalizado("Cerrar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        add(panelPrincipal);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    // Getters para los componentes
    public JTable getTblUsuarios() { return tblUsuarios; }
    public BotonPersonalizado getBtnAgregar() { return btnAgregar; }
    public BotonPersonalizado getBtnEditar() { return btnEditar; }
    public BotonPersonalizado getBtnEliminar() { return btnEliminar; }
    public BotonPersonalizado getBtnCerrar() { return btnCerrar; }
}