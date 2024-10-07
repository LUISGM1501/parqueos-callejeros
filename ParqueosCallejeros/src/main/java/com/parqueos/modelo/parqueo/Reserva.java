package com.parqueos.modelo.parqueo;

import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_RESERVAS = "reservas.json";
    
    private final String idReserva;
    private final UsuarioParqueo usuario;
    private final EspacioParqueo espacio;
    private final Vehiculo vehiculo;
    private final LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private boolean activa;

    public Reserva(UsuarioParqueo usuario, EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        this.idReserva = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.espacio = espacio;
        this.vehiculo = vehiculo;
        this.horaInicio = LocalDateTime.now();
        this.horaFin = horaInicio.plusMinutes(tiempoComprado);
        this.activa = true;
        
        if (!espacio.estaDisponible()) {
            throw new IllegalStateException("El espacio no está disponible");
        }
        espacio.ocupar(vehiculo);
    }

    public void guardar() {
        List<Reserva> reservas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
        reservas.add(this);
        GestorArchivos.guardarTodo(reservas, ARCHIVO_RESERVAS);
    }

    public void actualizar() {
        List<Reserva> reservas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getIdReserva().equals(this.idReserva)) {
                reservas.set(i, this);
                break;
            }
        }
        GestorArchivos.guardarTodo(reservas, ARCHIVO_RESERVAS);
    }

    public void eliminar() {
        List<Reserva> reservas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
        reservas.removeIf(r -> r.getIdReserva().equals(this.idReserva));
        GestorArchivos.guardarTodo(reservas, ARCHIVO_RESERVAS);
    }

    public static Reserva cargar(String id) {
        List<Reserva> reservas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
        return reservas.stream()
                .filter(r -> r.getIdReserva().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<Reserva> cargarTodas() {
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
    }

    public void extenderTiempo(int tiempoAdicional) {
        if (!activa) {
            throw new IllegalStateException("No se puede extender el tiempo de una reserva inactiva");
        }
        this.horaFin = this.horaFin.plusMinutes(tiempoAdicional);
        this.actualizar();
    }

    public int finalizarReserva() {
        if (!activa) {
            throw new IllegalStateException("Esta reserva ya ha sido finalizada");
        }
        LocalDateTime ahora = LocalDateTime.now();
        int tiempoNoUsado = 0;
        
        if (ahora.isBefore(horaFin)) {
            tiempoNoUsado = (int) java.time.Duration.between(ahora, horaFin).toMinutes();
        }
        
        this.activa = false;
        espacio.liberar();
        this.actualizar();
        return tiempoNoUsado;
    }

    public boolean estaActiva() {
        return activa && LocalDateTime.now().isBefore(horaFin);
    }

    // Getters
    public String getIdReserva() {
        return idReserva;
    }

    public UsuarioParqueo getUsuario() {
        return usuario;
    }

    public EspacioParqueo getEspacio() {
        return espacio;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva='" + idReserva + '\'' +
                ", usuario=" + usuario.getIdUsuario() +
                ", espacio=" + espacio.getNumero() +
                ", vehiculo=" + vehiculo.getPlaca() +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", activa=" + activa +
                '}';
    }
}