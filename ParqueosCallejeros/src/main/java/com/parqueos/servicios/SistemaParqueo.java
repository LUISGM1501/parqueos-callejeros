package com.parqueos.servicios;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.util.GestorArchivos;

import java.util.List;

public class SistemaParqueo {
    private ConfiguracionParqueo configuracion;
    private AuthService authService;
    private GestorNotificaciones gestorNotificaciones;
    private GestorUsuarios gestorUsuarios;
    private GestorReservas gestorReservas;
    private GestorMultas gestorMultas;
    private GestorEspacios gestorEspacios;

    private static final String ARCHIVO_CONFIGURACION = "configuracion.json";

    public SistemaParqueo() {
        this.gestorNotificaciones = new GestorNotificaciones();
        this.authService = new AuthService(gestorNotificaciones);
        this.gestorUsuarios = new GestorUsuarios(authService);
        this.gestorReservas = new GestorReservas();
        this.gestorMultas = new GestorMultas();
        this.gestorEspacios = new GestorEspacios();
        cargarDatos();
    }

    private void cargarDatos() {
        List<ConfiguracionParqueo> configuraciones = GestorArchivos.cargarTodosLosElementos(ARCHIVO_CONFIGURACION, ConfiguracionParqueo.class);
        if (!configuraciones.isEmpty()) {
            configuracion = configuraciones.get(0);
        } else {
            configuracion = ConfiguracionParqueo.obtenerInstancia();
            configuracion.guardar();
        }
        gestorUsuarios.cargarUsuarios();
        gestorReservas.cargarReservas();
        gestorMultas.cargarMultas();
        gestorEspacios.cargarEspacios();
    }

    public String iniciarSesion(String idUsuario, String pin) {
        return authService.iniciarSesion(idUsuario, pin);
    }

    public void cerrarSesion(String token) {
        authService.cerrarSesion(token);
    }

    public void cambiarPin(String token, String pinActual, String nuevoPin) {
        authService.cambiarPin(token, pinActual, nuevoPin);
    }

    public void solicitarRestablecimientoPin(String idUsuario) {
        authService.solicitarRestablecimientoPin(idUsuario);
    }

    public void setConfiguracion(String token, ConfiguracionParqueo nuevaConfiguracion) {
        if (authService.esAdministrador(token)) {
            this.configuracion = nuevaConfiguracion;
            configuracion.guardar();
        } else {
            throw new IllegalStateException("Usuario no autorizado");
        }
    }

    public ConfiguracionParqueo getConfiguracion(String token) {
        if (authService.estaAutenticado(token)) {
            return configuracion;
        } else {
            throw new IllegalStateException("Usuario no autenticado");
        }
    }

    // Getters para los gestores
    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }

    public GestorReservas getGestorReservas() {
        return gestorReservas;
    }

    public GestorMultas getGestorMultas() {
        return gestorMultas;
    }

    public GestorEspacios getGestorEspacios() {
        return gestorEspacios;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public GestorNotificaciones getGestorNotificaciones() {
        return gestorNotificaciones;
    }
}