package com.parqueos.ui.vistas;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.controladores.ControladorLogin;

import java.awt.Color;
import java.awt.Dimension;

public abstract class VistaBase extends JFrame {
    protected BotonPersonalizado btnCerrarSesion;
    protected SistemaParqueo sistemaParqueo;
    protected String token;

    protected VistaBase(String titulo, SistemaParqueo sistemaParqueo, String token) {
        super(titulo);
        this.sistemaParqueo = sistemaParqueo;
        this.token = token;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
    }

    protected void agregarBotonCerrarSesion(JPanel panel) {
        btnCerrarSesion = new BotonPersonalizado("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(231, 76, 60));
        btnCerrarSesion.setForeground(Color.WHITE);
        panel.add(btnCerrarSesion);
        
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    protected void cerrarSesion() {
        sistemaParqueo.cerrarSesion(token);
        this.dispose();
        VistaLogin vistaLogin = new VistaLogin(sistemaParqueo);
        new ControladorLogin(vistaLogin, sistemaParqueo.getAuthService(), sistemaParqueo);
        vistaLogin.setVisible(true);
    }

    public BotonPersonalizado getBtnCerrarSesion() {
        return btnCerrarSesion;
    }

    public abstract void inicializarComponentes();
}