package com.parqueos.servicios;

import com.parqueos.modelo.usuario.Usuario;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    private Map<String, Usuario> usuarios;
    private Map<String, String> sesiones;

    public AuthService() {
        this.usuarios = new HashMap<>();
        this.sesiones = new HashMap<>();
    }

    public void registrarUsuario(Usuario usuario) {
        usuarios.put(usuario.getIdUsuario(), usuario);
    }

    public String iniciarSesion(String idUsuario, String pin) {
        Usuario usuario = usuarios.get(idUsuario);
        if (usuario != null && usuario.validarPin(pin)) {
            String token = UUID.randomUUID().toString();
            sesiones.put(token, idUsuario);
            return token;
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
}