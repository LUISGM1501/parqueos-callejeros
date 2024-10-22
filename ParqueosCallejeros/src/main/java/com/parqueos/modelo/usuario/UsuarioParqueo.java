package com.parqueos.modelo.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.vehiculo.Vehiculo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioParqueo extends Usuario {
    @JsonProperty("numeroTarjeta")
    private String numeroTarjeta;
    @JsonProperty("fechaVencimientoTarjeta")
    private String fechaVencimientoTarjeta;
    @JsonProperty("codigoValidacionTarjeta")
    private String codigoValidacionTarjeta;
    @JsonManagedReference
    @JsonProperty("vehiculos")
    private List<Vehiculo> vehiculos;
    @JsonProperty("tiempoGuardado")
    private int tiempoGuardado; // en minutos
    @JsonIgnore
    private final List<Reserva> reservasActivas;

    // Constructor de usuario parqueo
    public UsuarioParqueo(String nombre, String apellidos, int telefono, String email, 
                         String direccion, String idUsuario, String pin,
                         String numeroTarjeta, String fechaVencimientoTarjeta, 
                         String codigoValidacionTarjeta) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin, TipoUsuario.USUARIO_PARQUEO);
        this.numeroTarjeta = numeroTarjeta;
        this.fechaVencimientoTarjeta = fechaVencimientoTarjeta;
        this.codigoValidacionTarjeta = codigoValidacionTarjeta;
        this.vehiculos = new ArrayList<>();
        this.reservasActivas = new ArrayList<>();
    }

    // Constructor sin argumentos para el JSON 
    @JsonCreator
    public UsuarioParqueo() {
        super("", "", 0, "", "", "", "", TipoUsuario.USUARIO_PARQUEO);
        this.vehiculos = new ArrayList<>();
        this.reservasActivas = new ArrayList<>();
    }

    // Getters y setters

    @JsonProperty("vehiculos")
    public List<Vehiculo> getVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    @JsonProperty("vehiculos")
    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    @JsonProperty("tiempoGuardado")
    public int getTiempoGuardado() {
        return tiempoGuardado;
    }

    @JsonProperty("tiempoGuardado")
    public void setTiempoGuardado(int tiempoGuardado) {
        this.tiempoGuardado = tiempoGuardado;
    }

    @JsonProperty("numeroTarjeta")
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    @JsonProperty("numeroTarjeta")
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }   

    @JsonProperty("fechaVencimientoTarjeta")
    public String getFechaVencimientoTarjeta() {
        return fechaVencimientoTarjeta;
    }

    @JsonProperty("fechaVencimientoTarjeta")
    public void setFechaVencimientoTarjeta(String fechaVencimientoTarjeta) {
        this.fechaVencimientoTarjeta = fechaVencimientoTarjeta;
    }   

    @JsonProperty("codigoValidacionTarjeta")
    public String getCodigoValidacionTarjeta() {
        return codigoValidacionTarjeta;
    }

    @JsonProperty("codigoValidacionTarjeta")
    public void setCodigoValidacionTarjeta(String codigoValidacionTarjeta) {
        this.codigoValidacionTarjeta = codigoValidacionTarjeta;
    }   
    
    @JsonProperty("reservasActivas")
    public List<Reserva> getReservasActivas() {
        return new ArrayList<>(reservasActivas);
    }

    // Metodos

    // Metodo para actualizar un usuario en el archivo
    @Override
    public void actualizarEnArchivo() {
        super.actualizarEnArchivo();
        // Actualizar vehículos y reservas si es necesario
        for (Vehiculo vehiculo : vehiculos) {
            vehiculo.actualizar();
        }
        for (Reserva reserva : reservasActivas) {
            reserva.actualizar();
        }
    }

    // Metodo para cargar todos los usuarios parqueo
    public static List<UsuarioParqueo> cargarTodosUserParqueo() {
        // Cargar todos los usuarios
        List<Usuario> usuarios = Usuario.cargarTodos();

        // Filtrar los usuarios parqueo
        return usuarios.stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.USUARIO_PARQUEO)
                .map(u -> (UsuarioParqueo) u)
                .collect(Collectors.toList());
    }

    // Metodo para agregar un vehiculo
    public void agregarVehiculo(Vehiculo vehiculo) {
        // Cargar vehiculos del archivo Json
        List<Vehiculo> vehiculosCargados = Vehiculo.cargarTodos();

        // Agregar el vehiculo actual
        vehiculosCargados.add(vehiculo);

        // Guardar los vehiculos en el archivo Json
        Vehiculo.guardarTodos(vehiculosCargados);

        // Agregar el vehiculo actual
        this.vehiculos.add(vehiculo);

        // Actualizar el usuario en el archivo Json
        this.actualizarEnArchivo();
    }

    // Metodo para parquear un vehiculo
    public Reserva parquear(EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        // Verificar si el vehiculo esta registrado para este usuario
        if (!vehiculos.contains(vehiculo)) {
            throw new IllegalArgumentException("El vehículo no está registrado para este usuario");
        }

        // Verificar si el espacio de parqueo esta disponible
        if (!espacio.estaDisponible()) {
            throw new IllegalStateException("El espacio de parqueo no está disponible");
        }

        // Verificar si el tiempo comprado es mayor o igual al tiempo minimo
        ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
        if (tiempoComprado < config.getTiempoMinimo()) {
            throw new IllegalArgumentException("El tiempo mínimo de compra es " + config.getTiempoMinimo() + " minutos");
        }
        
        // Calcular el costo de la reserva
        double costo = (tiempoComprado / 60.0) * config.getPrecioHora();
        
        // Crear una nueva reserva
        Reserva reserva = new Reserva(this, espacio, vehiculo, tiempoComprado);
        reserva.guardar();
        reservasActivas.add(reserva);
        espacio.ocupar(vehiculo);
        this.actualizarEnArchivo();
        
        // Retornar la reserva
        return reserva;
    }
    
    // Metodo para agregar tiempo a una reserva
    public void agregarTiempo(Reserva reserva, int tiempoAdicional) {
        // Verificar si la reserva esta activa para este usuario
        if (!reservasActivas.contains(reserva)) {
            throw new IllegalArgumentException("La reserva no está activa para este usuario");
        }

        // Verificar si el tiempo adicional es mayor o igual al tiempo minimo
        ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
        if (tiempoAdicional < config.getTiempoMinimo()) {
            throw new IllegalArgumentException("El tiempo mínimo de compra es " + config.getTiempoMinimo() + " minutos");
        }

        // Calcular el costo adicional
        double costoAdicional = (tiempoAdicional / 60.0) * config.getPrecioHora();
        
        // Agregar el tiempo adicional a la reserva
        reserva.extenderTiempo(tiempoAdicional);

        // Actualizar la reserva en el archivo Json
        reserva.actualizar();

        // Actualizar el usuario en el archivo Json
        this.actualizarEnArchivo();
    }

    // Metodo para desaparcar un vehiculo
    public void desaparcar(Reserva reserva) {
        // Verificar si la reserva esta activa para este usuario
        if (!reservasActivas.contains(reserva)) {
            throw new IllegalArgumentException("La reserva no está activa para este usuario");
        }

        // Finalizar la reserva
        int tiempoNoUsado = reserva.finalizarReserva();

        // Agregar el tiempo no usado al tiempo guardado
        this.tiempoGuardado += tiempoNoUsado;

        // Eliminar la reserva de las reservas activas
        reservasActivas.remove(reserva);
        
        // Eliminar la reserva del archivo Json
        reserva.eliminar();

        // Actualizar el usuario en el archivo Json
        this.actualizarEnArchivo();
    }

    // Metodo para buscar espacios de parqueo disponibles
    public List<EspacioParqueo> buscarParqueosDisponibles() {
        // Cargar todos los espacios de parqueo
        List<EspacioParqueo> espacios = EspacioParqueo.cargarTodos();

        // Filtrar los espacios de parqueo disponibles
        return espacios.stream()
                .filter(EspacioParqueo::estaDisponible)
                .collect(Collectors.toList());
    }
}
