package com.parqueos.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.util.GestorArchivos;

public class GestorReservas {
    private static final String ARCHIVO_RESERVAS = "reservas.json";
    private List<Reserva> reservas;

    public void cargarReservas() {
        reservas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
    }

    public Reserva crearReserva(Reserva reserva) {
        reservas.add(reserva);
        guardarReservas();
        return reserva;
    }

    public void finalizarReserva(Reserva reserva) {
        reserva.finalizarReserva();
        reservas.remove(reserva);
        guardarReservas();
    }

    public List<Reserva> obtenerHistorialReservas(UsuarioParqueo usuario) {
        return reservas.stream()
                       .filter(r -> r.getUsuario().equals(usuario))
                       .sorted((r1, r2) -> r2.getHoraInicio().compareTo(r1.getHoraInicio()))
                       .collect(Collectors.toList());
    }

    public Reserva buscarReserva(String idReserva) {
        return reservas.stream()
                       .filter(r -> r.getIdReserva().equals(idReserva))
                       .findFirst()
                       .orElse(null);
    }

    private void guardarReservas() {
        GestorArchivos.guardarTodo(reservas, ARCHIVO_RESERVAS);
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public List<Reserva> obtenerReservasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservas.stream()
                       .filter(r -> r.getHoraInicio().toLocalDate().isAfter(fechaInicio.minusDays(1)) && 
                                    r.getHoraInicio().toLocalDate().isBefore(fechaFin.plusDays(1)))
                       .collect(Collectors.toList());
    }
}