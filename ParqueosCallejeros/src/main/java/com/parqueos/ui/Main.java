package com.parqueos.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.parqueos.servicios.AuthService;
import com.parqueos.servicios.GestorNotificaciones;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.controladores.ControladorLogin;
import com.parqueos.ui.vistas.VistaLogin;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Iniciando aplicación");
                    GestorNotificaciones gestorNotificaciones = new GestorNotificaciones();
                    AuthService authService = new AuthService(gestorNotificaciones);
                    SistemaParqueo sistemaParqueo = new SistemaParqueo();
                    VistaLogin vistaLogin = new VistaLogin(sistemaParqueo);
                    ControladorLogin controladorLogin = new ControladorLogin(vistaLogin, authService, sistemaParqueo);
                    vistaLogin.setVisible(true);

                    // Mostrar informacion de usuarios de prueba
                    JOptionPane.showMessageDialog(null, 
                        "Usuarios de prueba:\n" +
                        "Administrador: admin / 1234\n" +
                        "Usuario: user / 1234\n" +
                        "Inspector: inspector / 1234",
                        "Información de Prueba",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Error al iniciar la aplicacion: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}