package com.parqueos.ui.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;

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
    private final Map<Class<? extends Usuario>, BiConsumer<Usuario, String>> redireccionUsuarios;

    public ControladorLogin(VistaLogin vista, AuthService authService, SistemaParqueo sistemaParqueo) {
        this.vista = vista;
        this.authService = authService;
        this.sistemaParqueo = sistemaParqueo;
        this.redireccionUsuarios = new HashMap<>();
        inicializarRedireccionUsuarios();
        inicializar();
    }

    private void inicializarRedireccionUsuarios() {
        redireccionUsuarios.put(Administrador.class, (usuario, token) -> {
            VistaAdministrador vistaAdmin = new VistaAdministrador();
            new ControladorAdministrador(vistaAdmin, sistemaParqueo);
            vistaAdmin.setVisible(true);
        });

        redireccionUsuarios.put(UsuarioParqueo.class, (usuario, token) -> {
            VistaUsuarioParqueo vistaUsuario = new VistaUsuarioParqueo();
            new ControladorUsuarioParqueo(vistaUsuario, sistemaParqueo, (UsuarioParqueo) usuario, token);
            vistaUsuario.setVisible(true);
        });

        redireccionUsuarios.put(Inspector.class, (usuario, token) -> {
            VistaInspector vistaInspector = new VistaInspector();
            new ControladorInspector(vistaInspector, sistemaParqueo, (Inspector) usuario);
            vistaInspector.setVisible(true);
        });
    }

    @Override
    protected void inicializar() {
        vista.getBotonLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = vista.getCampoUsuario().getText();
                String contrasena = new String(vista.getCampoContrasena().getPassword());
                
                try {
                    String token = authService.iniciarSesion(usuario, contrasena);
                    System.out.println("Inicio de sesión exitoso. Token: " + token);
                    Usuario usuarioAutenticado = authService.obtenerUsuarioAutenticado(token);
                    redirigirUsuario(usuarioAutenticado, token);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Error de inicio de sesión: " + ex.getMessage());
                    vista.mostrarMensajeError("Error de inicio de sesión: " + ex.getMessage());
                }
            }
        });
    }

    private void redirigirUsuario(Usuario usuario, String token) {
        vista.setVisible(false);
        if (usuario instanceof UsuarioParqueo) {
            VistaUsuarioParqueo vistaUsuario = new VistaUsuarioParqueo();
            new ControladorUsuarioParqueo(vistaUsuario, sistemaParqueo, (UsuarioParqueo) usuario, token);
            vistaUsuario.setVisible(true);
        } else {
            // Manejar otros tipos de usuarios...
            System.out.println("Tipo de usuario no manejado: " + usuario.getClass().getSimpleName());
            vista.setVisible(true);
        }
    }
}