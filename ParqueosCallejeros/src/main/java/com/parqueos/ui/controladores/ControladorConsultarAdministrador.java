package com.parqueos.ui.controladores;

import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Usuario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane; // Importar para mostrar mensajes


import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaConsultarAdministrador;
import com.parqueos.ui.vistas.VistaCrudAdministrador; // Importa la vista necesaria
import java.time.format.DateTimeFormatter;

public class ControladorConsultarAdministrador extends ControladorBase {
    // Inicializar vista y datos del sistema
    private VistaConsultarAdministrador vista;
    private SistemaParqueo sistemaParqueo;

    public ControladorConsultarAdministrador(VistaConsultarAdministrador vista, SistemaParqueo sistemaParqueo) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        inicializar();
    }

    @Override
    protected void inicializar() {
        // Asignar acciones a los botones

        // Botón Buscar
        vista.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = vista.getTxtId().getText();
                Usuario admin = sistemaParqueo.consultarUsuario(id);
                consultarAdministrador(admin);
                System.out.println("Imprimiendo info");
            }
        });

        // Botón Regresar
        vista.getBtnRegresar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Regresando a la vista anterior");
                vista.dispose();
                VistaCrudAdministrador vistaCrud = new VistaCrudAdministrador();
                new ControladorCrudAdministrador(vistaCrud, sistemaParqueo);
                vistaCrud.setVisible(true);
            }
        });
    }
    
    //Consultar Administradores
    // Consultar administrador
   


    // Consultar administrador por ID
    private void consultarAdministrador(Usuario admin) {
        String id = vista.getTxtId().getText();

        // Lógica para consultar el administrador en el sistemaParqueo
        if (admin != null) {
            // Llenar los campos de texto con la información del administrador
            vista.getTxtNombre().setText(admin.getNombre());
            vista.getTxtApellidos().setText(admin.getApellidos());
            vista.getTxtTelefono().setText(Integer.toString(admin.getTelefono()));
            vista.getTxtCorreo().setText(admin.getEmail());
            vista.getTxtDireccion().setText(admin.getDireccion());
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            vista.getTxtFechaIngreso().setText(admin.getFechaIngreso().format(formato));
            vista.getTxtPin().setText(admin.getPin());
        } else {
            // Mostrar un mensaje si el administrador no fue encontrado
            JOptionPane.showMessageDialog(vista, "Administrador no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
