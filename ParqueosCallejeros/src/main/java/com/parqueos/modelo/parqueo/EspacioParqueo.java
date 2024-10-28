package com.parqueos.modelo.parqueo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EspacioParqueo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_ESPACIOS = "espacios.json";
    private static final Logger LOGGER = Logger.getLogger(EspacioParqueo.class.getName());

    @JsonProperty("id")
    private final String id;
    @JsonProperty("numero")
    private final String numero;
    @JsonProperty("ocupado")
    private boolean ocupado;
    @JsonProperty("pagado")
    private boolean pagado;
    @JsonProperty("vehiculoActual")
    private Vehiculo vehiculoActual;

    // Constructor del espacio de parqueo
    public EspacioParqueo(String numero) {
        this.id = UUID.randomUUID().toString();
        this.numero = numero;
        this.ocupado = false;
        this.pagado = false;
        this.vehiculoActual = null;
    }

    // Constructor sin argumentos para el JSON
    @JsonCreator
    public EspacioParqueo() {
        this.id = UUID.randomUUID().toString();
        this.numero = "";
        this.ocupado = false;
        this.pagado = false;
        this.vehiculoActual = null;
    }

    // Getters y setters
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("ocupado")
    public boolean estaOcupado() {
        return ocupado;
    }

    @JsonProperty("pagado")
    public boolean estaPagado() {
        return pagado;
    }

    @JsonProperty("pagado")
    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    @JsonProperty("numero")
    public String getNumero() {
        return numero;
    }

    @JsonProperty("vehiculoActual")
    public Vehiculo getVehiculoActual() {
        return vehiculoActual;
    }

    @JsonProperty("estaDisponible")
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

    // Metodo para cargar referencias
    public void cargarReferencias() {
        if (vehiculoActual != null) {
            try {
                this.vehiculoActual = Vehiculo.cargar(vehiculoActual.getId());
            } catch (Exception e) {
                // Log del error pero no detener la ejecución
                LOGGER.warning("No se pudo cargar el vehículo con ID: " + vehiculoActual.getId());
            }
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