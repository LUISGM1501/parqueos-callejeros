package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaCrudAdministrador extends VistaBase {
    // Botones de la vista CRUD para administradores
    private BotonPersonalizado btnAgregarAdmin;
    private BotonPersonalizado btnConsultarAdmin;
    private BotonPersonalizado btnModificarAdmin;
    private BotonPersonalizado btnEliminarAdmin;
    private BotonPersonalizado btnRegresar;

    public VistaCrudAdministrador(){
        super("CRUD de Administradores");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes(){
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);

        btnAgregarAdmin = new BotonPersonalizado("Agregar Administrador");
        btnAgregarAdmin.setBounds(300, 100, 250, 40);
        panel.add(btnAgregarAdmin);

        btnConsultarAdmin = new BotonPersonalizado("Consultar Administrador");
        btnConsultarAdmin.setBounds(300, 150, 250, 40);
        panel.add(btnConsultarAdmin);

        btnModificarAdmin = new BotonPersonalizado("Modifcar Administrador");
        btnModificarAdmin.setBounds(300, 200, 250, 40);
        panel.add(btnModificarAdmin);

        btnEliminarAdmin = new BotonPersonalizado("Eliminar Administrador");
        btnEliminarAdmin.setBounds(300, 250, 250, 40);
        panel.add(btnEliminarAdmin);

        btnRegresar = new BotonPersonalizado("Regresar");
        btnRegresar.setBounds(300, 300, 250, 40);
        panel.add(btnRegresar);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para cada botón
    public BotonPersonalizado getBtnAgregarAdmin() {
        return btnAgregarAdmin;
    }

    public BotonPersonalizado getBtnModificarAdmin() {
        return btnModificarAdmin;
    }

    public BotonPersonalizado getBtnEliminarAdmin() {
        return btnEliminarAdmin;
    }

    public BotonPersonalizado getBtnConsultarAdmin() {
        return btnConsultarAdmin;
    }

    public BotonPersonalizado getBtnRegresar() {
        return btnRegresar;
    }
}
