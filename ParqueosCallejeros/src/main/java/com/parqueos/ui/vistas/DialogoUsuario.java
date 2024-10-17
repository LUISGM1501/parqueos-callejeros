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

    public DialogoUsuario(Window owner, Usuario usuario) {
        super(owner, usuario == null ? "Agregar Usuario" : "Editar Usuario", ModalityType.APPLICATION_MODAL);
        this.usuarioActual = usuario;
        inicializarComponentes();
        if (usuario != null) {
            cargarDatosUsuario();
        }
    }

    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        add(txtNombre = new JTextField(20), gbc);
        txtNombre.setToolTipText("Ingrese el nombre (2-20 caracteres)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        add(txtApellidos = new JTextField(20), gbc);
        txtApellidos.setToolTipText("Ingrese los apellidos (1-40 caracteres)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        add(txtTelefono = new JTextField(20), gbc);
        txtTelefono.setToolTipText("Ingrese el teléfono (8 dígitos)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(txtEmail = new JTextField(20), gbc);
        txtEmail.setToolTipText("Ingrese un email válido (ejemplo@dominio.com)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        add(txtDireccion = new JTextField(20), gbc);
        txtDireccion.setToolTipText("Ingrese la dirección (5-60 caracteres)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("ID Usuario:"), gbc);
        gbc.gridx = 1;
        add(txtIdUsuario = new JTextField(20), gbc);
        txtIdUsuario.setToolTipText("Ingrese el ID de usuario (2-25 caracteres)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("PIN:"), gbc);
        gbc.gridx = 1;
        add(txtPin = new JPasswordField(20), gbc);
        txtPin.setToolTipText("Ingrese el PIN (4 dígitos)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Tipo Usuario:"), gbc);
        gbc.gridx = 1;
        add(cmbTipoUsuario = new JComboBox<>(Usuario.TipoUsuario.values()), gbc);
        cmbTipoUsuario.setToolTipText("Seleccione el tipo de usuario");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Número Tarjeta:"), gbc);
        gbc.gridx = 1;
        add(txtNumeroTarjeta = new JTextField(20), gbc);
        txtNumeroTarjeta.setToolTipText("Ingrese el número de tarjeta (16 dígitos)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Fecha Vencimiento:"), gbc);
        gbc.gridx = 1;
        add(txtFechaVencimiento = new JTextField(20), gbc);
        txtFechaVencimiento.setToolTipText("Ingrese la fecha de vencimiento (MM/YY)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Código Validación:"), gbc);
        gbc.gridx = 1;
        add(txtCodigoValidacion = new JTextField(20), gbc);
        txtCodigoValidacion.setToolTipText("Ingrese el código de validación (3 dígitos)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Terminal ID:"), gbc);
        gbc.gridx = 1;
        add(txtTerminalId = new JTextField(20), gbc);
        txtTerminalId.setToolTipText("Ingrese el ID de terminal (6 caracteres)");

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Vehículos:"), gbc);
        gbc.gridx = 1;
        modeloListaVehiculos = new DefaultListModel<>();
        listaVehiculos = new JList<>(modeloListaVehiculos);
        JScrollPane scrollVehiculos = new JScrollPane(listaVehiculos);
        scrollVehiculos.setPreferredSize(new Dimension(200, 100));
        add(scrollVehiculos, gbc);
        listaVehiculos.setToolTipText("Lista de vehículos registrados");

        gbc.gridx = 1; gbc.gridy++;
        btnAgregarVehiculo = new BotonPersonalizado("Agregar Vehículo");
        add(btnAgregarVehiculo, gbc);
        btnAgregarVehiculo.setToolTipText("Haga clic para agregar un nuevo vehículo");

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new BotonPersonalizado("Guardar");
        btnCancelar = new BotonPersonalizado("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, gbc);

        btnAgregarVehiculo.addActionListener(e -> agregarVehiculo());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnCancelar.addActionListener(e -> dispose());

        cmbTipoUsuario.addActionListener(e -> actualizarCamposSegunTipoUsuario());

        pack();
        setLocationRelativeTo(null);
    }

    private void actualizarCamposSegunTipoUsuario() {
        Usuario.TipoUsuario tipoSeleccionado = (Usuario.TipoUsuario) cmbTipoUsuario.getSelectedItem();
        txtNumeroTarjeta.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
        txtFechaVencimiento.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
        txtCodigoValidacion.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
        txtTerminalId.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.INSPECTOR);
        btnAgregarVehiculo.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
        listaVehiculos.setEnabled(tipoSeleccionado == Usuario.TipoUsuario.USUARIO_PARQUEO);
    }

    private void cargarDatosUsuario() {
        txtNombre.setText(usuarioActual.getNombre());
        txtApellidos.setText(usuarioActual.getApellidos());
        txtTelefono.setText(String.valueOf(usuarioActual.getTelefono()));
        txtEmail.setText(usuarioActual.getEmail());
        txtDireccion.setText(usuarioActual.getDireccion());
        txtIdUsuario.setText(usuarioActual.getIdUsuario());
        cmbTipoUsuario.setSelectedItem(usuarioActual.getTipoUsuario());

        if (usuarioActual instanceof UsuarioParqueo) {
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuarioActual;
            txtNumeroTarjeta.setText(usuarioParqueo.getNumeroTarjeta());
            txtFechaVencimiento.setText(usuarioParqueo.getFechaVencimientoTarjeta());
            txtCodigoValidacion.setText(usuarioParqueo.getCodigoValidacionTarjeta());
            for (Vehiculo vehiculo : usuarioParqueo.getVehiculos()) {
                modeloListaVehiculos.addElement(vehiculo.getPlaca());
                vehiculos.add(vehiculo);
            }
        } else if (usuarioActual instanceof Inspector) {
            Inspector inspector = (Inspector) usuarioActual;
            txtTerminalId.setText(inspector.getTerminalId());
        }

        actualizarCamposSegunTipoUsuario();
    }

    private void agregarVehiculo() {
        String placa = JOptionPane.showInputDialog(this, "Ingrese la placa del vehículo:");
        if (placa != null && !placa.trim().isEmpty()) {
            String marca = JOptionPane.showInputDialog(this, "Ingrese la marca del vehículo (opcional):");
            String modelo = JOptionPane.showInputDialog(this, "Ingrese el modelo del vehículo (opcional):");
            Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, null);
            vehiculos.add(vehiculo);
            modeloListaVehiculos.addElement(placa);
        }
    }

    private boolean validarCampos() {
        if (!Validador.validarNombre(getNombre())) {
            JOptionPane.showMessageDialog(this, "Nombre inválido. Debe tener entre 2 y 20 caracteres.");
            return false;
        }
        if (!Validador.validarApellidos(getApellidos())) {
            JOptionPane.showMessageDialog(this, "Apellidos inválidos. Deben tener entre 1 y 40 caracteres.");
            return false;
        }
        if (!Validador.validarTelefono(getTelefono())) {
            JOptionPane.showMessageDialog(this, "Teléfono inválido. Debe ser un número de 8 dígitos.");
            return false;
        }
        if (!Validador.validarEmail(getEmail())) {
            JOptionPane.showMessageDialog(this, "Email inválido.");
            return false;
        }
        if (!Validador.validarDireccion(getDireccion())) {
            JOptionPane.showMessageDialog(this, "Dirección inválida. Debe tener entre 5 y 60 caracteres.");
            return false;
        }
        if (!Validador.validarIdUsuario(getIdUsuario())) {
            JOptionPane.showMessageDialog(this, "ID de usuario inválido. Debe tener entre 2 y 25 caracteres.");
            return false;
        }
        if (!Validador.validarPin(getPin())) {
            JOptionPane.showMessageDialog(this, "PIN inválido. Debe ser un número de 4 dígitos.");
            return false;
        }

        if (getTipoUsuario() == Usuario.TipoUsuario.USUARIO_PARQUEO) {
            if (!Validador.validarNumeroTarjeta(getNumeroTarjeta())) {
                JOptionPane.showMessageDialog(this, "Número de tarjeta inválido. Debe tener 16 dígitos.");
                return false;
            }
            if (!Validador.validarFechaVencimiento(getFechaVencimiento())) {
                JOptionPane.showMessageDialog(this, "Fecha de vencimiento inválida. Use el formato MM/YY.");
                return false;
            }
            if (!Validador.validarCodigoValidacion(getCodigoValidacion())) {
                JOptionPane.showMessageDialog(this, "Código de validación inválido. Debe ser un número de 3 dígitos.");
                return false;
            }
        }

        if (getTipoUsuario() == Usuario.TipoUsuario.INSPECTOR) {
            if (!Validador.validarTerminalId(getTerminalId())) {
                JOptionPane.showMessageDialog(this, "Terminal ID inválido. Debe tener 6 caracteres.");
                return false;
            }
        }

        return true;
    }

    private void guardarUsuario() {
        if (!validarCampos()) {
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