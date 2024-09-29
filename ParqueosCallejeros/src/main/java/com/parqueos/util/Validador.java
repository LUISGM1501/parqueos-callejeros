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
}