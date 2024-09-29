package com.parqueos.builders;

import com.parqueos.modelo.usuario.Inspector;

public class InspectorBuilder extends UsuarioBuilder {
    private String terminalId;

    public InspectorBuilder conTerminalId(String terminalId) {
        this.terminalId = terminalId;
        return this;
    }

    @Override
    public Inspector construir() {
        return new Inspector(nombre, apellidos, telefono, email, direccion, idUsuario, pin, terminalId);
    }
}