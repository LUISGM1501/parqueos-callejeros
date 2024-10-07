package com.parqueos.ui.vistas;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class VistaAgregarAdministrador extends VistaBase {
    // Elementos de la vista para agregar administradores
    private JTextField txtNombre;
    private JTextField txtApellidos;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JTextField txtFechaIngreso;
    private JTextField txtIdentificacionUsuario;
    private JTextField txtPin;
    private BotonPersonalizado btnGuardar;
    private BotonPersonalizado btnCancelar;

    public VistaAgregarAdministrador(){
        super("Agregar Administrador");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes(){
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(50, 50, 120, 30);
        panel.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(180, 50, 200, 30);
        panel.add(txtNombre);

        JLabel lblApellidos = new JLabel("Apellidos:");
        lblApellidos.setBounds(50, 90, 120, 30);
        panel.add(lblApellidos);

        txtApellidos = new JTextField();
        txtApellidos.setBounds(180, 90, 200, 30);
        panel.add(txtApellidos);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(50, 130, 120, 30);
        panel.add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(180, 130, 200, 30);
        panel.add(txtTelefono);

        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setBounds(50, 170, 120, 30);
        panel.add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(180, 170, 200, 30);
        panel.add(txtCorreo);

        JLabel lblDireccion = new JLabel("Dirección Física:");
        lblDireccion.setBounds(50, 210, 120, 30);
        panel.add(lblDireccion);

        txtDireccion = new JTextField();
        txtDireccion.setBounds(180, 210, 200, 30);
        panel.add(txtDireccion);

        JLabel lblFechaIngreso = new JLabel("Fecha de Ingreso:");
        lblFechaIngreso.setBounds(50, 250, 120, 30);
        panel.add(lblFechaIngreso);

        txtFechaIngreso = new JTextField();
        txtFechaIngreso.setBounds(180, 250, 200, 30);
        panel.add(txtFechaIngreso);

        JLabel lblIdentificacion = new JLabel("ID de Usuario:");
        lblIdentificacion.setBounds(50, 290, 120, 30);
        panel.add(lblIdentificacion);

        txtIdentificacionUsuario = new JTextField();
        txtIdentificacionUsuario.setBounds(180, 290, 200, 30);
        panel.add(txtIdentificacionUsuario);

        JLabel lblPin = new JLabel("PIN:");
        lblPin.setBounds(50, 330, 120, 30);
        panel.add(lblPin);

        txtPin = new JTextField();
        txtPin.setBounds(180, 330, 200, 30);
        panel.add(txtPin);

        btnGuardar = new BotonPersonalizado("Guardar");
        btnGuardar.setBounds(50, 380, 150, 40);
        panel.add(btnGuardar);

        btnCancelar = new BotonPersonalizado("Cancelar");
        btnCancelar.setBounds(230, 380, 150, 40);
        panel.add(btnCancelar);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para cada componente
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

    public JTextField getTxtIdentificacionUsuario() {
        return txtIdentificacionUsuario;
    }

    public JTextField getTxtPin() {
        return txtPin;
    }

    public BotonPersonalizado getBtnGuardar() {
        return btnGuardar;
    }

    public BotonPersonalizado getBtnCancelar() {
        return btnCancelar;
    }
}
