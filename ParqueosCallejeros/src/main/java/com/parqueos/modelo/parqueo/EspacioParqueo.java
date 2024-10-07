package com.parqueos.modelo.parqueo;

import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class EspacioParqueo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_ESPACIOS = "espacios.json";

    private String id;
    private String numero;
    private boolean ocupado;
    private boolean pagado;
    private Vehiculo vehiculoActual;

    public EspacioParqueo(String numero) {
        this.id = UUID.randomUUID().toString();
        this.numero = numero;
        this.ocupado = false;
        this.pagado = false;
        this.vehiculoActual = null;
    }

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

    // Getters y setters
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
        List<EspacioParqueo> espacios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
        espacios.add(this);
        GestorArchivos.guardarTodo(espacios, ARCHIVO_ESPACIOS);
    }

    public void actualizar() {
        List<EspacioParqueo> espacios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
        for (int i = 0; i < espacios.size(); i++) {
            if (espacios.get(i).getId().equals(this.id)) {
                espacios.set(i, this);
                break;
            }
        }
        GestorArchivos.guardarTodo(espacios, ARCHIVO_ESPACIOS);
    }

    public void eliminar() {
        List<EspacioParqueo> espacios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
        espacios.removeIf(e -> e.getId().equals(this.id));
        GestorArchivos.guardarTodo(espacios, ARCHIVO_ESPACIOS);
    }

    public static EspacioParqueo cargar(String id) {
        List<EspacioParqueo> espacios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
        return espacios.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<EspacioParqueo> cargarTodos() {
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);
    }

    public void ocupar(Vehiculo vehiculo) {
        this.ocupado = true;
        this.vehiculoActual = vehiculo;
        this.actualizar();
    }

    public void liberar() {
        this.ocupado = false;
        this.pagado = false;
        this.vehiculoActual = null;
        this.actualizar();
    }

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