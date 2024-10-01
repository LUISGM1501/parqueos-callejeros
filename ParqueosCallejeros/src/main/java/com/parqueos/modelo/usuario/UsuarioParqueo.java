package com.parqueos.modelo.usuario;

import java.util.ArrayList;
import java.util.List;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.vehiculo.Vehiculo;

public class UsuarioParqueo extends Usuario {
    private String numeroTarjeta;
    private String fechaVencimientoTarjeta;
    private String codigoValidacionTarjeta;
    private List<Vehiculo> vehiculos;
    private int tiempoGuardado; // en minutos
    private List<Reserva> reservasActivas;

    public UsuarioParqueo(String nombre, String apellidos, int telefono, String email, String direccion, 
                          String idUsuario, String pin, String numeroTarjeta, String fechaVencimientoTarjeta, 
                          String codigoValidacionTarjeta) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin);
        this.numeroTarjeta = numeroTarjeta;
        this.fechaVencimientoTarjeta = fechaVencimientoTarjeta;
        this.codigoValidacionTarjeta = codigoValidacionTarjeta;
        this.vehiculos = new ArrayList<>();
        this.tiempoGuardado = 0;
        this.reservasActivas = new ArrayList<>();
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        this.vehiculos.add(vehiculo);
    }

    public Reserva parquear(EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        if (!vehiculos.contains(vehiculo)) {
            throw new IllegalArgumentException("El vehículo no está registrado para este usuario");
        }
        // Logica para guardar la reserva en el sistema
        Reserva reserva = new Reserva(this, espacio, vehiculo, tiempoComprado);
        // Aquí iria la logica para guardar la reserva en el sistema
        reservasActivas.add(reserva);
        return reserva;
    }

    public void agregarTiempo(Reserva reserva, int tiempoAdicional) {
        if (!reservasActivas.contains(reserva)) {
            throw new IllegalArgumentException("La reserva no está activa para este usuario");
        }
        reserva.extenderTiempo(tiempoAdicional);
    }

    public void desaparcar(Reserva reserva) {
        if (!reservasActivas.contains(reserva)) {
            throw new IllegalArgumentException("La reserva no esta activa para este usuario");
        }
        int tiempoNoUsado = reserva.finalizarReserva();
        this.tiempoGuardado += tiempoNoUsado;
        reservasActivas.remove(reserva);
    }

    public List<EspacioParqueo> buscarParqueosDisponibles() {
        // Falta implementar la logica para buscar espacios de parqueo disponibles
        return null; // Placeholder
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
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }   

    public String getFechaVencimientoTarjeta() {
        return fechaVencimientoTarjeta;
    }

    public void setFechaVencimientoTarjeta(String fechaVencimientoTarjeta) {
        this.fechaVencimientoTarjeta = fechaVencimientoTarjeta;
    }   

    public String getCodigoValidacionTarjeta() {
        return codigoValidacionTarjeta;
    }

    public void setCodigoValidacionTarjeta(String codigoValidacionTarjeta) {
        this.codigoValidacionTarjeta = codigoValidacionTarjeta;
    }   
    
    public List<Reserva> getReservasActivas() {
        return new ArrayList<>(reservasActivas);
    }
}