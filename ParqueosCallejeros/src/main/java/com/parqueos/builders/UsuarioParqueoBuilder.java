package com.parqueos.builders;

import com.parqueos.modelo.usuario.UsuarioParqueo;

public class UsuarioParqueoBuilder extends UsuarioBuilder<UsuarioParqueoBuilder> {
    private String numeroTarjeta;
    private String fechaVencimientoTarjeta;
    private String codigoValidacionTarjeta;

    public UsuarioParqueoBuilder conNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
        return this;
    }

    public UsuarioParqueoBuilder conFechaVencimientoTarjeta(String fechaVencimientoTarjeta) {
        this.fechaVencimientoTarjeta = fechaVencimientoTarjeta;
        return this;
    }

    public UsuarioParqueoBuilder conCodigoValidacionTarjeta(String codigoValidacionTarjeta) {
        this.codigoValidacionTarjeta = codigoValidacionTarjeta;
        return this;
    }

    @Override
    public UsuarioParqueo construir() {
        return new UsuarioParqueo(nombre, apellidos, telefono, email, direccion, idUsuario, pin,
                                  numeroTarjeta, fechaVencimientoTarjeta, codigoValidacionTarjeta);
    }
}