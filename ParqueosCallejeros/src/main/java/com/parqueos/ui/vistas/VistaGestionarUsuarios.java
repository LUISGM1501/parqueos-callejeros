package com.parqueos.ui.vistas;

//importaciones
import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;

public class VistaGestionarUsuarios extends VistaBase {
    //Inicializar los botones de la vista
    
    private BotonPersonalizado btnUsuariosParqueo;
    private BotonPersonalizado btnAdmin;
    private BotonPersonalizado btnInspector;
    private BotonPersonalizado btnRegresar;
    
    public VistaGestionarUsuarios(){
        super("Gestionar Usuarios");
        inicializarComponentes();
    }
    
    
    @Override
    public void inicializarComponentes(){
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);
        
        btnUsuariosParqueo = new BotonPersonalizado("Usuarios");
        btnUsuariosParqueo.setBounds(300, 100, 200, 40);
        panel.add(btnUsuariosParqueo);
        
        btnAdmin = new BotonPersonalizado("Administradores");
        btnAdmin.setBounds(300, 150, 200, 40);
        panel.add(btnAdmin);
        
        btnInspector = new BotonPersonalizado("Inspectores");
        btnInspector.setBounds(300, 200, 200, 40);
        panel.add(btnInspector);
        
        btnRegresar = new BotonPersonalizado("Regresar");
        btnRegresar.setBounds(300, 250, 200, 40);
        panel.add(btnRegresar);

        pack();
        setLocationRelativeTo(null);
    }
    
    //GETTERS DE3 LOS BOTONES
    public BotonPersonalizado getBtnUsuariosParqueo() {
        return btnUsuariosParqueo;
    }

    public BotonPersonalizado getBtnAdmin() {
        return btnAdmin;
    }

    public BotonPersonalizado getBtnInspector() {
        return btnInspector;
    }

    public BotonPersonalizado getBtnRegresar() {
        return btnRegresar;
    }

}
