package com.parqueos.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parqueos.modelo.usuario.UsuarioParqueo;

public class GestorArchivos {
    private static final Logger LOGGER = Logger.getLogger(GestorArchivos.class.getName());
    private static final ObjectMapper objectMapper = configurarObjectMapper();

    private static ObjectMapper configurarObjectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .allowIfSubType(Object.class)
                .allowIfSubType("com.parqueos.modelo.*")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setDefaultTyping(new DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL, ptv)
                    .init(JsonTypeInfo.Id.CLASS, null)
                    .inclusion(JsonTypeInfo.As.PROPERTY));

        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        
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
        File archivo = new File(nombreArchivo);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        
        try {
            String contenido = new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8);
            JavaType tipo = objectMapper.getTypeFactory().constructCollectionType(List.class, tipoClase);
            
            List<T> elementos = objectMapper.readValue(contenido, tipo);
            
            // Recuperar referencias para UsuarioParqueo
            if (!elementos.isEmpty() && elementos.get(0) instanceof UsuarioParqueo) {
                for (T elemento : elementos) {
                    ((UsuarioParqueo)elemento).recuperarReferencias();
                }
            }
            
            return elementos;
        } catch (Exception e) {
            LOGGER.severe("Error al cargar elementos desde " + nombreArchivo + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static synchronized <T> void guardarTodo(List<T> elementos, String nombreArchivo) {
        try {
            // Crear una copia de seguridad antes de guardar
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                // Crear un backup del archivo
                File backup = new File(nombreArchivo + ".bak");
                if (backup.exists()) {
                    // Si el backup existe, eliminarlo
                    backup.delete();
                }
                // Renombrar el archivo original al backup
                archivo.renameTo(backup);
            }
            
            // Guardar los nuevos datos
            objectMapper.writeValue(new File(nombreArchivo), elementos);
            
            // Si el guardado fue exitoso, eliminar el backup
            File backup = new File(nombreArchivo + ".bak");
            if (backup.exists()) {
                // Si el backup existe, eliminarlo
                backup.delete();
            }
            
        } catch (IOException e) {
            LOGGER.severe("Error al guardar elementos en " + nombreArchivo + ": " + e.getMessage());
            
            // Si hubo error, restaurar desde el backup
            File backup = new File(nombreArchivo + ".bak");
            if (backup.exists()) {
                // Si el backup existe, restaurarlo
                backup.renameTo(new File(nombreArchivo));
            }
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
