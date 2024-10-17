package com.parqueos.servicios;

import java.util.List;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.util.GestorArchivos;

public class SistemaParqueo {
    private ConfiguracionParqueo configuracion;
    private final AuthService authService;
    private final GestorNotificaciones gestorNotificaciones;
    private final GestorUsuarios gestorUsuarios;
    private final GestorReservas gestorReservas;
    private final GestorMultas gestorMultas;
    private final GestorEspacios gestorEspacios;
    private final GestorReportes gestorReportes;
    private final GestorVehiculos gestorVehiculos;

    private static final String ARCHIVO_CONFIGURACION = "configuracion.json";

    public SistemaParqueo() {
        this.gestorNotificaciones = new GestorNotificaciones();
        this.authService = new AuthService(gestorNotificaciones);
        this.gestorReservas = new GestorReservas();
        this.gestorMultas = new GestorMultas();
        this.gestorEspacios = new GestorEspacios();
        this.gestorReportes = new GestorReportes();
        this.gestorVehiculos = new GestorVehiculos();
        this.gestorUsuarios = new GestorUsuarios(authService, gestorVehiculos);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List<ConfiguracionParqueo> configuraciones = GestorArchivos.cargarTodosLosElementos(ARCHIVO_CONFIGURACION, ConfiguracionParqueo.class);
            if (!configuraciones.isEmpty()) {
                configuracion = configuraciones.get(0);
            } else {
                configuracion = ConfiguracionParqueo.obtenerInstancia();
                configuracion.guardar();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la configuración. Se usará la configuración por defecto.");
            e.printStackTrace();
            configuracion = ConfiguracionParqueo.obtenerInstancia();
        }
    
        try {
            gestorUsuarios.cargarUsuarios();
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            gestorReservas.cargarReservas();
        } catch (Exception e) {
            System.err.println("Error al cargar reservas. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            gestorMultas.cargarMultas();
        } catch (Exception e) {
            System.err.println("Error al cargar multas. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            gestorEspacios.cargarEspacios();
        } catch (Exception e) {
            System.err.println("Error al cargar espacios. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            gestorReportes.cargarReportes();
        } catch (Exception e) {
            System.err.println("Error al cargar reportes. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            gestorVehiculos.cargarVehiculos();
        } catch (Exception e) {
            System.err.println("Error al cargar vehículos. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
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
    
    public GestorReportes getGestorReportes() {
        return gestorReportes;
    }
}
