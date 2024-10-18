package com.parqueos.ui.controladores;

import java.awt.Frame;
import java.awt.Window;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.DialogoUsuario;
import com.parqueos.ui.vistas.VistaGestionUsuarios;
import com.parqueos.util.Validador;

// Controlador para la gestion de usuarios
public class ControladorGestionUsuarios {
    private static final Logger LOGGER = Logger.getLogger(ControladorGestionUsuarios.class.getName());

    private final VistaGestionUsuarios vista;
    private final SistemaParqueo sistemaParqueo;
    private final String token;

    // Constructor para inicializar el controlador
    public ControladorGestionUsuarios(VistaGestionUsuarios vista, SistemaParqueo sistemaParqueo, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.token = token;
        inicializar();
    }

    // Metodo para inicializar el controlador
    private void inicializar() {
        cargarUsuarios();
        vista.getBtnAgregar().addActionListener(e -> agregarUsuario());
        vista.getBtnEditar().addActionListener(e -> editarUsuario());
        vista.getBtnEliminar().addActionListener(e -> eliminarUsuario());
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
    }

    // Metodo para cargar los usuarios en la tabla
    private void cargarUsuarios() {
        // Obtener los usuarios del sistema
        List<Usuario> usuarios = sistemaParqueo.getGestorUsuarios().getUsuarios();
        // Crear el modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel();
        // Agregar las columnas
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellidos");
        modelo.addColumn("Email");
        modelo.addColumn("Tipo");

        // Agregar los usuarios al modelo
        for (Usuario usuario : usuarios) {
            modelo.addRow(new Object[]{
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
            });
        }

        // Setear el modelo de la tabla
        vista.getTblUsuarios().setModel(modelo);
        // Log de la cantidad de usuarios cargados
        LOGGER.info("Usuarios cargados en la tabla: " + usuarios.size());
    }

    // Metodo para agregar un usuario
    private void agregarUsuario() {
        // Obtener la ventana padre
        Window window = SwingUtilities.getWindowAncestor(vista);
        // Obtener el frame 
        Frame frame = window instanceof Frame ? (Frame) window : null;
        // Crear el dialogo
        DialogoUsuario dialogo = new DialogoUsuario(frame, null);
        // Mostrar el dialogo
        dialogo.setVisible(true);
        
        // Si el id del usuario no es null
        if (dialogo.getIdUsuario() != null) {
            try {
                // Validar los datos del usuario
                if (!validarDatosUsuario(dialogo)) {
                    return;
                }
                
                // Crear el nuevo usuario
                Usuario nuevoUsuario = sistemaParqueo.getGestorUsuarios().crearUsuario(
                    dialogo.getNombre(),
                    dialogo.getApellidos(),
                    Integer.parseInt(dialogo.getTelefono()),
                    dialogo.getEmail(),
                    dialogo.getDireccion(),
                    dialogo.getIdUsuario(),
                    dialogo.getPin(),
                    dialogo.getTipoUsuario(),
                    dialogo.getNumeroTarjeta(),
                    dialogo.getFechaVencimiento(),
                    dialogo.getCodigoValidacion(),
                    dialogo.getTerminalId(),
                    dialogo.getVehiculos()
                );
                
                // Cargar los usuarios
                cargarUsuarios();
                // Mostrar el mensaje de confirmacion
                JOptionPane.showMessageDialog(vista, "Usuario agregado con éxito.");
                // Log del usuario agregado
                LOGGER.info("Usuario agregado: " + nuevoUsuario.getId());
            } catch (Exception e) {
                // Log del error
                LOGGER.log(Level.SEVERE, "Error al agregar usuario", e);
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, "Error al agregar usuario: " + e.getMessage());
            }
        }
    }

    // Metodo para editar un usuario
    private void editarUsuario() {
        // Obtener la fila seleccionada
        int filaSeleccionada = vista.getTblUsuarios().getSelectedRow();
        // Si la fila seleccionada es mayor o igual a 0
        if (filaSeleccionada >= 0) {
            // Obtener el id del usuario
            String id = (String) vista.getTblUsuarios().getValueAt(filaSeleccionada, 0);
            // Buscar el usuario
            Usuario usuario = sistemaParqueo.getGestorUsuarios().buscarUsuario(id);
            // Si el usuario no es null
            if (usuario != null) {
                // Obtener la ventana padre
                Window window = SwingUtilities.getWindowAncestor(vista);
                // Obtener el frame
                Frame frame = window instanceof Frame ? (Frame) window : null;
                // Crear el dialogo
                DialogoUsuario dialogo = new DialogoUsuario(frame, usuario);
                // Mostrar el dialogo
                dialogo.setVisible(true);
                
                // Si el id del usuario no es null
                if (dialogo.getIdUsuario() != null) {
                    try {
                        // Validar los datos del usuario
                        if (!validarDatosUsuario(dialogo)) {
                            return;
                        }
                        
                        // Actualizar el usuario
                        Usuario usuarioActualizado = sistemaParqueo.getGestorUsuarios().actualizarUsuario(
                            id,
                            dialogo.getNombre(),
                            dialogo.getApellidos(),
                            Integer.parseInt(dialogo.getTelefono()),
                            dialogo.getEmail(),
                            dialogo.getDireccion(),
                            dialogo.getIdUsuario(),
                            dialogo.getPin(),
                            dialogo.getTipoUsuario(),
                            dialogo.getNumeroTarjeta(),
                            dialogo.getFechaVencimiento(),
                            dialogo.getCodigoValidacion(),
                            dialogo.getTerminalId(),
                            dialogo.getVehiculos()
                        );
                        
                        // Cargar los usuarios
                        cargarUsuarios();
                        // Mostrar el mensaje de confirmacion
                        JOptionPane.showMessageDialog(vista, "Usuario actualizado con éxito.");
                        // Log del usuario actualizado
                        LOGGER.info("Usuario actualizado: " + usuarioActualizado.getId());
                    } catch (Exception e) {
                        // Log del error
                        LOGGER.log(Level.SEVERE, "Error al actualizar usuario", e);
                        // Mostrar el mensaje de error
                        JOptionPane.showMessageDialog(vista, "Error al actualizar usuario: " + e.getMessage());
                    }
                }
            }
        } else {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un usuario para editar.");
        }
    }

    // Metodo para eliminar un usuario
    private void eliminarUsuario() {
        // Obtener la fila seleccionada
        int filaSeleccionada = vista.getTblUsuarios().getSelectedRow();
        // Si la fila seleccionada es mayor o igual a 0
        if (filaSeleccionada >= 0) {
            // Obtener el id del usuario
            String id = (String) vista.getTblUsuarios().getValueAt(filaSeleccionada, 0);
            // Mostrar el dialogo de confirmacion
            // El dialogo muestra un mensaje y dos botones: "Si" y "No"
            int confirmacion = JOptionPane.showConfirmDialog(vista, 
                "¿Está seguro de que desea eliminar este usuario?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            // Si el usuario selecciona "Si"
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    // Eliminar el usuario
                    sistemaParqueo.getGestorUsuarios().eliminarUsuario(id);
                    // Cargar los usuarios
                    cargarUsuarios();
                    // Mostrar el mensaje de confirmacion
                    JOptionPane.showMessageDialog(vista, "Usuario eliminado con éxito.");
                    // Log del usuario eliminado
                    LOGGER.info("Usuario eliminado: " + id);
                } catch (Exception e) {
                    // Log del error
                    LOGGER.log(Level.SEVERE, "Error al eliminar usuario", e);
                    // Mostrar el mensaje de error
                    JOptionPane.showMessageDialog(vista, "Error al eliminar usuario: " + e.getMessage());
                }
            }
        } else {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un usuario para eliminar.");
        }
    }

    // Metodo para validar los datos del usuario
    private boolean validarDatosUsuario(DialogoUsuario dialogo) {
        // Si el nombre no es valido
        if (!Validador.validarNombre(dialogo.getNombre())) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Nombre inválido");
            return false;
        }
        // Si los apellidos no son validos
        if (!Validador.validarApellidos(dialogo.getApellidos())) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Apellidos inválidos");
            return false;
        }
        // Si el telefono no es valido
        if (!Validador.validarTelefono(dialogo.getTelefono())) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Teléfono inválido");
            // Retornar false
            return false;
        }
        // Si el email no es valido
        if (!Validador.validarEmail(dialogo.getEmail())) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Email inválido");
            return false;
        }
        // Si la direccion no es valida
        if (!Validador.validarDireccion(dialogo.getDireccion())) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Dirección inválida");
            return false;
        }
        // Si el id del usuario no es valido
        if (!Validador.validarIdUsuario(dialogo.getIdUsuario())) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "ID de usuario inválido");
            return false;
        }
        // Si el pin no es valido
        if (!Validador.validarPin(dialogo.getPin())) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "PIN inválido");
            return false;
        }

        // Si el tipo de usuario es usuario parqueo
        if (dialogo.getTipoUsuario() == Usuario.TipoUsuario.USUARIO_PARQUEO) {
            // Si el numero de tarjeta no es valido
            if (!Validador.validarNumeroTarjeta(dialogo.getNumeroTarjeta())) {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, "Número de tarjeta inválido");
                return false;
            }
            // Si la fecha de vencimiento no es valida
            if (!Validador.validarFechaVencimiento(dialogo.getFechaVencimiento())) {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, "Fecha de vencimiento inválida");
                return false;
            }
            // Si el codigo de validacion no es valido
            if (!Validador.validarCodigoValidacion(dialogo.getCodigoValidacion())) {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, "Código de validación inválido");
                return false;
            }
            // Si los vehiculos no son validos
            for (Vehiculo vehiculo : dialogo.getVehiculos()) {
                // Si la placa no es valida
                if (!Validador.validarPlaca(vehiculo.getPlaca())) {
                    // Mostrar el mensaje de error
                    JOptionPane.showMessageDialog(vista, "Placa de vehículo inválida: " + vehiculo.getPlaca());
                    return false;
                }
            }
        }

        // Si el tipo de usuario es inspector
        if (dialogo.getTipoUsuario() == Usuario.TipoUsuario.INSPECTOR) {
            // Si el terminal id no es valido
            if (!Validador.validarTerminalId(dialogo.getTerminalId())) {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, "Terminal ID inválido");
                return false;
            }
        }

        return true;
    }
}