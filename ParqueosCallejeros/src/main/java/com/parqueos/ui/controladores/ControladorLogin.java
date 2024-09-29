package com.parqueos.ui.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.parqueos.servicios.AuthService;
import com.parqueos.ui.vistas.VistaLogin;

public class ControladorLogin extends ControladorBase {
    private VistaLogin vista;
    private AuthService authService;

    public ControladorLogin(VistaLogin vista, AuthService authService) {
        this.vista = vista;
        this.authService = authService;
        inicializar();
    }

    @Override
    protected void inicializar() {
        vista.getBotonLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = vista.getCampoUsuario().getText();
                String contrasena = new String(vista.getCampoContrasena().getPassword());
                
                try {
                    String token = authService.iniciarSesion(usuario, contrasena);
                    // Falta la logica para cambiar a la vista principal
                    System.out.println("Inicio de sesión exitoso. Token: " + token);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Error de inicio de sesión: " + ex.getMessage());
                }
            }
        });
    }
}