package com.parqueos.servicios;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.util.GestorArchivos;

import java.util.List;
import java.util.stream.Collectors;

public class GestorMultas {
    private static final String ARCHIVO_MULTAS = "multas.json";
    private List<Multa> multas;

    public void cargarMultas() {
        multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
    }

    public void agregarMulta(Multa multa) {
        multas.add(multa);
        guardarMultas();
    }

    public void actualizarMulta(Multa multa) {
        int index = multas.indexOf(multa);
        if (index != -1) {
            multas.set(index, multa);
            guardarMultas();
        }
    }

    public void eliminarMulta(String idMulta) {
        multas.removeIf(m -> m.getIdMulta().equals(idMulta));
        guardarMultas();
    }

    public List<Multa> obtenerMultasUsuario(UsuarioParqueo usuario) {
        return multas.stream()
                     .filter(m -> m.getVehiculo().getPropietario().equals(usuario))
                     .sorted((m1, m2) -> m2.getFechaHora().compareTo(m1.getFechaHora()))
                     .collect(Collectors.toList());
    }

    public Multa buscarMulta(String idMulta) {
        return multas.stream()
                     .filter(m -> m.getIdMulta().equals(idMulta))
                     .findFirst()
                     .orElse(null);
    }

    private void guardarMultas() {
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    public List<Multa> getMultas() {
        return multas;
    }
}