package com.parqueos.servicios;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.parqueos.builders.AdministradorBuilder;
import com.parqueos.builders.InspectorBuilder;
import com.parqueos.builders.UsuarioParqueoBuilder;
import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;

public class AuthService {
    private final Map<String, Usuario> usuarios;
    private final Map<String, String> sesiones;
    private final Map<String, String> pinesTemporales;
    private final GestorNotificaciones gestorNotificaciones;

    public AuthService(GestorNotificaciones gestorNotificaciones) {
        this.usuarios = new HashMap<>();
        this.sesiones = new HashMap<>();
        this.pinesTemporales = new HashMap<>();
        this.gestorNotificaciones = gestorNotificaciones;
        agregarUsuariosPrueba();
    }

    private void agregarUsuariosPrueba() {
        System.out.println("Agregando usuarios de prueba");
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

        System.out.println("Usuarios registrados:");
        for (String id : usuarios.keySet()) {
            System.out.println(id + ": " + usuarios.get(id).getClass().getSimpleName());
        }
    }

    public void registrarUsuario(Usuario usuario) {
        System.out.println("Registrando usuario: " + usuario.getIdUsuario());
        usuarios.put(usuario.getIdUsuario(), usuario);
    }

    public String iniciarSesion(String idUsuario, String pin) {
        System.out.println("Intento de inicio de sesión para: " + idUsuario);
        Usuario usuario = usuarios.get(idUsuario);
        if (usuario != null) {
            System.out.println("Usuario encontrado: " + usuario.getClass().getSimpleName());
            System.out.println("PIN proporcionado: " + pin);
            System.out.println("PIN almacenado: " + usuario.getPin());
            if (usuario.validarPin(pin)) {
                String token = UUID.randomUUID().toString();
                sesiones.put(token, idUsuario);
                System.out.println("Inicio de sesion exitoso");
                return token;
            } else {
                System.out.println("PIN incorrecto");
            }
        } else {
            System.out.println("Usuario no encontrado");
        }
        throw new IllegalArgumentException("Credenciales invalidas");
    }

    public void cerrarSesion(String token) {
        sesiones.remove(token);
    }

    public Usuario obtenerUsuarioAutenticado(String token) {
        String idUsuario = sesiones.get(token);
        if (idUsuario != null) {
            return usuarios.get(idUsuario);
        }
        throw new IllegalStateException("Sesion no valida");
    }

    public boolean estaAutenticado(String token) {
        return sesiones.containsKey(token);
    }

    public void cambiarPin(String token, String pinActual, String nuevoPin) {
        Usuario usuario = obtenerUsuarioAutenticado(token);
        usuario.cambiarPin(pinActual, nuevoPin);
    }

    public void solicitarRestablecimientoPin(String idUsuario) {
        Usuario usuario = usuarios.get(idUsuario);
        if (usuario != null) {
            String pinTemporal = generarPinTemporal();
            pinesTemporales.put(idUsuario, pinTemporal);
            gestorNotificaciones.enviarCorreo(usuario.getEmail(), "Restablecimiento de PIN", 
                "Su PIN temporal es: " + pinTemporal + ". Por favor, cambielo despues de iniciar sesion.");
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
    }

    private String generarPinTemporal() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    private boolean validarPinTemporal(String idUsuario, String pin) {
        String pinTemporal = pinesTemporales.get(idUsuario);
        return pinTemporal != null && pinTemporal.equals(pin);
    }
}