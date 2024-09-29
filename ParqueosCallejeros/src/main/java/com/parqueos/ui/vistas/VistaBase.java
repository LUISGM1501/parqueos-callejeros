package com.parqueos.ui.vistas;

import javax.swing.JFrame;
import java.awt.Dimension;

public abstract class VistaBase extends JFrame {
    protected VistaBase(String titulo) {
        super(titulo);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
    }

    public abstract void inicializarComponentes();
}