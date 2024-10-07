package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class VistaEliminarAdministrador extends VistaBase {
    // Campo de texto para el PIN y botones de eliminar y cancelar
    private JTextField txtPin;
    private BotonPersonalizado btnEliminar;
    private BotonPersonalizado btnCancelar;

    public VistaEliminarAdministrador(){
        super("Eliminar Administrador");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes(){
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);

        JLabel lblPin = new JLabel("PIN:");
        lblPin.setBounds(50, 50, 120, 30);
        panel.add(lblPin);

        txtPin = new JTextField();
        txtPin.setBounds(180, 50, 200, 30);
        panel.add(txtPin);

        btnEliminar = new BotonPersonalizado("Eliminar");
        btnEliminar.setBounds(50, 100, 150, 40);
        panel.add(btnEliminar);

        btnCancelar = new BotonPersonalizado("Cancelar");
        btnCancelar.setBounds(230, 100, 150, 40);
        panel.add(btnCancelar);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para cada componente
    public JTextField getTxtPin() {
        return txtPin;
    }

    public BotonPersonalizado getBtnEliminar() {
        return btnEliminar;
    }

    public BotonPersonalizado getBtnCancelar() {
        return btnCancelar;
    }
}
