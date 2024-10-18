package com.parqueos.ui.componentes;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

// Clase para crear un boton personalizado
public class BotonPersonalizado extends JButton {
    // Constructor para inicializar el boton
    public BotonPersonalizado(String texto) {
        super(texto);
        // Setear el color de fondo
        setBackground(new Color(0, 123, 255));
        // Para el color de texto
        setForeground(Color.WHITE);
        // Setear la fuente
        setFont(new Font("Arial", Font.BOLD, 14));
        // No mostrar el borde cuando el boton esta enfocado
        setFocusPainted(false);
        // No mostrar el borde cuando el boton esta seleccionado
        setBorderPainted(false);
    }
}