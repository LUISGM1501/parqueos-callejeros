package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JLabel;

public class VistaLogin extends VistaBase {
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private BotonPersonalizado botonLogin;

    public VistaLogin() {
        super("Login");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);

        JLabel labelUsuario = new JLabel("Usuario:");
        labelUsuario.setBounds(50, 50, 100, 30);
        panel.add(labelUsuario);

        campoUsuario = new JTextField();
        campoUsuario.setBounds(150, 50, 200, 30);
        panel.add(campoUsuario);

        JLabel labelContrasena = new JLabel("Contraseña:");
        labelContrasena.setBounds(50, 100, 100, 30);
        panel.add(labelContrasena);

        campoContrasena = new JPasswordField();
        campoContrasena.setBounds(150, 100, 200, 30);
        panel.add(campoContrasena);

        botonLogin = new BotonPersonalizado("Iniciar Sesión");
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