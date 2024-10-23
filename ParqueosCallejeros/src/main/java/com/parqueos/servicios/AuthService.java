package com.parqueos.servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import com.parqueos.builders.AdministradorBuilder;
import com.parqueos.builders.InspectorBuilder;
import com.parqueos.builders.UsuarioParqueoBuilder;
import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;

// Clase AuthService para la autenticacion de usuarios
public class AuthService {
    private final Map<String, Usuario> usuarios;
    private final Map<String, String> sesiones;
    private final Map<String, String> pinesTemporales;
    private final GestorNotificaciones gestorNotificaciones;

    // Constructor para la clase AuthService
    public AuthService(GestorNotificaciones gestorNotificaciones) {
        this.usuarios = new HashMap<>();
        this.sesiones = new HashMap<>();
        this.pinesTemporales = new HashMap<>(); 
        this.gestorNotificaciones = gestorNotificaciones;
        agregarUsuariosPrueba();
    }

    // Metodo para agregar usuarios de prueba
    private void agregarUsuariosPrueba() {
        Administrador admin = new AdministradorBuilder()
            .conNombre("Admin")
            .conApellidos("Admin")
            .conTelefono(12345678)
            .conEmail("admin@example.com")
            .conDireccion("Dirección Admin")
            .conIdUsuario("admin")
            .conPin("1234")
            .construir();

        UsuarioParqueo usuario = new UsuarioParqueoBuilder()
            .conNombre("Usuario")
            .conApellidos("Usuario")
            .conTelefono(87654321)
            .conEmail("usuario@example.com")
            .conDireccion("Dirección Usuario")
            .conIdUsuario("user")
            .conPin("1234")
            .conNumeroTarjeta("1234567890123456")
            .conFechaVencimientoTarjeta("12/25")
            .conCodigoValidacionTarjeta("123")
            .construir();

        Inspector inspector = new InspectorBuilder()
            .conNombre("Inspector")
            .conApellidos("Inspector")
            .conTelefono(13579246)
            .conEmail("inspector@example.com")
            .conDireccion("Dirección Inspector")
            .conIdUsuario("inspector")
            .conPin("1234")
            .conTerminalId("INS001")
            .construir();

        registrarUsuario(admin);
        registrarUsuario(usuario);
        registrarUsuario(inspector);

        System.out.println("Usuarios de prueba agregados:");
        for (Usuario u : usuarios.values()) {
            System.out.println(u.getIdUsuario() + ": " + u.getClass().getSimpleName());
        }
    }

    // Metodo para registrar un usuario
    public void registrarUsuario(Usuario usuario) {
        // Agregar el usuario al mapa de usuarios
        usuarios.put(usuario.getIdUsuario(), usuario);
    }

    // Metodo para iniciar una sesion
    public String iniciarSesion(String idUsuario, String pin) {
        // Obtener el usuario del mapa de usuarios
        Usuario usuario = usuarios.get(idUsuario);

        // Validar que el usuario existe
        if (usuario == null) {
            // Si no existe, lanzar una excepcion
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Validar que el pin es correcto

        if (usuario.validarPin(pin)) {
            // Generar un token para la sesion
            String token = UUID.randomUUID().toString();
            // Agregar el token y el id del usuario al mapa de sesiones
            sesiones.put(token, usuario.getIdUsuario());
            // Retornar el token
            return token;
        } else {
            // Si el pin es incorrecto, lanzar una excepcion
            throw new IllegalArgumentException("Credenciales inválidas");
        }
    }

    // Metodo para cerrar una sesion    
    public void cerrarSesion(String token) {
        // Remover el token del mapa de sesiones
        sesiones.remove(token);
    }

    // Metodo para obtener el usuario autenticado
    public Usuario obtenerUsuarioAutenticado(String token) {
        // Obtener el id del usuario del mapa de sesiones
        String idUsuario = sesiones.get(token);

        // Validar que el id del usuario existe
        if (idUsuario != null) {
            // Retornar el usuario del mapa de usuarios
            return usuarios.get(idUsuario);
        }
        // Si no existe, lanzar una excepcion
        throw new IllegalStateException("Sesión no válida");
    }

    // Metodo para validar si el usuario esta autenticado
    public boolean estaAutenticado(String token) {
        // Validar que el token existe en el mapa de sesiones
        return sesiones.containsKey(token);
    }

    // Metodo para cambiar el pin de un usuario
    public void cambiarPin(String token, String pinActual, String nuevoPin) {
        // Obtener el usuario autenticado
        Usuario usuario = obtenerUsuarioAutenticado(token);
        // Cambiar el pin del usuario
        usuario.cambiarPin(pinActual, nuevoPin);
    }

    // Metodo para solicitar el restablecimiento de un pin
    public void solicitarRestablecimientoPin(String idUsuario) {
        // Obtener el usuario del mapa de usuarios
        Usuario usuario = usuarios.get(idUsuario);

        // Validar que el usuario existe
        if (usuario == null) {
            // Si no existe, lanzar una excepcion
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Generar un pin temporal
        String pinTemporal = generarPinTemporal();

        // Agregar el pin temporal al mapa de pines temporales
        pinesTemporales.put(idUsuario, pinTemporal);

        // Enviar un correo al usuario con el pin temporal
        gestorNotificaciones.enviarNotificacion(usuario.getEmail(), "Restablecimiento de PIN", 
            "Su PIN temporal es: " + pinTemporal + ". Por favor, cámbielo después de iniciar sesión.");
    }

    // Metodo para generar un pin temporal
    private String generarPinTemporal() {
        // Crear un objeto Random para generar un numero aleatorio
        Random random = new Random();

        // Generar un numero aleatorio de 4 digitos
        return String.format("%04d", random.nextInt(10000));
    }

    // Metodo para obtener todos los usuarios
    public List<Usuario> obtenerTodosUsuarios() {
        // Retornar una lista con todos los usuarios del mapa de usuarios
        return new ArrayList<>(usuarios.values());
    }

    // Metodo para obtener todos los administradores
    public List<Administrador> obtenerTodosAdministradores() {
        // Retornar una lista con todos los administradores del mapa de usuarios
        return usuarios.values().stream()
                .filter(u -> u instanceof Administrador)
                .map(u -> (Administrador) u)
                .collect(Collectors.toList());
    }

    // Metodo para obtener todos los inspectores
    public List<Inspector> obtenerTodosInspectores() {
        // Retornar una lista con todos los inspectores del mapa de usuarios
        return usuarios.values().stream()
                // Filtrar los usuarios que son inspectores
                .filter(u -> u instanceof Inspector)

                // Convertir los usuarios a inspectores
                .map(u -> (Inspector) u)

                // Retornar una lista con los inspectores
                .collect(Collectors.toList());
    }

    // Metodo para obtener todos los usuarios parqueo
    public List<UsuarioParqueo> obtenerTodosUsuariosParqueo() {
        // Retornar una lista con todos los usuarios parqueo del mapa de usuarios
        return usuarios.values().stream()
                // Filtrar los usuarios que son usuarios parqueo
                .filter(u -> u instanceof UsuarioParqueo)

                // Convertir los usuarios a usuarios parqueo
                .map(u -> (UsuarioParqueo) u)

                // Retornar una lista con los usuarios parqueo
                .collect(Collectors.toList());
    }

    // Metodo para validar si el usuario es administrador
    public boolean esAdministrador(String token) {
        // Obtener el usuario autenticado
        Usuario usuario = obtenerUsuarioAutenticado(token);

        // Validar si el usuario es administrador
        return usuario instanceof Administrador;
    }

    // Metodo para validar si el usuario es inspector
    public boolean esInspector(String token) {
        // Obtener el usuario autenticado
        Usuario usuario = obtenerUsuarioAutenticado(token);

        // Validar si el usuario es inspector
        return usuario instanceof Inspector;
    }

    // Metodo para validar si el usuario es usuario parqueo
    public boolean esUsuarioParqueo(String token) {
        // Obtener el usuario autenticado
        Usuario usuario = obtenerUsuarioAutenticado(token);

        // Validar si el usuario es usuario parqueo
        return usuario instanceof UsuarioParqueo;
    }

    // Metodo para validar si el pin temporal es correcto
    private boolean validarPinTemporal(String idUsuario, String pin) {
        // Obtener el pin temporal del mapa de pines temporales
        String pinTemporal = pinesTemporales.get(idUsuario);

        // Validar si el pin temporal existe y es igual al pin
        return pinTemporal != null && pinTemporal.equals(pin);
    }
}