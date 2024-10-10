package com.parqueos.ui.vistas;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;
import com.parqueos.ui.controladores.ControladorLogin;
import com.parqueos.servicios.SistemaParqueo;

public class VistaLogin extends JFrame {
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private BotonPersonalizado botonLogin;
    private SistemaParqueo sistemaParqueo;
    private ControladorLogin controlador;

    public VistaLogin(SistemaParqueo sistemaParqueo) {
        super("Inicio de Sesión");
        this.sistemaParqueo = sistemaParqueo;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PanelPersonalizado panel = new PanelPersonalizado();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titulo
        JLabel labelTitulo = new JLabel("<html><h1><font color='#4a90e2'>Sistema de Parqueos</font></h1></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(labelTitulo, gbc);

        // Usuario
        JLabel labelUsuario = new JLabel("<html><font color='#333333'>Usuario:</font></html>");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelUsuario, gbc);

        campoUsuario = new JTextField(15);
        campoUsuario.setToolTipText("Ingrese su nombre de usuario");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(campoUsuario, gbc);

        // Contraseña
        JLabel labelContrasena = new JLabel("<html><font color='#333333'>Contraseña:</font></html>");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelContrasena, gbc);

        campoContrasena = new JPasswordField(15);
        campoContrasena.setToolTipText("Ingrese su contraseña");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(campoContrasena, gbc);

        // Login
        botonLogin = new BotonPersonalizado("Iniciar Sesión");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(botonLogin, gbc);

        setPreferredSize(new Dimension(400, 300));
        pack();
        setLocationRelativeTo(null);
    }

    public void setControlador(ControladorLogin controlador) {
        this.controlador = controlador;
        botonLogin.addActionListener(e -> controlador.iniciarSesion());
    }

    public JTextField getCampoUsuario() {
        return campoUsuario;
    }

    public JPasswordField getCampoContrasena() {
        return campoContrasena;
    }

    public BotonPersonalizado getBotonLogin() {
        return botonLogin;
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public SistemaParqueo getSistemaParqueo() {
        return sistemaParqueo;
    }
}