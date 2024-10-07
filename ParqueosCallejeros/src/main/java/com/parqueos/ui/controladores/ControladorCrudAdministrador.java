package com.parqueos.ui.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.*;

public class ControladorCrudAdministrador extends ControladorBase {
    // Inicializar la vista CRUD de administradores y el sistema de parqueo
    private VistaCrudAdministrador vista;
    private SistemaParqueo sistemaParqueo;

    public ControladorCrudAdministrador(VistaCrudAdministrador vista, SistemaParqueo sistemaParqueo) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        inicializar();
    }

    @Override
    protected void inicializar() {
        // Acción para el botón de agregar administrador
        vista.getBtnAgregarAdmin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirVistaAgregarAdministrador();
            }
        });

        // Acción para el botón de consultar administrador
        vista.getBtnConsultarAdmin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vista.dispose();
                abrirVistaConsultarAdministrador();
            }
        });

        // Acción para el botón de modificar administrador
        vista.getBtnModificarAdmin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirVistaModificarAdministrador();
            }
        });

        // Acción para el botón de eliminar administrador
        vista.getBtnEliminarAdmin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirVistaEliminarAdministrador();
            }
        });

        // Acción para el botón de regresar
        vista.getBtnRegresar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vista.dispose();
                VistaAdministrador vistaAdmin = new VistaAdministrador();
                new ControladorAdministrador(vistaAdmin, sistemaParqueo);
                vistaAdmin.setVisible(true);
            }
        });
    }

    // Métodos para abrir cada vista específica
    private void abrirVistaAgregarAdministrador() {
        VistaAgregarAdministrador vistaAgregar = new VistaAgregarAdministrador();
        //new ControladorAgregarAdministrador(vistaAgregar, sistemaParqueo);
        vistaAgregar.setVisible(true);
    }

    private void abrirVistaConsultarAdministrador() {
        VistaConsultarAdministrador vistaConsultar = new VistaConsultarAdministrador();
        new ControladorConsultarAdministrador(vistaConsultar, sistemaParqueo);
        vistaConsultar.setVisible(true);
    }

    private void abrirVistaModificarAdministrador() {
        VistaModificarAdministrador vistaModificar = new VistaModificarAdministrador();
        //new ControladorModificarAdministrador(vistaModificar, sistemaParqueo);
        vistaModificar.setVisible(true);
    }

    private void abrirVistaEliminarAdministrador() {
        VistaEliminarAdministrador vistaEliminar = new VistaEliminarAdministrador();
        //new ControladorEliminarAdministrador(vistaEliminar, sistemaParqueo);
        vistaEliminar.setVisible(true);
    }
}
