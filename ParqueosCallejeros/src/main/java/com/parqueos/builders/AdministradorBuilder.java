package com.parqueos.builders;

import com.parqueos.modelo.usuario.Administrador;

public class AdministradorBuilder extends UsuarioBuilder<AdministradorBuilder> {
    // Metodo para construir el administrador
    
    @Override
    public Administrador construir() {
        return new Administrador(nombre, apellidos, telefono, email, direccion, idUsuario, pin);
    }
}