package com.parqueos.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;

public class GestorArchivos {
    private static final Logger LOGGER = Logger.getLogger(GestorArchivos.class.getName());
    private static final ObjectMapper objectMapper = configurarObjectMapper();

    private static ObjectMapper configurarObjectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .allowIfSubType(Object.class)
                .allowIfSubType(Usuario.class)
                .allowIfSubType(Administrador.class)
                .allowIfSubType(UsuarioParqueo.class)
                .allowIfSubType(Inspector.class)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .registerModule(new JavaTimeModule())
            .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

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
            LOGGER.info("El archivo " + nombreArchivo + " no existe o está vacío. Se creará uno nuevo.");
            return new ArrayList<>();
        }
        
        try {
            String contenido = new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8);
            JavaType tipo = objectMapper.getTypeFactory().constructCollectionType(List.class, tipoClase);
            
            // Intenta deserializar directamente
            try {
                return objectMapper.readValue(contenido, tipo);
            } catch (Exception e) {
                LOGGER.warning("Error en la deserialización directa, intentando con un enfoque alternativo: " + e.getMessage());
                // Si falla, intenta deserializar como un array simple
                JsonNode root = objectMapper.readTree(contenido);
                List<T> resultado = new ArrayList<>();
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        try {
                            T elemento = objectMapper.treeToValue(node, tipoClase);
                            resultado.add(elemento);
                        } catch (Exception ex) {
                            LOGGER.warning("Error al deserializar elemento individual: " + ex.getMessage());
                        }
                    }
                }
                return resultado;
            }
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
