package com.parqueos.modelo.parqueo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

public class EspacioParqueo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_ESPACIOS = "espacios.json";

    private final String id;
    private final String numero;
    private boolean ocupado;
    private boolean pagado;
    private Vehiculo vehiculoActual;

    // Constructor del espacio de parqueo
    public EspacioParqueo(String numero) {
        this.id = UUID.randomUUID().toString();
        this.numero = numero;
        this.ocupado = false;
        this.pagado = false;
        this.vehiculoActual = null;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public boolean estaPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public String getNumero() {
        return numero;
    }

    public Vehiculo getVehiculoActual() {
        return vehiculoActual;
    }

    public boolean estaDisponible() {
        return !ocupado;
    }

    // Metodos
    public void guardar() {
        // Cargar todos los espacios del json
        List<EspacioParqueo> espacios = cargarTodos();

        // Agregar el espacio actual
        espacios.add(this);

        // Guardar todos los espacios en el json
        guardarTodos(espacios);
    }

    public void actualizar() {
        // Cargar todos los espacios del json
        List<EspacioParqueo> espacios = cargarTodos();

        // Actualizar el espacio actual
        for (int i = 0; i < espacios.size(); i++) {
            if (espacios.get(i).getId().equals(this.id)) {
                // Actualizar el espacio actual
                espacios.set(i, this);
                break;
            }
        }

        // Guardar todos los espacios en el json
        guardarTodos(espacios);
    }

    // Metodo para eliminar un espacio del parqueo
    public void eliminar() {
        // Cargar todos los espacios del json
        List<EspacioParqueo> espacios = cargarTodos();

        // Eliminar el espacio actual
        espacios.removeIf(e -> e.getId().equals(this.id));

        // Guardar todos los espacios en el json
        guardarTodos(espacios);
    }

    // Metodo para cargar un espacio del parqueo
    public static EspacioParqueo cargar(String id) {
        // Cargar todos los espacios del json
        List<EspacioParqueo> espacios = cargarTodos();

        // Buscar el espacio con el id
        return espacios.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Metodo para cargar todos los espacios del parqueo
    public static List<EspacioParqueo> cargarTodos() {
        // Cargar todos los espacios del json
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
    }

    // Metodo para guardar todos los espacios del parqueo
    public static void guardarTodos(List<EspacioParqueo> espacios) {
        // Guardar todos los espacios en el json
        GestorArchivos.guardarTodo(espacios, ARCHIVO_ESPACIOS);
    }

    // Metodo para ocupar un espacio del parqueo
    public void ocupar(Vehiculo vehiculo) {
        // Verificar si el espacio esta disponible
        if (this.estaDisponible()) {
            // Ocupar el espacio
            this.ocupado = true;
            this.vehiculoActual = vehiculo;

            // Guardar en el json
            this.actualizar();
        }
    }

    // Metodo para liberar un espacio del parqueo
    public void liberar() {
        // Verificar si el espacio esta ocupado
        if (this.estaOcupado()) {
            // Liberar el espacio
            this.ocupado = false;
            this.pagado = false;
            this.vehiculoActual = null;

            // Guardar en el json
            this.actualizar();
        }
    }

    // Metodo para convertir el espacio de parqueo a un string
    @Override
    public String toString() {
        return "EspacioParqueo{" +
                "numero='" + numero + '\'' +
                ", ocupado=" + ocupado +
                ", pagado=" + pagado +
                ", vehiculoActual=" + (vehiculoActual != null ? vehiculoActual.getPlaca() : "ninguno") +
                '}';
    }
}