package com.parqueos.ui;

import com.parqueos.servicios.AuthService;
import com.parqueos.ui.vistas.VistaLogin;
import com.parqueos.ui.controladores.ControladorLogin;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AuthService authService = new AuthService();
                VistaLogin vistaLogin = new VistaLogin();
                ControladorLogin controladorLogin = new ControladorLogin(vistaLogin, authService);
                vistaLogin.setVisible(true);
            }
        });
    }
}