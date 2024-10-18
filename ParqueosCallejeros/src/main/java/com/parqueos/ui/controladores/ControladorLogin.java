package com.parqueos.ui.controladores;

import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.servicios.AuthService;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaAdministrador;
import com.parqueos.ui.vistas.VistaInspector;
import com.parqueos.ui.vistas.VistaLogin;
import com.parqueos.ui.vistas.VistaUsuarioParqueo;

// Controlador para el login
public class ControladorLogin extends ControladorBase {
    private final VistaLogin vista;
    private final AuthService authService;
    private final SistemaParqueo sistemaParqueo;

    // Constructor para inicializar el controlador
    public ControladorLogin(VistaLogin vista, AuthService authService, SistemaParqueo sistemaParqueo) {
        this.vista = vista;
        this.authService = authService;
        this.sistemaParqueo = sistemaParqueo;
        this.vista.setControlador(this);
        inicializar();
    }

    // Metodo para inicializar el controlador
    @Override
    protected void inicializar() {
    }

    // Metodo para iniciar la sesion
    public void iniciarSesion() {
        // Obtener el id del usuario y el pin
        String idUsuario = vista.getCampoUsuario().getText();
        String pin = new String(vista.getCampoContrasena().getPassword());

        try {
            // Iniciar la sesion
            String token = sistemaParqueo.iniciarSesion(idUsuario, pin);
            // Obtener el usuario autenticado
            Usuario usuario = sistemaParqueo.getAuthService().obtenerUsuarioAutenticado(token);
            // Redirigir al usuario
            redirigirUsuario(usuario, token);
        } catch (IllegalArgumentException ex) {
            // Mostrar el mensaje de error
            vista.mostrarMensajeError("Credenciales inválidas. Por favor, intente de nuevo.");
        }
    }

    // Metodo para redirigir al usuario
    private void redirigirUsuario(Usuario usuario, String token) {
        // Ocultar la vista de login
        vista.setVisible(false);
        // Si el usuario es un administrador
        if (usuario instanceof Administrador) {
            // Crear la vista del administrador
            VistaAdministrador vistaAdmin = new VistaAdministrador(sistemaParqueo, token);
            // Crear el controlador del administrador
            new ControladorAdministrador(vistaAdmin, sistemaParqueo, token);
            // Mostrar la vista del administrador
            vistaAdmin.setVisible(true);
        } else if (usuario instanceof UsuarioParqueo) {
            // Crear la vista del usuario parqueo
            VistaUsuarioParqueo vistaUsuario = new VistaUsuarioParqueo(sistemaParqueo, token);
            // Crear el controlador del usuario parqueo
            new ControladorUsuarioParqueo(vistaUsuario, sistemaParqueo, (UsuarioParqueo) usuario, token);
            // Mostrar la vista del usuario parqueo
            vistaUsuario.setVisible(true);
        } else if (usuario instanceof Inspector) {
            // Crear la vista del inspector
            VistaInspector vistaInspector = new VistaInspector(sistemaParqueo, token);
            // Crear el controlador del inspector
            new ControladorInspector(vistaInspector, sistemaParqueo, (Inspector) usuario, token);
            // Mostrar la vista del inspector
            vistaInspector.setVisible(true);
        } else {
            // Mostrar el mensaje de error
            vista.mostrarMensajeError("Tipo de usuario no reconocido.");
            // Mostrar la vista de login
            vista.setVisible(true);
        }
    }
}