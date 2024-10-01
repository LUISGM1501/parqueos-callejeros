package com.parqueos.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

public class GestorArchivos {

    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // Método para añadir un nuevo elemento a un archivo JSON
    public static <T> void addElemento(T elemento, String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        elementos.add(elemento);
        guardarTodo(elementos, nombreArchivo);
    }

    // Método para modificar un elemento existente en el archivo JSON
    public static <T> void modificarElemento(String id, T elementoActualizado, String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        for (int i = 0; i < elementos.size(); i++) {
            if (obtenerId(elementos.get(i)).equals(id)) { 
                elementos.set(i, elementoActualizado);
                break;
            }
        }
        guardarTodo(elementos, nombreArchivo);
    }

    // Método para eliminar un elemento del archivo JSON
    public static <T> void eliminarElemento(String id, String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        elementos.removeIf(elemento -> obtenerId(elemento).equals(id));
        guardarTodo(elementos, nombreArchivo);
    }

    // Metodo para cargar un elemento de un archivo JSON
    public static <T> T cargarElemento(String id, String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        for (T elemento : elementos) {
            if (obtenerId(elemento).equals(id)) {
                return elemento;
            }
        }
        return null;
    }

    // Método para cargar todos los elementos de un archivo JSON
    public static <T> List<T> cargarTodosLosElementos(String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = new ArrayList<>();
        File file = new File(nombreArchivo);
        if (file.exists()) {
            try {
                CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, tipoClase);
                elementos = objectMapper.readValue(file, listType);
                System.out.println("Elementos cargados correctamente desde " + nombreArchivo);
            } catch (IOException e) {
                System.err.println("Error al cargar los elementos: " + e.getMessage());
            }
        } else {
            System.out.println("El archivo " + nombreArchivo + " no existe. Se creará uno nuevo.");
        }
        return elementos;
    }

    // Método para guardar todos los elementos en el archivo JSON
    public static <T> void guardarTodo(List<T> elementos, String nombreArchivo) {
        try {
            objectMapper.writeValue(new File(nombreArchivo), elementos);
            System.out.println("Elementos guardados correctamente en " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar los elementos: " + e.getMessage());
        }
    }

    // Método auxiliar para obtener el ID de un elemento
    private static <T> String obtenerId(T elemento) {
        // Supongamos que cada elemento tiene un método `getId()`
        try {
            return (String) elemento.getClass().getMethod("getId").invoke(elemento);
        } catch (Exception e) {
            throw new RuntimeException("El elemento no tiene un método getId válido", e);
        }
    }
}
