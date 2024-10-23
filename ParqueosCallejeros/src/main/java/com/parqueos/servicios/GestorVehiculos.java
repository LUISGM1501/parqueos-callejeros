package com.parqueos.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar los vehiculos
public class GestorVehiculos {
    // Archivo de vehiculos
    private static final String ARCHIVO_VEHICULOS = "vehiculos.json";
    private static final Logger LOGGER = Logger.getLogger(GestorVehiculos.class.getName());
    
    private List<Vehiculo> vehiculos = new ArrayList<>();

    // Constructor para inicializar la lista de vehiculos
    public GestorVehiculos() {
        cargarVehiculos();
    }

    // Metodo para cargar los vehiculos
    public void cargarVehiculos() {
        try {
            // Cargar los vehiculos del archivo json
            vehiculos = GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
            if (vehiculos == null) {
                vehiculos = new ArrayList<>();
            }
            // Mensaje de confirmacion
            LOGGER.info("Vehículos cargados exitosamente. Total: " + vehiculos.size());
        } catch (Exception e) {
            // Mensaje de error
            LOGGER.severe("Error al cargar vehículos: " + e.getMessage());
            // Inicializar la lista de vehiculos
            vehiculos = new ArrayList<>();
        }
    }

    // Metodo para agregar un vehiculo
    public void agregarVehiculo(Vehiculo vehiculo) {
        // Agregar el vehiculo a la lista de vehiculos
        vehiculos.add(vehiculo);
        // Guardar los vehiculos
        guardarVehiculos();
        // Mensaje de confirmacion
        LOGGER.info("Vehículo agregado al gestor: " + vehiculo.getId());
    }

    // Metodo para actualizar un vehiculo
    public void actualizarVehiculo(Vehiculo vehiculo) {
        // Buscar el vehiculo en la lista de vehiculos
        int index = vehiculos.indexOf(vehiculo);

        // Si el vehiculo existe, actualizarlo
        if (index != -1) {
            vehiculos.set(index, vehiculo);
            // Guardar los vehiculos
            guardarVehiculos();
            // Mensaje de confirmacion
            LOGGER.info("Vehículo actualizado: " + vehiculo.getId());
        } else {
            // Mensaje de error
            LOGGER.warning("No se pudo actualizar el vehículo: " + vehiculo.getId());
        }
    }

    // Metodo para eliminar un vehiculo
    public void eliminarVehiculo(String id) {
        // Eliminar el vehiculo de la lista de vehiculos
        boolean removido = vehiculos.removeIf(v -> v.getId().equals(id));

        // Si se elimino el vehiculo, guardar los vehiculos
        if (removido) {
            // Guardar los vehiculos
            guardarVehiculos();
            // Mensaje de confirmacion
            LOGGER.info("Vehículo eliminado: " + id);
        } else {
            // Mensaje de error
            LOGGER.warning("No se pudo eliminar el vehículo: " + id);
        }
    }

    // Metodo para obtener los vehiculos por usuario
    public List<Vehiculo> obtenerVehiculosPorUsuario(String idUsuario) {
        // Retornar una lista de vehiculos filtrados por el id del usuario
        return vehiculos.stream()
                .filter(v -> idUsuario.equals(v.getPropietarioId()))
                .collect(Collectors.toList());
    }

    // Metodo para buscar un vehiculo por id
    public Vehiculo buscarVehiculo(String id) {
        // Retornar el vehiculo buscado
        return vehiculos.stream()
                // Filtrar el vehiculo por id
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Metodo para obtener todos los vehiculos
    public List<Vehiculo> obtenerTodosLosVehiculos() {
        // Retornar una lista de vehiculos
        return new ArrayList<>(vehiculos);
    }

    // Metodo para guardar los vehiculos
    private void guardarVehiculos() {
        try {
            // Guardar los vehiculos en el archivo json
            GestorArchivos.guardarTodo(vehiculos, ARCHIVO_VEHICULOS);
            // Mensaje de confirmacion
            LOGGER.info("Vehículos guardados exitosamente");
        } catch (Exception e) {
            // Mensaje de error
            LOGGER.severe("Error al guardar vehículos: " + e.getMessage());
        }
    }

    // Metodo para inicializar la lista de vehiculos
    public void inicializarListaVacia() {
        // Cuando no se puede cargar los vehiculos del archivo json
        vehiculos = new ArrayList<>();
    }
}