package com.parqueos.builders;

import com.parqueos.modelo.usuario.Usuario;

public class UsuarioBuilder {
    protected String nombre;
    protected String apellidos;
    protected int telefono;
    protected String email;
    protected String direccion;
    protected String idUsuario;
    protected String pin;

    public UsuarioBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public UsuarioBuilder conApellidos(String apellidos) {
        this.apellidos = apellidos;
        return this;
    }

    public UsuarioBuilder conTelefono(int telefono) {
        this.telefono = telefono;
        return this;
    }

    public UsuarioBuilder conEmail(String email) {
        this.email = email;
        return this;
    }

    public UsuarioBuilder conDireccion(String direccion) {
        this.direccion = direccion;
        return this;
    }

    public UsuarioBuilder conIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }

    public UsuarioBuilder conPin(String pin) {
        this.pin = pin;
        return this;
    }

    public Usuario construir() {
        return new Usuario(nombre, apellidos, telefono, email, direccion, idUsuario, pin);
    }
}