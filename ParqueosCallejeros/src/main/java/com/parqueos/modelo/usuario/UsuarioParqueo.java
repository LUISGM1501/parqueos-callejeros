package com.parqueos.modelo.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.vehiculo.Vehiculo;

public class UsuarioParqueo extends Usuario {
    private String numeroTarjeta;
    private String fechaVencimientoTarjeta;
    private String codigoValidacionTarjeta;
    private final List<Vehiculo> vehiculos;
    private int tiempoGuardado; // en minutos
    private final List<Reserva> reservasActivas;

    public UsuarioParqueo(String nombre, String apellidos, int telefono, String email, String direccion, 
                          String idUsuario, String pin, String numeroTarjeta, String fechaVencimientoTarjeta, 
                          String codigoValidacionTarjeta) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin, TipoUsuario.USUARIO_PARQUEO);
        this.numeroTarjeta = numeroTarjeta;
        this.fechaVencimientoTarjeta = fechaVencimientoTarjeta;
        this.codigoValidacionTarjeta = codigoValidacionTarjeta;
        this.vehiculos = new ArrayList<>();
        this.tiempoGuardado = 0;
        this.reservasActivas = new ArrayList<>();
    }

    // Constructor sin argumentos para Jackson
    public UsuarioParqueo() {
        super("", "", 0, "", "", "", "", TipoUsuario.USUARIO_PARQUEO);
        this.vehiculos = new ArrayList<>();
        this.tiempoGuardado = 0;
        this.reservasActivas = new ArrayList<>();
    }

    // Getters y setters

    public List<Vehiculo> getVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    public int getTiempoGuardado() {
        return tiempoGuardado;
    }

    public void setTiempoGuardado(int tiempoGuardado) {
        this.tiempoGuardado = tiempoGuardado;
        this.actualizarEnArchivo();
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
        this.actualizarEnArchivo();
    }   

    public String getFechaVencimientoTarjeta() {
        return fechaVencimientoTarjeta;
    }

    public void setFechaVencimientoTarjeta(String fechaVencimientoTarjeta) {
        this.fechaVencimientoTarjeta = fechaVencimientoTarjeta;
        this.actualizarEnArchivo();
    }   

    public String getCodigoValidacionTarjeta() {
        return codigoValidacionTarjeta;
    }

    public void setCodigoValidacionTarjeta(String codigoValidacionTarjeta) {
        this.codigoValidacionTarjeta = codigoValidacionTarjeta;
        this.actualizarEnArchivo();
    }   
    
    public List<Reserva> getReservasActivas() {
        return new ArrayList<>(reservasActivas);
    }

    // Metodos

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

    public static List<UsuarioParqueo> cargarTodosUserParqueo() {
        return Usuario.cargarTodos().stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.USUARIO_PARQUEO)
                .map(u -> (UsuarioParqueo) u)
                .collect(Collectors.toList());
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        this.vehiculos.add(vehiculo);
        this.actualizarEnArchivo();
    }

    public Reserva parquear(EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        if (!vehiculos.contains(vehiculo)) {
            throw new IllegalArgumentException("El vehículo no está registrado para este usuario");
        }
        if (!espacio.estaDisponible()) {
            throw new IllegalStateException("El espacio de parqueo no está disponible");
        }
        ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
        if (tiempoComprado < config.getTiempoMinimo()) {
            throw new IllegalArgumentException("El tiempo mínimo de compra es " + config.getTiempoMinimo() + " minutos");
        }
        
        double costo = (tiempoComprado / 60.0) * config.getPrecioHora();
        
        Reserva reserva = new Reserva(this, espacio, vehiculo, tiempoComprado);
        reserva.guardar();
        reservasActivas.add(reserva);
        espacio.ocupar(vehiculo);
        this.actualizarEnArchivo();
        
        return reserva;
    }

    public void agregarTiempo(Reserva reserva, int tiempoAdicional) {
        if (!reservasActivas.contains(reserva)) {
            throw new IllegalArgumentException("La reserva no está activa para este usuario");
        }
        ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
        if (tiempoAdicional < config.getTiempoMinimo()) {
            throw new IllegalArgumentException("El tiempo mínimo de compra es " + config.getTiempoMinimo() + " minutos");
        }
        
        double costoAdicional = (tiempoAdicional / 60.0) * config.getPrecioHora();
        
        reserva.extenderTiempo(tiempoAdicional);
        reserva.actualizar();
        this.actualizarEnArchivo();
    }

    public void desaparcar(Reserva reserva) {
        if (!reservasActivas.contains(reserva)) {
            throw new IllegalArgumentException("La reserva no está activa para este usuario");
        }
        int tiempoNoUsado = reserva.finalizarReserva();
        this.tiempoGuardado += tiempoNoUsado;
        reservasActivas.remove(reserva);
        reserva.getEspacio().liberar();
        reserva.eliminar();
        this.actualizarEnArchivo();
    }

    public List<EspacioParqueo> buscarParqueosDisponibles() {
        return EspacioParqueo.cargarTodos().stream()
                .filter(EspacioParqueo::estaDisponible)
                .collect(Collectors.toList());
    }
}