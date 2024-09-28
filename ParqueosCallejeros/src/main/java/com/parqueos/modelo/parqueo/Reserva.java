package com.parqueos.modelo.parqueo;

import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;

import java.time.LocalDateTime;
import java.time.Duration;

public class Reserva {
    private UsuarioParqueo usuario;
    private EspacioParqueo espacio;
    private Vehiculo vehiculo;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;

    public Reserva(UsuarioParqueo usuario, EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        this.usuario = usuario;
        this.espacio = espacio;
        this.vehiculo = vehiculo;
        this.horaInicio = LocalDateTime.now();
        this.horaFin = horaInicio.plusMinutes(tiempoComprado);
        
        espacio.ocupar(vehiculo);
        espacio.setPagado(true);
    }

    public void extenderTiempo(int tiempoAdicional) {
        this.horaFin = this.horaFin.plusMinutes(tiempoAdicional);
    }

    public int finalizarReserva() {
        LocalDateTime ahora = LocalDateTime.now();
        int tiempoNoUsado = 0;
        
        if (ahora.isBefore(horaFin)) {
            Duration duracion = Duration.between(ahora, horaFin);
            tiempoNoUsado = (int) duracion.toMinutes();
        }
        
        espacio.liberar();
        return tiempoNoUsado;
    }

    public boolean estaActiva() {
        return LocalDateTime.now().isBefore(horaFin);
    }

    // Getters
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
                "usuario=" + usuario.getIdUsuario() +
                ", espacio=" + espacio.getNumero() +
                ", vehiculo=" + vehiculo.getPlaca() +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}