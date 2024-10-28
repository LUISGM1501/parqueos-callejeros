package com.parqueos.ui.vistas;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.controladores.ControladorLogin;

import java.awt.Color;
import java.awt.Dimension;

// Clase abstracta para las vistas 
public abstract class VistaBase extends JFrame {
    protected BotonPersonalizado btnCerrarSesion;
    protected SistemaParqueo sistemaParqueo;
    protected String token;

    // Constructor 
    protected VistaBase(String titulo, SistemaParqueo sistemaParqueo, String token) {
        super(titulo);
        this.sistemaParqueo = sistemaParqueo;
        this.token = token;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
    }

    // Metodo para agregar el botón de cerrar sesión
    protected void agregarBotonCerrarSesion(JPanel panel) {
        btnCerrarSesion = new BotonPersonalizado("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(231, 76, 60));
        btnCerrarSesion.setForeground(Color.WHITE);
        panel.add(btnCerrarSesion);
        
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    // Metodo para cerrar la sesión
    protected void cerrarSesion() {
        sistemaParqueo.cerrarSesion(token);
        this.dispose();
        VistaLogin vistaLogin = new VistaLogin(sistemaParqueo);
        new ControladorLogin(vistaLogin, sistemaParqueo.getAuthService(), sistemaParqueo);
        vistaLogin.setVisible(true);
    }

    // Getter para el botón de cerrar sesión
    public BotonPersonalizado getBtnCerrarSesion() {
        return btnCerrarSesion;
    }

    // Metodo para inicializar los componentes
    public abstract void inicializarComponentes();
}