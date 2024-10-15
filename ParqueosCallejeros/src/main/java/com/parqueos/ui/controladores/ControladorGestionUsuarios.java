package com.parqueos.ui.controladores;

import java.awt.Frame;
import java.awt.Window;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.DialogoUsuario;
import com.parqueos.ui.vistas.VistaGestionUsuarios;

public class ControladorGestionUsuarios {
    private final VistaGestionUsuarios vista;
    private final SistemaParqueo sistemaParqueo;
    private final String token;

    public ControladorGestionUsuarios(VistaGestionUsuarios vista, SistemaParqueo sistemaParqueo, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.token = token;
        inicializar();
    }

    private void inicializar() {
        cargarUsuarios();
        vista.getBtnAgregar().addActionListener(e -> agregarUsuario());
        vista.getBtnEditar().addActionListener(e -> editarUsuario());
        vista.getBtnEliminar().addActionListener(e -> eliminarUsuario());
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = sistemaParqueo.getGestorUsuarios().getUsuarios();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellidos");
        modelo.addColumn("Email");
        modelo.addColumn("Tipo");

        for (Usuario usuario : usuarios) {
            modelo.addRow(new Object[]{
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
            });
        }

        vista.getTblUsuarios().setModel(modelo);
    }

    private void agregarUsuario() {
        Window window = SwingUtilities.getWindowAncestor(vista);
        Frame frame = window instanceof Frame ? (Frame) window : null;
        DialogoUsuario dialogo = new DialogoUsuario(frame, null);
        dialogo.setVisible(true);
        if (dialogo.getIdUsuario() != null) {
            try {
                Usuario nuevoUsuario = sistemaParqueo.getGestorUsuarios().crearUsuario(
                    dialogo.getNombre(), dialogo.getApellidos(), Integer.parseInt(dialogo.getTelefono()),
                    dialogo.getEmail(), dialogo.getDireccion(), dialogo.getIdUsuario(), dialogo.getPin(),
                    dialogo.getTipoUsuario(), dialogo.getNumeroTarjeta(), dialogo.getFechaVencimiento(),
                    dialogo.getCodigoValidacion(), dialogo.getTerminalId(), dialogo.getVehiculos()
                );
                cargarUsuarios();
                JOptionPane.showMessageDialog(vista, "Usuario agregado con éxito.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, "Error al agregar usuario: " + e.getMessage());
            }
        }
    }

    private void editarUsuario() {
        int filaSeleccionada = vista.getTblUsuarios().getSelectedRow();
        if (filaSeleccionada >= 0) {
            String id = (String) vista.getTblUsuarios().getValueAt(filaSeleccionada, 0);
            Usuario usuario = sistemaParqueo.getGestorUsuarios().buscarUsuario(id);
            if (usuario != null) {
                Window window = SwingUtilities.getWindowAncestor(vista);
                Frame frame = window instanceof Frame ? (Frame) window : null;
                DialogoUsuario dialogo = new DialogoUsuario(frame, usuario);
                dialogo.setVisible(true);
                if (dialogo.getIdUsuario() != null) {
                    try {
                        Usuario usuarioActualizado = sistemaParqueo.getGestorUsuarios().actualizarUsuario(
                            id, dialogo.getNombre(), dialogo.getApellidos(), Integer.parseInt(dialogo.getTelefono()),
                            dialogo.getEmail(), dialogo.getDireccion(), dialogo.getIdUsuario(), dialogo.getPin(),
                            dialogo.getTipoUsuario(), dialogo.getNumeroTarjeta(), dialogo.getFechaVencimiento(),
                            dialogo.getCodigoValidacion(), dialogo.getTerminalId(), dialogo.getVehiculos()
                        );
                        cargarUsuarios();
                        JOptionPane.showMessageDialog(vista, "Usuario actualizado con éxito.");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(vista, "Error al actualizar usuario: " + e.getMessage());
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un usuario para editar.");
        }
    }

    private void eliminarUsuario() {
        int filaSeleccionada = vista.getTblUsuarios().getSelectedRow();
        if (filaSeleccionada >= 0) {
            String id = (String) vista.getTblUsuarios().getValueAt(filaSeleccionada, 0);
            int confirmacion = JOptionPane.showConfirmDialog(vista, 
                "¿Está seguro de que desea eliminar este usuario?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    sistemaParqueo.getGestorUsuarios().eliminarUsuario(id);
                    cargarUsuarios();
                    JOptionPane.showMessageDialog(vista, "Usuario eliminado con éxito.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar usuario: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un usuario para eliminar.");
        }
    }
}