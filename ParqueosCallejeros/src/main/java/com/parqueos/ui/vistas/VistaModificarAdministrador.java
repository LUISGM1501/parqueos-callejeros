package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class VistaModificarAdministrador extends VistaBase {
    // Campos de texto de la vista para modificar administradores
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtApellidos;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JTextField txtFechaIngreso;
    private JTextField txtPin;
    private BotonPersonalizado btnBuscar;
    private BotonPersonalizado btnGuardar;
    private BotonPersonalizado btnCancelar;

    public VistaModificarAdministrador(){
        super("Modificar Administrador");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes(){
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);

        JLabel lblId = new JLabel("ID de Usuario:");
        lblId.setBounds(50, 50, 120, 30);
        panel.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(180, 50, 200, 30);
        panel.add(txtId);

        // Botón de búsqueda
        btnBuscar = new BotonPersonalizado("Buscar");
        btnBuscar.setBounds(400, 50, 100, 30);
        panel.add(btnBuscar);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(50, 100, 120, 30);
        panel.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(180, 100, 200, 30);
        panel.add(txtNombre);

        JLabel lblApellidos = new JLabel("Apellidos:");
        lblApellidos.setBounds(50, 140, 120, 30);
        panel.add(lblApellidos);

        txtApellidos = new JTextField();
        txtApellidos.setBounds(180, 140, 200, 30);
        panel.add(txtApellidos);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(50, 180, 120, 30);
        panel.add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(180, 180, 200, 30);
        panel.add(txtTelefono);

        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setBounds(50, 220, 120, 30);
        panel.add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(180, 220, 200, 30);
        panel.add(txtCorreo);

        JLabel lblDireccion = new JLabel("Dirección Física:");
        lblDireccion.setBounds(50, 260, 120, 30);
        panel.add(lblDireccion);

        txtDireccion = new JTextField();
        txtDireccion.setBounds(180, 260, 200, 30);
        panel.add(txtDireccion);

        JLabel lblFechaIngreso = new JLabel("Fecha de Ingreso:");
        lblFechaIngreso.setBounds(50, 300, 120, 30);
        panel.add(lblFechaIngreso);

        txtFechaIngreso = new JTextField();
        txtFechaIngreso.setBounds(180, 300, 200, 30);
        panel.add(txtFechaIngreso);

        JLabel lblPin = new JLabel("PIN:");
        lblPin.setBounds(50, 340, 120, 30);
        panel.add(lblPin);

        txtPin = new JTextField();
        txtPin.setBounds(180, 340, 200, 30);
        panel.add(txtPin);

        btnGuardar = new BotonPersonalizado("Guardar");
        btnGuardar.setBounds(50, 390, 150, 40);
        panel.add(btnGuardar);

        btnCancelar = new BotonPersonalizado("Cancelar");
        btnCancelar.setBounds(230, 390, 150, 40);
        panel.add(btnCancelar);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para cada componente
    public JTextField getTxtId() {
        return txtId;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtApellidos() {
        return txtApellidos;
    }

    public JTextField getTxtTelefono() {
        return txtTelefono;
    }

    public JTextField getTxtCorreo() {
        return txtCorreo;
    }

    public JTextField getTxtDireccion() {
        return txtDireccion;
    }

    public JTextField getTxtFechaIngreso() {
        return txtFechaIngreso;
    }

    public JTextField getTxtPin() {
        return txtPin;
    }

    public BotonPersonalizado getBtnBuscar() {
        return btnBuscar;
    }

    public BotonPersonalizado getBtnGuardar() {
        return btnGuardar;
    }

    public BotonPersonalizado getBtnCancelar() {
        return btnCancelar;
    }
}
