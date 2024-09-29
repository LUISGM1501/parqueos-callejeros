package com.parqueos.servicios;

import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.multa.Multa;

import java.time.format.DateTimeFormatter;

public class GestorNotificaciones {
    
    public void notificarReservaCreada(Reserva reserva) {
        Usuario usuario = reserva.getUsuario();
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Su reserva ha sido creada exitosamente:\n" +
            "Espacio: %s\n" +
            "Vehiculo: %s\n" +
            "Inicio: %s\n" +
            "Fin: %s\n\n" +
            "Gracias por usar nuestro servicio de parqueo.",
            usuario.getNombre(),
            usuario.getApellidos(),
            reserva.getEspacio().getNumero(),
            reserva.getVehiculo().getPlaca(),
            reserva.getHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            reserva.getHoraFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        enviarCorreo(usuario.getEmail(), "Reserva Creada", mensaje);
    }
    
    public void notificarMultaGenerada(Multa multa) {
        Usuario usuario = multa.getVehiculo().getPropietario();
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se ha generado una multa para su vehiculo:\n" +
            "Vehiculo: %s\n" +
            "Espacio: %s\n" +
            "Fecha y hora: %s\n" +
            "Monto: $%.2f\n\n" +
            "Por favor, realice el pago lo antes posible para evitar cargos adicionales.",
            usuario.getNombre(),
            usuario.getApellidos(),
            multa.getVehiculo().getPlaca(),
            multa.getEspacio().getNumero(),
            multa.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            multa.getMonto()
        );
        
        enviarCorreo(usuario.getEmail(), "Multa Generada", mensaje);
    }
    
    private void enviarCorreo(String destinatario, String asunto, String mensaje) {
        // Falta implementar la logica para enviar correos 
        System.out.println("Enviando correo a: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Mensaje:\n" + mensaje);
        System.out.println("--------------------");
    }
}