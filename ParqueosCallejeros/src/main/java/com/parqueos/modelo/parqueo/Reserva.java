package com.parqueos.modelo.parqueo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

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

    // Constructor de la reserva
    public Reserva(UsuarioParqueo usuario, EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        this.idReserva = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.espacio = espacio;
        this.vehiculo = vehiculo;
        this.horaInicio = LocalDateTime.now();
        this.horaFin = horaInicio.plusMinutes(tiempoComprado);
        this.activa = true;
        
        // Verificar si el espacio esta disponible
        if (!espacio.estaDisponible()) {
            // Lanzar una excepcion si el espacio no esta disponible
            throw new IllegalStateException("El espacio no está disponible");
        }

        // Ocupar el espacio
        espacio.ocupar(vehiculo);
    }

    // Metodo para guardar una reserva
    public void guardar() {
        // Cargar todas las reservas del json
        List<Reserva> reservas = cargarTodas();

        // Agregar la reserva actual
        reservas.add(this);

        // Guardar todas las reservas en el json
        guardarTodas(reservas);
    }

    // Metodo para actualizar una reserva
    public void actualizar() {
        // Cargar todas las reservas del json
        List<Reserva> reservas = cargarTodas();

        // Buscar la reserva actual y actualizarla
        for (int i = 0; i < reservas.size(); i++) {

            // Verificar si la reserva actual es la misma que la reserva actual
            if (reservas.get(i).getIdReserva().equals(this.idReserva)) {
                // Actualizar la reserva
                reservas.set(i, this);
                break;
            }
        }

        // Guardar todas las reservas en el json
        guardarTodas(reservas);
    }

    // Metodo para eliminar una reserva
    public void eliminar() {
        // Cargar todas las reservas del json
        List<Reserva> reservas = cargarTodas();

        // Eliminar la reserva actual
        reservas.removeIf(r -> r.getIdReserva().equals(this.idReserva));

        // Guardar todas las reservas en el json
        guardarTodas(reservas);
    }

    // Metodo para cargar una reserva
    public static Reserva cargar(String id) {
        // Cargar todas las reservas del json
        List<Reserva> reservas = cargarTodas();

        // Buscar la reserva actual
        return reservas.stream()
                .filter(r -> r.getIdReserva().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Metodo para cargar todas las reservas
    public static List<Reserva> cargarTodas() {
        // Cargar todas las reservas del json
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
    }

    // Metodo para guardar todas las reservas
    public static void guardarTodas(List<Reserva> reservas) {
        // Guardar todas las reservas en el json
        GestorArchivos.guardarTodo(reservas, ARCHIVO_RESERVAS);
    }

    // Metodo para extender el tiempo de una reserva
    public void extenderTiempo(int tiempoAdicional) {
        // Verificar si la reserva esta activa
        if (!activa) {
            // Lanzar una excepcion si la reserva no esta activa
            throw new IllegalStateException("No se puede extender el tiempo de una reserva inactiva");
        }

        // Extender el tiempo de la reserva
        this.horaFin = this.horaFin.plusMinutes(tiempoAdicional);

        // Actualizar la reserva
        this.actualizar();
    }

    // Metodo para finalizar una reserva
    public int finalizarReserva() {
        // Verificar si la reserva esta activa
        if (!activa) {
            // Lanzar una excepcion si la reserva ya ha sido finalizada
            throw new IllegalStateException("Esta reserva ya ha sido finalizada");
        }

        // Obtener la hora actual
        LocalDateTime ahora = LocalDateTime.now();

        // Obtener el tiempo no usado
        int tiempoNoUsado = 0;
        
        // Verificar si la hora actual es antes de la hora de fin de la reserva
        if (ahora.isBefore(horaFin)) {
            // Obtener el tiempo no usado
            tiempoNoUsado = (int) java.time.Duration.between(ahora, horaFin).toMinutes();
        }
        
        // Desactivar la reserva
        this.activa = false;
        espacio.liberar();

        //  Guardar la reserva
        this.actualizar();

        // Retornar el tiempo no usado
        return tiempoNoUsado;
    }

    // Getters and Setters
    public boolean estaActiva() {
        return activa && LocalDateTime.now().isBefore(horaFin);
    }

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

    // Metodo para convertir la reserva a un string
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