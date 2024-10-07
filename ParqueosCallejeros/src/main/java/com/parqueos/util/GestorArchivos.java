package com.parqueos.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestorArchivos {
    private static final Logger LOGGER = Logger.getLogger(GestorArchivos.class.getName());
    private static final ObjectMapper objectMapper = configurarObjectMapper();

    private static ObjectMapper configurarObjectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();
        
        return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public static <T> void guardarElemento(T elemento, String nombreArchivo) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, (Class<T>) elemento.getClass());
        elementos.add(elemento);
        guardarTodo(elementos, nombreArchivo);
    }

    public static <T> void actualizarElemento(String id, T elementoActualizado, String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        for (int i = 0; i < elementos.size(); i++) {
            if (obtenerId(elementos.get(i)).equals(id)) {
                elementos.set(i, elementoActualizado);
                guardarTodo(elementos, nombreArchivo);
                return;
            }
        }
        LOGGER.warning("No se encontró el elemento con ID " + id + " para actualizar en " + nombreArchivo);
    }

    public static <T> void eliminarElemento(String id, String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        if (elementos.removeIf(elemento -> obtenerId(elemento).equals(id))) {
            guardarTodo(elementos, nombreArchivo);
        } else {
            LOGGER.warning("No se encontró el elemento con ID " + id + " para eliminar en " + nombreArchivo);
        }
    }

    public static <T> T cargarElemento(String id, String nombreArchivo, Class<T> tipoClase) {
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        return elementos.stream()
                .filter(elemento -> obtenerId(elemento).equals(id))
                .findFirst()
                .orElse(null);
    }

    public static <T> List<T> cargarTodosLosElementos(String nombreArchivo, Class<T> tipoClase) {
        File file = new File(nombreArchivo);
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, tipoClase));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al cargar los elementos desde " + nombreArchivo, e);
            }
        } else {
            LOGGER.info("El archivo " + nombreArchivo + " no existe. Se creará uno nuevo.");
        }
        return new ArrayList<>();
    }

    public static <T> void guardarTodo(List<T> elementos, String nombreArchivo) {
        try {
            objectMapper.writeValue(new File(nombreArchivo), elementos);
            LOGGER.info("Elementos guardados correctamente en " + nombreArchivo);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar los elementos en " + nombreArchivo, e);
        }
    }

    private static <T> String obtenerId(T elemento) {
        try {
            return (String) elemento.getClass().getMethod("getId").invoke(elemento);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener el ID del elemento", e);
            throw new RuntimeException("El elemento no tiene un método getId válido", e);
        }
    }
}