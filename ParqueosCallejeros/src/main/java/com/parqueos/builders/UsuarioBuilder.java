package com.parqueos.builders;

import com.parqueos.modelo.usuario.Usuario;

public class UsuarioBuilder <T extends UsuarioBuilder<T>> {
    // Builder principal el cual se hereda para crear los distintos tipos de usuarios

    protected String nombre;
    protected String apellidos;
    protected int telefono;
    protected String email;
    protected String direccion;
    protected String idUsuario;
    protected String pin;
    protected Usuario.TipoUsuario tipoUsuario;

    // Setters para los atributos del usuario
    @SuppressWarnings("unchecked") // Para quitar las advertencias deunchecked para los generics
    public T conNombre(String nombre) {
        this.nombre = nombre;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conApellidos(String apellidos) {
        this.apellidos = apellidos;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conTelefono(int telefono) {
        this.telefono = telefono;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conEmail(String email) {
        this.email = email;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conDireccion(String direccion) {
        this.direccion = direccion;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conPin(String pin) {
        this.pin = pin;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conTipoUsuario(Usuario.TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
        return (T) this;
    }

    // Metodo para construir el usuario
    public Usuario construir() {
        return new Usuario(nombre, apellidos, telefono, email, direccion, idUsuario, pin, tipoUsuario);
    }
}