package com.parqueos.builders;

import com.parqueos.modelo.usuario.Administrador;

public class AdministradorBuilder extends UsuarioBuilder<AdministradorBuilder> {
    @Override
    public Administrador construir() {
        return new Administrador(nombre, apellidos, telefono, email, direccion, idUsuario, pin);
    }
}