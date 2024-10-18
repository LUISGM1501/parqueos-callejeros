package com.parqueos.ui.componentes;

import java.awt.Color;

import javax.swing.JPanel;

// Clase para crear un panel personalizado
public class PanelPersonalizado extends JPanel {
    // Constructor para inicializar el panel
    public PanelPersonalizado() {
        // Setear el color de fondo
        setBackground(new Color(240, 240, 240));
        // Setear el layout
        // null para poder setear las coordenadas de los componentes
        setLayout(null);
    }
}