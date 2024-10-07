package com.parqueos.ui.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaGestionarUsuarios;
import com.parqueos.ui.vistas.*;

public class ControladorGestionarUsuarios extends ControladorBase {
    //inicializar vista y datos del sistema
    private VistaGestionarUsuarios vista;
    private SistemaParqueo sistemaParqueo;
    
    public ControladorGestionarUsuarios(VistaGestionarUsuarios vista, SistemaParqueo sistemaParqueo) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        inicializar();
    }
    
    @Override
    protected void inicializar() {
        //asignar acciones a los botones
        //BOTON USUARIOS
        vista.getBtnUsuariosParqueo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirGestionUsuariosParqueo();
                
            }
        });
        
        //BOTON ADMINS
        vista.getBtnAdmin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirGestionAdmin();
                // Aquí puedes añadir lógica específica para el botón btnAdmin
            }
        });
        //BOTON INSPECTORES
        vista.getBtnInspector().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirGestionInspector();
                // Aquí puedes añadir lógica específica para el botón btnInspector
            }
        });
        //boton para regresar
        vista.getBtnRegresar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Regresando a la vista anterior");
                vista.dispose();
                VistaAdministrador vistaAdmin = new VistaAdministrador();
                new ControladorAdministrador(vistaAdmin, sistemaParqueo);
                vistaAdmin.setVisible(true);
            }
        });
    }
    
    //abrir gestion usuarios parqueo
    private void abrirGestionUsuariosParqueo(){
        System.out.println("Abriendo vista de usuarios de parqueo");
    }
    
    private void abrirGestionAdmin(){
        System.out.println("Abriendo vista de administradores");
        vista.dispose();
        VistaCrudAdministrador vistaCrudAdmin = new VistaCrudAdministrador();
        new ControladorCrudAdministrador(vistaCrudAdmin, sistemaParqueo);
        vistaCrudAdmin.setVisible(true);
    }
    
    private void abrirGestionInspector(){
        System.out.println("Abriendo vista de inspectores");
    }

}
