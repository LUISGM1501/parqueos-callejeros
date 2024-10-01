package com.parqueos.ui.vistas;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaLogin extends VistaBase {
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private BotonPersonalizado botonLogin;

    public VistaLogin() {
        super("Login");
        inicializarComponentes();
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void inicializarComponentes() {
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);

        JLabel labelUsuario = new JLabel("User:");
        labelUsuario.setBounds(50, 50, 100, 30);
        panel.add(labelUsuario);

        campoUsuario = new JTextField();
        campoUsuario.setBounds(150, 50, 200, 30);
        panel.add(campoUsuario);

        JLabel labelContrasena = new JLabel("Password:");
        labelContrasena.setBounds(50, 100, 100, 30);
        panel.add(labelContrasena);

        campoContrasena = new JPasswordField();
        campoContrasena.setBounds(150, 100, 200, 30);
        panel.add(campoContrasena);

        botonLogin = new BotonPersonalizado("Login");
        botonLogin.setBounds(150, 150, 200, 40);
        panel.add(botonLogin);

        pack();
    }

    // Getters para los componentes
    public JTextField getCampoUsuario() {
        return campoUsuario;
    }

    public JPasswordField getCampoContrasena() {
        return campoContrasena;
    }

    public BotonPersonalizado getBotonLogin() {
        return botonLogin;
    }
}