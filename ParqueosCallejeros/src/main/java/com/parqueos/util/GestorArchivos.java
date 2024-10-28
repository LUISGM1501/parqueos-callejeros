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
              // Habilitar la salida con formato
              .enable(SerializationFeature.INDENT_OUTPUT)
              // Deshabilitar la escritura de fechas como timestamps
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              // Deshabilitar la falla en la deserializacion de propiedades desconocidas
              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
              // Habilitar la aceptacion de un solo valor como un array
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

    // Metodo para guardar un elemento en un archivo
    public static <T> void guardarElemento(T elemento, String nombreArchivo) {
        // Cargar todos los elementos del archivo
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, (Class<T>) elemento.getClass());
        // Agregar el elemento a la lista
        elementos.add(elemento);
        // Guardar todos los elementos en el archivo
        guardarTodo(elementos, nombreArchivo);
    }

    // Metodo para actualizar un elemento en un archivo
    public static <T> void actualizarElemento(String id, T elementoActualizado, String nombreArchivo, Class<T> tipoClase) {
        // Cargar todos los elementos del archivo
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        // Actualizar el elemento en la lista
        for (int i = 0; i < elementos.size(); i++) {
            // Verificar si el id del elemento es igual al id del elemento actualizado
            if (obtenerId(elementos.get(i)).equals(id)) {
                // Actualizar el elemento en la lista
                elementos.set(i, elementoActualizado);
                // Guardar todos los elementos en el archivo
                guardarTodo(elementos, nombreArchivo);
                return;
            }
        }
        LOGGER.warning("No se encontró el elemento con ID " + id + " para actualizar en " + nombreArchivo);
    }

    // Metodo para eliminar un elemento en un archivo
    public static <T> void eliminarElemento(String id, String nombreArchivo, Class<T> tipoClase) {
        // Cargar todos los elementos del archivo
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        // Eliminar el elemento de la lista
        if (elementos.removeIf(elemento -> obtenerId(elemento).equals(id))) {
            // Guardar todos los elementos en el archivo
            guardarTodo(elementos, nombreArchivo);
        } else {
            LOGGER.warning("No se encontró el elemento con ID " + id + " para eliminar en " + nombreArchivo);
        }
    }

    // Metodo para cargar un elemento en un archivo
    public static <T> T cargarElemento(String id, String nombreArchivo, Class<T> tipoClase) {
        // Cargar todos los elementos del archivo
        List<T> elementos = cargarTodosLosElementos(nombreArchivo, tipoClase);
        // Buscar el elemento por id
        return elementos.stream()
                // Filtrar el elemento por id
                .filter(elemento -> obtenerId(elemento).equals(id))
                // Retornar el primer elemento encontrado
                .findFirst()
                // Retornar null si no se encuentra el elemento
                .orElse(null);
    }

    // Metodo para cargar todos los elementos en un archivo
    public static <T> List<T> cargarTodosLosElementos(String nombreArchivo, Class<T> tipoClase) {
        try {
            // Crear un archivo
            File archivo = new File(nombreArchivo);
            // Verificar si el archivo existe y no está vacío
            if (!archivo.exists() || archivo.length() == 0) {
                // Retornar una lista vacía
                LOGGER.info("Archivo " + nombreArchivo + " no existe o está vacío. Retornando lista vacía.");
                return new ArrayList<>();
            }
            // Crear un tipo de colección
            CollectionType tipo = objectMapper.getTypeFactory().constructCollectionType(List.class, tipoClase);
            // Leer el archivo y retornar los elementos
            return objectMapper.readValue(archivo, tipo);
            
        } catch (Exception e) {
            // Loggear el error
            LOGGER.severe("Error al cargar elementos desde " + nombreArchivo + ": " + e.getMessage());
            // Imprimir el stack trace
            e.printStackTrace();
            // Retornar una lista vacía
            return new ArrayList<>();
        }
    }

    // Metodo para guardar todos los elementos en un archivo
    public static <T> void guardarTodo(List<T> elementos, String nombreArchivo) {
        try {
            // Crear un archivo
            File archivo = new File(nombreArchivo);
            // Escribir el archivo con formato
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, elementos);
            // Loggear el éxito
            LOGGER.info("Guardado exitoso en " + nombreArchivo);
        } catch (Exception e) {
            // Loggear el error
            LOGGER.severe("Error al guardar en " + nombreArchivo + ": " + e.getMessage());
            // Imprimir el stack trace
            e.printStackTrace();
            // Lanzar una excepción
            throw new RuntimeException("Error al guardar", e);
        }
    }

    // Metodo para obtener el id de un elemento
    private static <T> String obtenerId(T elemento) {
        try {
            // Obtener el id del elemento
            return (String) elemento.getClass().getMethod("getId").invoke(elemento);
        } catch (Exception e) {
            // Loggear el error
            LOGGER.log(Level.SEVERE, "Error al obtener el ID del elemento", e);
            // Lanzar una excepción
            throw new RuntimeException("El elemento no tiene un método getId válido", e);
        }
    }

    // Metodo para debuggear un archivo
    private static void debugArchivo(String nombreArchivo) {
        try {
            // Crear un archivo
            File archivo = new File(nombreArchivo);
            // Verificar si el archivo existe
            if (archivo.exists()) {
                // Loggear el éxito
                LOGGER.info("Archivo existe: " + archivo.getAbsolutePath());
                LOGGER.info("Tamaño: " + archivo.length() + " bytes");
                LOGGER.info("Contenido: " + new String(Files.readAllBytes(archivo.toPath())));
            } else {
                // Loggear el error
                LOGGER.warning("Archivo no existe: " + archivo.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.severe("Error al debuggear archivo: " + e.getMessage());
        }
    }
}
