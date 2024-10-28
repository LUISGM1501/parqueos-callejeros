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
        vista.setVisible(false);
        if (usuario instanceof UsuarioParqueo) {
            VistaUsuarioParqueo vistaUsuario = new VistaUsuarioParqueo(sistemaParqueo, token);
            new ControladorUsuarioParqueo(vistaUsuario, sistemaParqueo, (UsuarioParqueo) usuario, token);
            vistaUsuario.setVisible(true);

        } else if (usuario instanceof Administrador) {
            VistaAdministrador vistaAdmin = new VistaAdministrador(sistemaParqueo, token);
            new ControladorAdministrador(vistaAdmin, sistemaParqueo, token);
            vistaAdmin.setVisible(true);

        } else if (usuario instanceof Inspector) {
            VistaInspector vistaInspector = new VistaInspector(sistemaParqueo, token);
            new ControladorInspector(vistaInspector, sistemaParqueo, (Inspector) usuario, token);
            vistaInspector.setVisible(true);

        } else {
            vista.mostrarMensajeError("Tipo de usuario no reconocido.");
            vista.setVisible(true);
        }
    }
}