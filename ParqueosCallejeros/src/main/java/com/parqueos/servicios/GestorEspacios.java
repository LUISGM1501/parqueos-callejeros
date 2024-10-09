package com.parqueos.servicios;

import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.util.GestorArchivos;

public class GestorEspacios {
    private static final String ARCHIVO_ESPACIOS = "espacios.json";
    private List<EspacioParqueo> espacios;

    public void cargarEspacios() {
        espacios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
    }

    public void agregarEspacio(EspacioParqueo espacio) {
        espacios.add(espacio);
        guardarEspacios();
    }

    public List<EspacioParqueo> obtenerEspaciosDisponibles() {
        return espacios.stream()
                       .filter(EspacioParqueo::estaDisponible)
                       .collect(Collectors.toList());
    }

    public List<EspacioParqueo> obtenerEspaciosOcupados() {
        return espacios.stream()
                       .filter(EspacioParqueo::estaOcupado)
                       .collect(Collectors.toList());
    }

    public EspacioParqueo buscarEspacio(String numeroEspacio) {
        return espacios.stream()
                       .filter(e -> e.getNumero().equals(numeroEspacio))
                       .findFirst()
                       .orElse(null);
    }

    private void guardarEspacios() {
        GestorArchivos.guardarTodo(espacios, ARCHIVO_ESPACIOS);
    }

    public List<EspacioParqueo> getEspacios() {
        return espacios;
    }
}