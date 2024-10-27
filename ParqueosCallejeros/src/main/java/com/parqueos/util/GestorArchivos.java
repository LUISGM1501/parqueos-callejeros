package com.parqueos.util;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class GestorArchivos {
    private static final ObjectMapper objectMapper = configurarObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(GestorArchivos.class.getName());

    private static ObjectMapper configurarObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configuraciones básicas
        mapper.registerModule(new JavaTimeModule())
              .enable(SerializationFeature.INDENT_OUTPUT)
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
              .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        // Configuración de visibilidad
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Manejo de tipos - usando el método moderno en lugar del deprecado
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );

        return mapper;
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
        try {
            File archivo = new File(nombreArchivo);
            
            if (!archivo.exists() || archivo.length() == 0) {
                LOGGER.info("Archivo " + nombreArchivo + " no existe o está vacío. Retornando lista vacía.");
                return new ArrayList<>();
            }

            CollectionType tipo = objectMapper.getTypeFactory().constructCollectionType(List.class, tipoClase);
            return objectMapper.readValue(archivo, tipo);
            
        } catch (Exception e) {
            LOGGER.severe("Error al cargar elementos desde " + nombreArchivo + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static <T> void guardarTodo(List<T> elementos, String nombreArchivo) {
        try {
            File archivo = new File(nombreArchivo);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, elementos);
            LOGGER.info("Guardado exitoso en " + nombreArchivo);
        } catch (Exception e) {
            LOGGER.severe("Error al guardar en " + nombreArchivo + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar", e);
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

    private static void debugArchivo(String nombreArchivo) {
        try {
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                LOGGER.info("Archivo existe: " + archivo.getAbsolutePath());
                LOGGER.info("Tamaño: " + archivo.length() + " bytes");
                LOGGER.info("Contenido: " + new String(Files.readAllBytes(archivo.toPath())));
            } else {
                LOGGER.warning("Archivo no existe: " + archivo.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.severe("Error al debuggear archivo: " + e.getMessage());
        }
    }
}
