package com.parqueos.ui.componentes;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

public class BotonPersonalizado extends JButton {
    public BotonPersonalizado(String texto) {
        super(texto);
        setBackground(new Color(0, 123, 255));
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 14));
        setFocusPainted(false);
        setBorderPainted(false);
    }
}