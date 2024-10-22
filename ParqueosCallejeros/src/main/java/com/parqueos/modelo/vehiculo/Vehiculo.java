package com.parqueos.modelo.vehiculo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.util.GestorArchivos;

public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_VEHICULOS = "vehiculos.json";

    private final String id;
    private String placa;
    private String marca;
    private String modelo;
    private String propietario;

    // Constructor de vehiculo
    public Vehiculo(String placa, String marca, String modelo, String propietario) {
        this.id = UUID.randomUUID().toString();
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.propietario = propietario;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String  propietario) {
        this.propietario = propietario;
    }

    // Metodos
    public void guardar() {
        // Cargar todos los vehiculos
        List<Vehiculo> vehiculos = cargarTodos();

        // Agregar el vehiculo actual
        vehiculos.add(this);

        // Guardar todos los vehiculos en el archivo json
        guardarTodos(vehiculos);
    }

    // Metodo para actualizar un vehiculo
    public void actualizar() {
        // Cargar todos los vehiculos
        List<Vehiculo> vehiculos = cargarTodos();

        // Buscar el vehiculo actual y actualizarlo
        for (int i = 0; i < vehiculos.size(); i++) {
            if (vehiculos.get(i).getId().equals(this.id)) {
                // Actualizar el vehiculo
                vehiculos.set(i, this);
                break;
            }
        }

        // Guardar todos los vehiculos en el archivo json
        guardarTodos(vehiculos);
    }

    // Metodo para eliminar un vehiculo
    public void eliminar() {
        // Cargar todos los vehiculos
        List<Vehiculo> vehiculos = cargarTodos();

        // Eliminar el vehiculo actual
        vehiculos.removeIf(v -> v.getId().equals(this.id));

        // Guardar todos los vehiculos en el archivo json
        guardarTodos(vehiculos);
    }

    // Metodo para cargar un vehiculo por su id
    public static Vehiculo cargar(String id) {
        // Cargar todos los vehiculos
        List<Vehiculo> vehiculos = cargarTodos();

        // Buscar el vehiculo por su id
        return vehiculos.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Metodo para cargar todos los vehiculos
    public static List<Vehiculo> cargarTodos() {
        // Cargar todos los vehiculos del archivo json
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
    }

    // Metodo para guardar todos los vehiculos
    public static void guardarTodos(List<Vehiculo> vehiculos) {
        // Guardar todos los vehiculos en el archivo json
        GestorArchivos.guardarTodo(vehiculos, ARCHIVO_VEHICULOS);
    }

    // Metodo para convertir a string
    @Override
    public String toString() {
        return "Vehiculo{" +
                "id='" + id + '\'' +
                ", placa='" + placa + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                '}';
    }
}