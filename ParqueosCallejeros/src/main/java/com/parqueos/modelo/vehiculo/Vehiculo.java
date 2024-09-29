package com.parqueos.modelo.vehiculo;

import java.io.Serializable;

import com.parqueos.modelo.usuario.UsuarioParqueo;

public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String placa;
    private String marca;
    private String modelo;
    private UsuarioParqueo propietario;

    public Vehiculo(String placa, String marca, String modelo, UsuarioParqueo propietario) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.propietario = propietario;
    }

    // Getters y setters
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public UsuarioParqueo getPropietario() {
        return propietario;
    }

    public void setPropietario(UsuarioParqueo  propietario) {
        this.propietario = propietario;
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "placa='" + placa + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                '}';
    }
}