package com.parqueos.servicios;

import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar los espacios de parqueo
public class GestorEspacios {
    // Archivo donde se guardan los espacios de parqueo
    private static final String ARCHIVO_ESPACIOS = "espacios.json";
    private List<EspacioParqueo> espacios;

    // Metodo para cargar los espacios de parqueo
    public void cargarEspacios() {
        espacios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
    }

    // Metodo para agregar un espacio de parqueo
    public void agregarEspacio(EspacioParqueo espacio) {
        espacios.add(espacio);
        guardarEspacios();
    }

    // Metodo para obtener los espacios disponibles
    public List<EspacioParqueo> obtenerEspaciosDisponibles() {
        return espacios.stream()
                       // Filtrar los espacios que estan disponibles
                       .filter(EspacioParqueo::estaDisponible)
                       // Convertir los espacios a una lista
                       .collect(Collectors.toList());
    }

    // Metodo para obtener los espacios ocupados
    public List<EspacioParqueo> obtenerEspaciosOcupados() {
        return espacios.stream()
                       // Filtrar los espacios que estan ocupados
                       .filter(EspacioParqueo::estaOcupado)
                       // Convertir los espacios a una lista
                       .collect(Collectors.toList());
    }

    // Metodo para buscar un espacio de parqueo por su numero
    public EspacioParqueo buscarEspacio(String numeroEspacio) {
        return espacios.stream()
                       // Filtrar el espacio que tiene el numero igual al numero del espacio
                       .filter(e -> e.getNumero().equals(numeroEspacio))
                       .findFirst()
                       .orElse(null);
    }

    // Metodo para guardar los espacios de parqueo
    private void guardarEspacios() {
        // Guardar los espacios de parqueo en el archivo json
        GestorArchivos.guardarTodo(espacios, ARCHIVO_ESPACIOS);
    }

    // Getter para obtener los espacios
    public List<EspacioParqueo> getEspacios() {
        return espacios;
    }
}