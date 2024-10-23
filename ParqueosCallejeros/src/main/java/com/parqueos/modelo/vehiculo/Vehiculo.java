package com.parqueos.modelo.vehiculo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.util.GestorArchivos;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_VEHICULOS = "vehiculos.json";

    @JsonProperty("id")
    private final String id;

    @JsonProperty("placa")
    private String placa;

    @JsonProperty("marca")
    private String marca;

    @JsonProperty("modelo")
    private String modelo;

    @JsonBackReference
    private transient UsuarioParqueo propietario;

    @JsonProperty("propietarioId")
    private String propietarioId;

    // Constructor de vehiculo
    public Vehiculo(String placa, String marca, String modelo, UsuarioParqueo propietario) {
        this.id = UUID.randomUUID().toString();
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.propietario = propietario;
        this.propietarioId = propietario != null ? propietario.getId() : null;
    }

    // Constructor sin argumentos para el JSON
    @JsonCreator
    public Vehiculo(@JsonProperty("id") String id,
                    @JsonProperty("placa") String placa,
                    @JsonProperty("marca") String marca,
                    @JsonProperty("modelo") String modelo,
                    @JsonProperty("propietarioId") String propietarioId) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.propietarioId = propietarioId;
    }

    // Getters y setters
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("placa")
    public String getPlaca() {
        return placa;
    }

    @JsonProperty("placa")
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    @JsonProperty("marca")
    public String getMarca() {
        return marca;
    }

    @JsonProperty("marca")
    public void setMarca(String marca) {
        this.marca = marca;
    }

    @JsonProperty("modelo")
    public String getModelo() {
        return modelo;
    }

    @JsonProperty("modelo")
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @JsonProperty("propietario")
    public UsuarioParqueo getPropietario() {
        return propietario;
    }

    @JsonBackReference
    public void setPropietario(UsuarioParqueo propietario) {
        this.propietario = propietario;
        this.propietarioId = propietario != null ? propietario.getId() : null;
    }

    @JsonProperty("propietarioId")
    public String getPropietarioId() {
        return propietarioId;
    }

    @JsonProperty("propietarioId")
    public void setPropietarioId(String propietarioId) {
        this.propietarioId = propietarioId;
    }

    // Metodos
    public void guardar() {
        // Asegurar que el propietarioId esté sincronizado antes de guardar
        if (propietario != null) {
            this.propietarioId = propietario.getId();
        }

        List<Vehiculo> vehiculos = cargarTodos();
        
        // Actualizar si ya existe
        boolean encontrado = false;
        for (int i = 0; i < vehiculos.size(); i++) {
            if (vehiculos.get(i).getId().equals(this.id)) {
                vehiculos.set(i, this);
                encontrado = true;
                break;
            }
        }
        
        // Agregar si no existe
        if (!encontrado) {
            vehiculos.add(this);
        }

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

    // Metodo para cargar el propietario del vehiculo
    public void cargarPropietario() {
        // Cargar el propietario del vehiculo
        this.propietario = (UsuarioParqueo) Usuario.cargar(this.propietarioId);
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