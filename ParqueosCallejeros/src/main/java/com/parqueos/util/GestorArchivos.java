package com.parqueos.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {
    
    public static <T> void agregarObjeto(T objeto, String nombreArchivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo, true)) {
            protected void writeStreamHeader() throws IOException {
                reset();
            }
        }) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al agregar el objeto en " + nombreArchivo);
        }
    }

    public static <T> List<T> cargarObjetos(String nombreArchivo) {
        List<T> objetos = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            while (true) {
                objetos.add((T) ois.readObject());
            }
        } catch (EOFException e) {
            // Fin del archivo, no es un error
        } catch (FileNotFoundException e) {
            System.out.println("El archivo " + nombreArchivo + " no existe. Se creará uno nuevo.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error al cargar los objetos desde " + nombreArchivo);
        }
        return objetos;
    }

    public static <T> void actualizarObjeto(T objeto, String id, String nombreArchivo) {
        List<T> objetos = cargarObjetos(nombreArchivo);
        boolean encontrado = false;
        for (int i = 0; i < objetos.size(); i++) {
            if (objetos.get(i).toString().equals(id)) {
                objetos.set(i, objeto);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            guardarObjetos(objetos, nombreArchivo);
        } else {
            System.err.println("No se encontró el objeto con ID: " + id);
        }
    }

    public static <T> void eliminarObjeto(String id, String nombreArchivo) {
        List<T> objetos = cargarObjetos(nombreArchivo);
        boolean removido = objetos.removeIf(obj -> obj.toString().equals(id));
        if (removido) {
            guardarObjetos(objetos, nombreArchivo);
        } else {
            System.err.println("No se encontró el objeto con ID: " + id);
        }
    }

    private static <T> void guardarObjetos(List<T> objetos, String nombreArchivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            for (T objeto : objetos) {
                oos.writeObject(objeto);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al guardar los objetos en " + nombreArchivo);
        }
    }

    public static <T> void guardarObjeto(T objeto, String nombreArchivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al guardar el objeto en " + nombreArchivo);
        }
    }
}