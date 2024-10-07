package com.parqueos.modelo.vehiculo;

import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.util.GestorArchivos;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_VEHICULOS = "vehiculos.json";

    private String id;
    private String placa;
    private String marca;
    private String modelo;
    private UsuarioParqueo propietario;

    public Vehiculo(String placa, String marca, String modelo, UsuarioParqueo propietario) {
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

    public UsuarioParqueo getPropietario() {
        return propietario;
    }

    public void setPropietario(UsuarioParqueo  propietario) {
        this.propietario = propietario;
    }

    // Metodos
    public void guardar() {
        List<Vehiculo> vehiculos = GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
        vehiculos.add(this);
        GestorArchivos.guardarTodo(vehiculos, ARCHIVO_VEHICULOS);
    }

    public void actualizar() {
        List<Vehiculo> vehiculos = GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
        for (int i = 0; i < vehiculos.size(); i++) {
            if (vehiculos.get(i).getId().equals(this.id)) {
                vehiculos.set(i, this);
                break;
            }
        }
        GestorArchivos.guardarTodo(vehiculos, ARCHIVO_VEHICULOS);
    }

    public void eliminar() {
        List<Vehiculo> vehiculos = GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
        vehiculos.removeIf(v -> v.getId().equals(this.id));
        GestorArchivos.guardarTodo(vehiculos, ARCHIVO_VEHICULOS);
    }

    public static Vehiculo cargar(String id) {
        List<Vehiculo> vehiculos = GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
        return vehiculos.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<Vehiculo> cargarTodos() {
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_VEHICULOS, Vehiculo.class);
    }

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