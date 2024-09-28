package com.parqueos.modelo.usuario;

import java.time.LocalDate;

public class Usuario {
    protected String nombre;
    protected String apellidos;
    protected int telefono;
    protected String email;
    protected String direccion;
    protected String idUsuario;
    private String pin;
    protected LocalDate fechaIngreso;

    public Usuario(String nombre, String apellidos, int telefono, String email, String direccion, String idUsuario, String pin) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.idUsuario = idUsuario;
        this.pin = pin;
        this.fechaIngreso = LocalDate.now();
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPin() {
        return pin;
    }   

    public void setPin(String pin) {
        this.pin = pin;
    }
    

    public boolean validarPin(String pin) {
        return this.pin.equals(pin);
    }

    public void cambiarPin(String pinActual, String nuevoPin) {
        if (validarPin(pinActual)) {
            this.pin = nuevoPin;
        } else {
            throw new IllegalArgumentException("PIN actual incorrecto");
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", telefono=" + telefono +
                ", email='" + email + '\'' +
                ", direccion='" + direccion + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                '}';
    }
}