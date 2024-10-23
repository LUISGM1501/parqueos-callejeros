package com.parqueos.servicios;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar las reservas
public class GestorReservas {
    // Archivo de reservas
    private static final String ARCHIVO_RESERVAS = "reservas.json";
    private List<Reserva> reservas;

    // Metodo para cargar las reservas
    public void cargarReservas() {
        reservas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);
    }

    // Metodo para crear una reserva
    public Reserva crearReserva(Reserva reserva) {
        // Agregar la reserva a la lista de reservas
        reservas.add(reserva);
        // Guardar las reservas
        guardarReservas();
        // Retornar la reserva
        return reserva;
    }

    // Metodo para finalizar una reserva
    public void finalizarReserva(Reserva reserva) {
        // Finalizar la reserva
        reserva.finalizarReserva();
        // Eliminar la reserva de la lista de reservas
        reservas.remove(reserva);
        // Guardar las reservas
        guardarReservas();
    }

    // Metodo para obtener el historial de reservas de un usuario
    public List<Reserva> obtenerHistorialReservas(UsuarioParqueo usuario) {
        return reservas.stream()
                       // Filtrar las reservas del usuario
                       .filter(r -> r.getUsuario().equals(usuario))
                       .sorted((r1, r2) -> r2.getHoraInicio().compareTo(r1.getHoraInicio()))
                       .collect(Collectors.toList());
    }

    // Metodo para buscar una reserva por su id
    public Reserva buscarReserva(String idReserva) {
        return reservas.stream()
                       // Filtrar la reserva que tiene el id igual al id de la reserva
                       .filter(r -> r.getIdReserva().equals(idReserva))
                       .findFirst()
                       .orElse(null);
    }
    
    // Metodo para guardar las reservas
    private void guardarReservas() {
        // Guardar las reservas en el archivo json
        GestorArchivos.guardarTodo(reservas, ARCHIVO_RESERVAS);
    }

    // Getter para obtener las reservas
    public List<Reserva> getReservas() {
        return reservas;
    }

    // Metodo para obtener las reservas entre dos fechas
    public List<Reserva> obtenerReservasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservas.stream()
                       // Filtrar las reservas que tienen la fecha y hora de la reserva entre las fechas
                       .filter(r -> r.getHoraInicio().toLocalDate().isAfter(fechaInicio.minusDays(1)) && 
                                    r.getHoraInicio().toLocalDate().isBefore(fechaFin.plusDays(1)))
                       .collect(Collectors.toList());
    }
}