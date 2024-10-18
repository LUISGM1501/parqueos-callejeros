package com.parqueos.servicios;

import java.util.List;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar el sistema de parqueo
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

    // Constructor para inicializar los gestores
    public SistemaParqueo() {
        // Inicializar los gestores
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

    // Metodo para cargar los datos
    private void cargarDatos() {
        try {
            // Cargar las configuraciones del archivo json
            List<ConfiguracionParqueo> configuraciones = GestorArchivos.cargarTodosLosElementos(ARCHIVO_CONFIGURACION, ConfiguracionParqueo.class);
            if (!configuraciones.isEmpty()) {
                // Si se encontro una configuracion, asignarla
                configuracion = configuraciones.get(0);
            } else {
                // Si no se encontro una configuracion, crear una nueva
                configuracion = ConfiguracionParqueo.obtenerInstancia();
                // Guardar la configuracion en el archivo json
                configuracion.guardar();
            }
        } catch (Exception e) {
            // Mensaje de error
            System.err.println("Error al cargar la configuración. Se usará la configuración por defecto.");
            e.printStackTrace();
            configuracion = ConfiguracionParqueo.obtenerInstancia();
        }
    
        // Cargar los usuarios
        try {
            // Cargar los usuarios del archivo json
            gestorUsuarios.cargarUsuarios();
        } catch (Exception e) {
            // Mensaje de error
            System.err.println("Error al cargar usuarios. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            // Cargar las reservas del archivo json
            gestorReservas.cargarReservas();
        } catch (Exception e) {
            // Mensaje de error
            System.err.println("Error al cargar reservas. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            // Cargar las multas del archivo json
            gestorMultas.cargarMultas();
        } catch (Exception e) {
            // Mensaje de error
            System.err.println("Error al cargar multas. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            // Cargar los espacios del archivo json
            gestorEspacios.cargarEspacios();
        } catch (Exception e) {
            // Mensaje de error
            System.err.println("Error al cargar espacios. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            // Cargar los reportes del archivo json
            gestorReportes.cargarReportes();
        } catch (Exception e) {
            // Mensaje de error
            System.err.println("Error al cargar reportes. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
        try {
            // Cargar los vehiculos del archivo json
            gestorVehiculos.cargarVehiculos();
        } catch (Exception e) {
            // Mensaje de error
            System.err.println("Error al cargar vehículos. Se iniciará con una lista vacía.");
            e.printStackTrace();
        }
    }

    // Metodo para iniciar sesion
    public String iniciarSesion(String idUsuario, String pin) {
        // Iniciar sesion en el servicio de autenticacion
        return authService.iniciarSesion(idUsuario, pin);
    }

    // Metodo para cerrar sesion
    public void cerrarSesion(String token) {
        // Cerrar sesion en el servicio de autenticacion
        authService.cerrarSesion(token);
    }

    // Metodo para cambiar el pin
    public void cambiarPin(String token, String pinActual, String nuevoPin) {
        // Cambiar el pin en el servicio de autenticacion
        authService.cambiarPin(token, pinActual, nuevoPin);
    }

    // Metodo para solicitar restablecimiento de pin
    public void solicitarRestablecimientoPin(String idUsuario) {
        // Solicitar restablecimiento de pin en el servicio de autenticacion
        authService.solicitarRestablecimientoPin(idUsuario);
    }

    // Metodo para setear la configuracion
    public void setConfiguracion(String token, ConfiguracionParqueo nuevaConfiguracion) {
        if (authService.esAdministrador(token)) {
            // Setear la nueva configuracion
            this.configuracion = nuevaConfiguracion;
            configuracion.guardar();
        } else {
            throw new IllegalStateException("Usuario no autorizado");
        }
    }

    // Metodo para obtener la configuracion
    public ConfiguracionParqueo getConfiguracion(String token) {
        // Verificar si el usuario esta autenticado
        if (authService.estaAutenticado(token)) {
            // Retornar la configuracion
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
