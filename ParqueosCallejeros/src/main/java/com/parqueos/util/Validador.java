package com.parqueos.util;

import java.util.regex.Pattern;

public class Validador {
    private static final Pattern PATRON_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{8}$");

    public static boolean validarEmail(String email) {
        return email != null && PATRON_EMAIL.matcher(email).matches();
    }

    public static boolean validarTelefono(String telefono) {
        return telefono != null && PATRON_TELEFONO.matcher(telefono).matches();
    }

    public static boolean validarPin(String pin) {
        return pin != null && pin.length() == 4 && pin.matches("\\d+");
    }

    public static boolean validarPlaca(String placa) {
        return placa != null && placa.length() >= 1 && placa.length() <= 6;
    }

    public static boolean validarNombre(String nombre) {
        return nombre != null && nombre.length() >= 2 && nombre.length() <= 20;
    }

    public static boolean validarApellidos(String apellidos) {
        return apellidos != null && apellidos.length() >= 1 && apellidos.length() <= 40;
    }

    public static boolean validarDireccion(String direccion) {
        return direccion != null && direccion.length() >= 5 && direccion.length() <= 60;
    }

    public static boolean validarIdUsuario(String idUsuario) {
        return idUsuario != null && idUsuario.length() >= 2 && idUsuario.length() <= 25;
    }

    public static boolean validarTipoUsuario(String tipoUsuario) {
        return tipoUsuario != null && tipoUsuario.length() >= 2 && tipoUsuario.length() <= 25;
    }

    public static boolean validarNumeroTarjeta(String numeroTarjeta) {
        return numeroTarjeta != null && numeroTarjeta.length() == 16 && numeroTarjeta.matches("\\d+");
    }
    
    public static boolean validarCodigoValidacion(String codigoValidacion) {
        return codigoValidacion != null && codigoValidacion.length() == 3 && codigoValidacion.matches("\\d+");
    }

    public static boolean validarTerminalId(String terminalId) {
        return terminalId != null && terminalId.length() == 6 && terminalId.matches("\\d+");
    }

    public static boolean validarTiempoGuardado(int tiempoGuardado) {
        return tiempoGuardado >= 0;
    }

    public static boolean validarFechaVencimiento(String fechaVencimiento) {
        // Expresión regular para verificar el formato MM/YY
        String patron = "^(0[1-9]|1[0-2])/\\d{2}$";

        // Verificar si la fecha cumple con el patrón
        return fechaVencimiento != null && Pattern.matches(patron, fechaVencimiento);
    }

}
