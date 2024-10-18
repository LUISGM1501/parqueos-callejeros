package com.parqueos.builders;

import com.parqueos.modelo.usuario.Inspector;

public class InspectorBuilder extends UsuarioBuilder<InspectorBuilder> {
    // Metodo para construir el inspector

    private String terminalId;

    // Setter para el terminalId
    public InspectorBuilder conTerminalId(String terminalId) {
        this.terminalId = terminalId;
        return this;
    }

    @Override
    public Inspector construir() {
        // Constructor del inspector
        return new Inspector(nombre, apellidos, telefono, email, direccion, idUsuario, pin, terminalId);
    }
}