package com.parqueos.util;

import java.util.regex.Pattern;

public class Validador {
    // Patrón para validar el email
    private static final Pattern PATRON_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    // ^[A-Za-z0-9+_.-]+: Coincide con cualquier combinación de caracteres alfanuméricos,
    // +: Coincide con uno o más de los caracteres anteriores.
    // _: Coincide con un guión bajo.
    // .: Coincide con un punto.
    // -: Coincide con un guión.
    // +: Coincide con uno o más de los caracteres anteriores.
    // @: Coincide con un arroba.
    // (.+)$: Coincide con cualquier combinación de caracteres que sigue al arroba.

    // Patrón para validar el teléfono
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{8}$");

    // Metodo para validar el email
    public static boolean validarEmail(String email) {
        return email != null && PATRON_EMAIL.matcher(email).matches();
    }

    // Metodo para validar el teléfono
    public static boolean validarTelefono(String telefono) {
        return telefono != null && PATRON_TELEFONO.matcher(telefono).matches();
    }

    // Metodo para validar el pin
    public static boolean validarPin(String pin) {
        return pin != null && pin.length() == 4 && pin.matches("\\d+");
    }

    // Metodo para validar la placa
    public static boolean validarPlaca(String placa) {
        return placa != null && placa.length() >= 1 && placa.length() <= 6;
    }

    // Metodo para validar el nombre
    public static boolean validarNombre(String nombre) {
        return nombre != null && nombre.length() >= 2 && nombre.length() <= 20;
    }

    // Metodo para validar los apellidos
    public static boolean validarApellidos(String apellidos) {
        return apellidos != null && apellidos.length() >= 1 && apellidos.length() <= 40;
    }

    // Metodo para validar la dirección
    public static boolean validarDireccion(String direccion) {
        return direccion != null && direccion.length() >= 5 && direccion.length() <= 60;
    }

    // Metodo para validar el id del usuario
    public static boolean validarIdUsuario(String idUsuario) {
        return idUsuario != null && idUsuario.length() >= 2 && idUsuario.length() <= 25;
    }

    // Metodo para validar el tipo de usuario
    public static boolean validarTipoUsuario(String tipoUsuario) {
        return tipoUsuario != null && tipoUsuario.length() >= 2 && tipoUsuario.length() <= 25;
    }

    // Metodo para validar el numero de tarjeta
    public static boolean validarNumeroTarjeta(String numeroTarjeta) {
        return numeroTarjeta != null && numeroTarjeta.length() == 16 && numeroTarjeta.matches("\\d+");
    }

    // Metodo para validar el codigo de validación
    public static boolean validarCodigoValidacion(String codigoValidacion) {
        return codigoValidacion != null && codigoValidacion.length() == 3 && codigoValidacion.matches("\\d+");
    }

    // Metodo para validar el id de la terminal
    public static boolean validarTerminalId(String terminalId) {
        return terminalId != null && terminalId.length() == 6 && terminalId.matches("\\d+");
    }

    // Metodo para validar el tiempo guardado
    public static boolean validarTiempoGuardado(int tiempoGuardado) {
        return tiempoGuardado >= 0;
    }

    // Metodo para validar la fecha de vencimiento
    public static boolean validarFechaVencimiento(String fechaVencimiento) {
        return fechaVencimiento != null && fechaVencimiento.length() == 4 && fechaVencimiento.matches("\\d+");
    }

}
