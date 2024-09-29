package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

import javax.swing.*;
import java.awt.*;

public class VistaUsuarioParqueo extends VistaBase {
    private BotonPersonalizado btnParquear;
    private BotonPersonalizado btnAgregarTiempo;
    private BotonPersonalizado btnDesaparcar;
    private BotonPersonalizado btnVerEspaciosDisponibles;
    private BotonPersonalizado btnVerHistorial;
    private BotonPersonalizado btnVerMultas;
    private JLabel lblTiempoGuardado;

    public VistaUsuarioParqueo() {
        super("Panel de Usuario de Parqueo");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        btnParquear = new BotonPersonalizado("Parquear");
        btnAgregarTiempo = new BotonPersonalizado("Agregar Tiempo");
        btnDesaparcar = new BotonPersonalizado("Desaparcar");
        btnVerEspaciosDisponibles = new BotonPersonalizado("Ver Espacios Disponibles");
        btnVerHistorial = new BotonPersonalizado("Ver Historial");
        btnVerMultas = new BotonPersonalizado("Ver Multas");
        lblTiempoGuardado = new JLabel("Tiempo guardado: 0 minutos");

        panel.add(btnParquear);
        panel.add(btnAgregarTiempo);
        panel.add(btnDesaparcar);
        panel.add(btnVerEspaciosDisponibles);
        panel.add(btnVerHistorial);
        panel.add(btnVerMultas);
        panel.add(lblTiempoGuardado);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para los componentes
    public BotonPersonalizado getBtnParquear() { return btnParquear; }
    public BotonPersonalizado getBtnAgregarTiempo() { return btnAgregarTiempo; }
    public BotonPersonalizado getBtnDesaparcar() { return btnDesaparcar; }
    public BotonPersonalizado getBtnVerEspaciosDisponibles() { return btnVerEspaciosDisponibles; }
    public BotonPersonalizado getBtnVerHistorial() { return btnVerHistorial; }
    public BotonPersonalizado getBtnVerMultas() { return btnVerMultas; }
    public void actualizarTiempoGuardado(int minutos) {
        lblTiempoGuardado.setText("Tiempo guardado: " + minutos + " minutos");
    }
}