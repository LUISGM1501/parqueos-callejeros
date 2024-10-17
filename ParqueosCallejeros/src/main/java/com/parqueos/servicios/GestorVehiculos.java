package com.parqueos.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

public class GestorVehiculos {
    private static final String ARCHIVO_VEHICULOS = "vehiculos.json";
    private static final Logger LOGGER = Logger.getLogger(GestorVehiculos.class.getName());
    
    private List<Vehiculo> vehiculos;

    public GestorVehiculos() {
        this.vehiculos = new ArrayList<>();
        cargarVehiculos();
    }

    public void cargarVehiculos() {
        try {
            vehiculos = GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
            LOGGER.info("Vehículos cargados exitosamente. Total: " + vehiculos.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar vehículos", e);
            vehiculos = new ArrayList<>();
        }
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
        guardarVehiculos();
        LOGGER.info("Vehículo agregado: " + vehiculo.getId());
    }

    public void actualizarVehiculo(Vehiculo vehiculo) {
        int index = vehiculos.indexOf(vehiculo);
        if (index != -1) {
            vehiculos.set(index, vehiculo);
            guardarVehiculos();
            LOGGER.info("Vehículo actualizado: " + vehiculo.getId());
        } else {
            LOGGER.warning("No se pudo actualizar el vehículo: " + vehiculo.getId());
        }
    }

    public void eliminarVehiculo(String id) {
        boolean removido = vehiculos.removeIf(v -> v.getId().equals(id));
        if (removido) {
            guardarVehiculos();
            LOGGER.info("Vehículo eliminado: " + id);
        } else {
            LOGGER.warning("No se pudo eliminar el vehículo: " + id);
        }
    }

    public List<Vehiculo> obtenerVehiculosPorUsuario(String idUsuario) {
        return vehiculos.stream()
                .filter(v -> v.getPropietario() != null && v.getPropietario().getId().equals(idUsuario))
                .collect(Collectors.toList());
    }

    public Vehiculo buscarVehiculo(String id) {
        return vehiculos.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Vehiculo> obtenerTodosLosVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    private void guardarVehiculos() {
        try {
            GestorArchivos.guardarTodo(vehiculos, ARCHIVO_VEHICULOS);
            LOGGER.info("Vehículos guardados exitosamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar vehículos", e);
        }
    }
}