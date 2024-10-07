package com.parqueos.servicios;

import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.util.GestorArchivos;

import java.util.List;

public class GestorUsuarios {
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private List<Usuario> usuarios;
    private AuthService authService;

    public GestorUsuarios(AuthService authService) {
        this.authService = authService;
    }

    public void cargarUsuarios() {
        usuarios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS, Usuario.class);
        for (Usuario usuario : usuarios) {
            authService.registrarUsuario(usuario);
        }
    }

    public void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
        authService.registrarUsuario(usuario);
        guardarUsuarios();
    }

    public void actualizarUsuario(Usuario usuario) {
        int index = usuarios.indexOf(usuario);
        if (index != -1) {
            usuarios.set(index, usuario);
            guardarUsuarios();
        }
    }

    public void eliminarUsuario(String idUsuario) {
        usuarios.removeIf(u -> u.getIdUsuario().equals(idUsuario));
        guardarUsuarios();
    }

    private void guardarUsuarios() {
        GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}