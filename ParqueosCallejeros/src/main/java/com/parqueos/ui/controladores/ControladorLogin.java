package com.parqueos.ui.controladores;

import com.parqueos.servicios.AuthService;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaLogin;
import com.parqueos.ui.vistas.VistaAdministrador;
import com.parqueos.ui.vistas.VistaUsuarioParqueo;
import com.parqueos.ui.vistas.VistaInspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.usuario.Inspector;

public class ControladorLogin extends ControladorBase {
    private final VistaLogin vista;
    private final AuthService authService;
    private final SistemaParqueo sistemaParqueo;

    public ControladorLogin(VistaLogin vista, AuthService authService, SistemaParqueo sistemaParqueo) {
        this.vista = vista;
        this.authService = authService;
        this.sistemaParqueo = sistemaParqueo;
        this.vista.setControlador(this);
        inicializar();
    }

    @Override
    protected void inicializar() {
    }

    public void iniciarSesion() {
        String idUsuario = vista.getCampoUsuario().getText();
        String pin = new String(vista.getCampoContrasena().getPassword());

        try {
            String token = sistemaParqueo.iniciarSesion(idUsuario, pin);
            Usuario usuario = sistemaParqueo.getAuthService().obtenerUsuarioAutenticado(token);
            redirigirUsuario(usuario, token);
        } catch (IllegalArgumentException ex) {
            vista.mostrarMensajeError("Credenciales inválidas. Por favor, intente de nuevo.");
        }
    }

    private void redirigirUsuario(Usuario usuario, String token) {
        vista.setVisible(false);
        if (usuario instanceof Administrador) {
            VistaAdministrador vistaAdmin = new VistaAdministrador(sistemaParqueo, token);
            new ControladorAdministrador(vistaAdmin, sistemaParqueo, token);
            vistaAdmin.setVisible(true);
        } else if (usuario instanceof UsuarioParqueo) {
            VistaUsuarioParqueo vistaUsuario = new VistaUsuarioParqueo(sistemaParqueo, token);
            new ControladorUsuarioParqueo(vistaUsuario, sistemaParqueo, (UsuarioParqueo) usuario, token);
            vistaUsuario.setVisible(true);
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