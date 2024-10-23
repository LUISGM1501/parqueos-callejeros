package com.parqueos.ui.vistas;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.util.Validador;

// Dialogo para agregar o editar un usuario
public class DialogoUsuario extends JDialog {
    private JTextField txtNombre;
    private JTextField txtApellidos;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtDireccion;
    private JTextField txtIdUsuario;
    private JPasswordField txtPin;
    private JComboBox<Usuario.TipoUsuario> cmbTipoUsuario;
    private JTextField txtNumeroTarjeta;
    private JTextField txtFechaVencimiento;
    private JTextField txtCodigoValidacion;
    private JTextField txtTerminalId;
    private JList<String> listaVehiculos;
    private DefaultListModel<String> modeloListaVehiculos;
    private BotonPersonalizado btnAgregarVehiculo;
    private BotonPersonalizado btnGuardar;
    private BotonPersonalizado btnCancelar;

    private final Usuario usuarioActual;
    private final List<Vehiculo> vehiculos = new ArrayList<>();

    // Constructor para inicializar el dialogo
    public DialogoUsuario(Window owner, Usuario usuario) {
        super(owner, usuario == null ? "Agregar Usuario" : "Editar Usuario", ModalityType.APPLICATION_MODAL);
        this.usuarioActual = usuario;
        inicializarComponentes();
        if (usuario != null) {
            cargarDatosUsuario();
        }
    }

    // Metodo para inicializar los componentes
    private void inicializarComponentes() {
        // Asignar el layout
        setLayout(new GridBagLayout());
        // Crear los constraints
        GridBagConstraints gbc = new GridBagConstraints();
        // Asignar los espacios
        gbc.insets = new Insets(5, 5, 5, 5);
        // Asignar el anclaje
        gbc.anchor = GridBagConstraints.WEST;

        // Asignar los componentes

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; // Asignar la posicion
        add(new JLabel("Nombre:"), gbc); // Agregar el label
        gbc.gridx = 1; // Asignar la posicion
        add(txtNombre = new JTextField(20), gbc); // Agregar el campo de texto
        txtNombre.setToolTipText("Ingrese el nombre (2-20 caracteres)"); // Agregar el tooltip

        // Apellidos
        gbc.gridx = 0; gbc.gridy++; // ++ Para la siguiente fila
        add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        add(txtApellidos = new JTextField(20), gbc);
        txtApellidos.setToolTipText("Ingrese los apellidos (1-40 caracteres)");

        // Telefono
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        add(txtTelefono = new JTextField(20), gbc);
        txtTelefono.setToolTipText("Ingrese el teléfono (8 dígitos)");

        // Email
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(txtEmail = new JTextField(20), gbc);
        txtEmail.setToolTipText("Ingrese un email válido (ejemplo@dominio.com)");

        // Direccion
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        add(txtDireccion = new JTextField(20), gbc);
        txtDireccion.setToolTipText("Ingrese la dirección (5-60 caracteres)");

        // ID Usuario
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("ID Usuario:"), gbc);
        gbc.gridx = 1;
        add(txtIdUsuario = new JTextField(20), gbc);
        txtIdUsuario.setToolTipText("Ingrese el ID de usuario (2-25 caracteres)");

        // PIN
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("PIN:"), gbc);
        gbc.gridx = 1;
        add(txtPin = new JPasswordField(20), gbc);
        txtPin.setToolTipText("Ingrese el PIN (4 dígitos)");

        // Tipo Usuario
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Tipo Usuario:"), gbc);
        gbc.gridx = 1;
        add(cmbTipoUsuario = new JComboBox<>(Usuario.TipoUsuario.values()), gbc);
        cmbTipoUsuario.setToolTipText("Seleccione el tipo de usuario");

        // Numero Tarjeta
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Número Tarjeta:"), gbc);
        gbc.gridx = 1;
        add(txtNumeroTarjeta = new JTextField(20), gbc);
        txtNumeroTarjeta.setToolTipText("Ingrese el número de tarjeta (16 dígitos)");

        // Fecha Vencimiento
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Fecha Vencimiento:"), gbc);
        gbc.gridx = 1;
        add(txtFechaVencimiento = new JTextField(20), gbc);
        txtFechaVencimiento.setToolTipText("Ingrese la fecha de vencimiento (MM/YY)");

        // Codigo Validacion
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Código Validación:"), gbc);
        gbc.gridx = 1;
        add(txtCodigoValidacion = new JTextField(20), gbc);
        txtCodigoValidacion.setToolTipText("Ingrese el código de validación (3 dígitos)");

        // Terminal ID
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Terminal ID:"), gbc);
        gbc.gridx = 1;
        add(txtTerminalId = new JTextField(20), gbc);
        txtTerminalId.setToolTipText("Ingrese el ID de terminal (6 caracteres)");

        // Vehiculos
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Vehículos:"), gbc);
        gbc.gridx = 1;
        modeloListaVehiculos = new DefaultListModel<>();
        listaVehiculos = new JList<>(modeloListaVehiculos);
        JScrollPane scrollVehiculos = new JScrollPane(listaVehiculos);
        scrollVehiculos.setPreferredSize(new Dimension(200, 100));
        add(scrollVehiculos, gbc);
        listaVehiculos.setToolTipText("Lista de vehículos registrados");

        // Agregar Vehiculo
        gbc.gridx = 1; gbc.gridy++;
        btnAgregarVehiculo = new BotonPersonalizado("Agregar Vehículo");
        add(btnAgregarVehiculo, gbc);
        btnAgregarVehiculo.setToolTipText("Haga clic para agregar un nuevo vehículo");

        // Botones
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2; // Ancho de 2 columnas
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Crear un panel con un layout de flujo
        btnGuardar = new BotonPersonalizado("Guardar"); // Boton de guardar
        btnCancelar = new BotonPersonalizado("Cancelar"); // Boton de cancelar
        panelBotones.add(btnGuardar); // Agregar el boton de guardar al panel
        panelBotones.add(btnCancelar); // Agregar el boton de cancelar al panel
        add(panelBotones, gbc); // Agregar el panel de botones al dialogo

        btnAgregarVehiculo.addActionListener(e -> agregarVehiculo());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnCancelar.addActionListener(e -> dispose());

        cmbTipoUsuario.addActionListener(e -> actualizarCamposSegunTipoUsuario());

        // pack hace que el dialogo se ajuste al tamaño de los componentes
        pack(); // Ajustar el tamaño del dialogo
        setLocationRelativeTo(null); // Centrar el dialogo
    }

    // Metodo para actualizar los campos segun el tipo de usuario
    private void actualizarCamposSegunTipoUsuario() {
        // Obtener el tipo de usuario seleccionado
        Usuario.TipoUsuario tipoSeleccionado = (Usuario.TipoUsuario) cmbTipoUsuario.getSelectedItem();
        // Deshabilitar los campos segun el tipo de usuario
        txtNumeroTarjeta.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
        txtFechaVencimiento.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
        txtCodigoValidacion.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);

        txtTerminalId.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.INSPECTOR);

        btnAgregarVehiculo.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
        listaVehiculos.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
    }

    // Metodo para cargar los datos del usuario
    private void cargarDatosUsuario() {
        txtNombre.setText(usuarioActual.getNombre()); 
        txtApellidos.setText(usuarioActual.getApellidos());
        txtTelefono.setText(String.valueOf(usuarioActual.getTelefono()));
        txtEmail.setText(usuarioActual.getEmail());
        txtDireccion.setText(usuarioActual.getDireccion());
        txtIdUsuario.setText(usuarioActual.getIdUsuario());
        cmbTipoUsuario.setSelectedItem(usuarioActual.getTipoUsuario());

        // Si el usuario es un usuario parqueo, cargar los datos del usuario parqueo
        if (usuarioActual instanceof UsuarioParqueo) {
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuarioActual;
            // Asignar los datos del usuario parqueo
            txtNumeroTarjeta.setText(usuarioParqueo.getNumeroTarjeta());
            txtFechaVencimiento.setText(usuarioParqueo.getFechaVencimientoTarjeta());
            txtCodigoValidacion.setText(usuarioParqueo.getCodigoValidacionTarjeta());
            // Agregar los vehiculos del usuario parqueo a la lista
            for (Vehiculo vehiculo : usuarioParqueo.getVehiculos()) {
                // Agregar el vehiculo a la lista
                modeloListaVehiculos.addElement(vehiculo.getPlaca());
                // Agregar el vehiculo a la lista de vehiculos
                vehiculos.add(vehiculo);
            }
        // Si el usuario es un inspector, cargar el terminal id
        } else if (usuarioActual instanceof Inspector) {
            Inspector inspector = (Inspector) usuarioActual;
            txtTerminalId.setText(inspector.getTerminalId());
        }

        // Actualizar los campos segun el tipo de usuario
        actualizarCamposSegunTipoUsuario();
    }

    // Metodo para agregar un vehiculo
    private void agregarVehiculo() {
        String placa = JOptionPane.showInputDialog(this, "Ingrese la placa del vehículo:");
        if (placa != null && !placa.trim().isEmpty()) {
            String marca = JOptionPane.showInputDialog(this, "Ingrese la marca del vehículo (opcional):");
            String modelo = JOptionPane.showInputDialog(this, "Ingrese el modelo del vehículo (opcional):");
            
            // Crear el vehículo
            Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, null);
            
            // Agregar el vehículo a la lista de vehículos
            vehiculos.add(vehiculo);
            
            // Agregar la placa a la lista visual
            modeloListaVehiculos.addElement(placa);
        }
    }

    // Metodo para validar los campos
    private boolean validarCampos() {
        // Validar el nombre
        if (!Validador.validarNombre(getNombre())) {
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "Nombre inválido. Debe tener entre 2 y 20 caracteres.");
            return false;
        }

        // Validar los apellidos
        if (!Validador.validarApellidos(getApellidos())) {
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "Apellidos inválidos. Deben tener entre 1 y 40 caracteres.");
            return false;
        }
        
        // Validar el telefono
        if (!Validador.validarTelefono(getTelefono())) {
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "Teléfono inválido. Debe ser un número de 8 dígitos.");
            return false;
        }

        // Validar el email
        if (!Validador.validarEmail(getEmail())) {
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "Email inválido.");
            return false;
        }

        // Validar la direccion
        if (!Validador.validarDireccion(getDireccion())) {
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "Dirección inválida. Debe tener entre 5 y 60 caracteres.");
            return false;
        }

        // Validar el id del usuario
        if (!Validador.validarIdUsuario(getIdUsuario())) {
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "ID de usuario inválido. Debe tener entre 2 y 25 caracteres.");
            return false;
        }

        // Validar el pin
        if (!Validador.validarPin(getPin())) {
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "PIN inválido. Debe ser un número de 4 dígitos.");
            return false;
        }

        // Si el tipo de usuario es usuario parqueo, validar los campos
        if (getTipoUsuario() == Usuario.TipoUsuario.USUARIO_PARQUEO) {
            // Validar el numero de tarjeta
            if (!Validador.validarNumeroTarjeta(getNumeroTarjeta())) {
                // Mostrar un mensaje de error
                JOptionPane.showMessageDialog(this, "Número de tarjeta inválido. Debe tener 16 dígitos.");
                return false;
            }
            // Validar la fecha de vencimiento
            if (!Validador.validarFechaVencimiento(getFechaVencimiento())) {
                // Mostrar un mensaje de error
                JOptionPane.showMessageDialog(this, "Fecha de vencimiento inválida. Use el formato MM/YY.");
                return false;
            }
            // Validar el codigo de validacion
            if (!Validador.validarCodigoValidacion(getCodigoValidacion())) {
                // Mostrar un mensaje de error
                JOptionPane.showMessageDialog(this, "Código de validación inválido. Debe ser un número de 3 dígitos.");
                return false;
            }
        }

        // Si el tipo de usuario es inspector, validar el terminal id
        if (getTipoUsuario() == Usuario.TipoUsuario.INSPECTOR) {
            // Validar el terminal id
            if (!Validador.validarTerminalId(getTerminalId())) {
                // Mostrar un mensaje de error
                JOptionPane.showMessageDialog(this, "Terminal ID inválido. Debe tener 6 caracteres.");
                return false;
            }
        }

        // Si pasa todas las validaciones, retornar true
        return true;
    }

    // Metodo para guardar el usuario
    private void guardarUsuario() {
        // Validar los campos
        if (!validarCampos()) {
            // Si no pasa las validaciones, no guardar el usuario
            return;
        }
        // Logica para guardar el usuario en el sistema
        dispose();
    }

    // Getters para los campos
    public String getNombre() { return txtNombre.getText(); }
    public String getApellidos() { return txtApellidos.getText(); }
    public String getTelefono() { return txtTelefono.getText(); }
    public String getEmail() { return txtEmail.getText(); }
    public String getDireccion() { return txtDireccion.getText(); }
    public String getIdUsuario() { return txtIdUsuario.getText(); }
    public String getPin() { return new String(txtPin.getPassword()); }
    public Usuario.TipoUsuario getTipoUsuario() { return (Usuario.TipoUsuario) cmbTipoUsuario.getSelectedItem(); }
    public String getNumeroTarjeta() { return txtNumeroTarjeta.getText(); }
    public String getFechaVencimiento() { return txtFechaVencimiento.getText(); }
    public String getCodigoValidacion() { return txtCodigoValidacion.getText(); }
    public String getTerminalId() { return txtTerminalId.getText(); }
    public List<Vehiculo> getVehiculos() { return vehiculos; }
}