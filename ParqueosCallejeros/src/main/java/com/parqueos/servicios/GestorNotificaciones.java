package com.parqueos.servicios;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.Usuario;

public class GestorNotificaciones {
    private static final String REMITENTE_EMAIL = System.getenv("REMITENTE_EMAIL");
    private static final String REMITENTE_PASSWORD = System.getenv("REMITENTE_PASSWORD");

    public void notificarReservaCreada(Reserva reserva) {
        Usuario usuario = reserva.getUsuario();
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Su reserva ha sido creada exitosamente:\n" +
            "Espacio: %s\n" +
            "Vehículo: %s\n" +
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
    
    public void notificarTiempoAgregado(Reserva reserva) {
        Usuario usuario = reserva.getUsuario();
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se ha agregado tiempo a su reserva:\n" +
            "Espacio: %s\n" +
            "Vehículo: %s\n" +
            "Nueva hora de fin: %s\n\n" +
            "Gracias por usar nuestro servicio de parqueo.",
            usuario.getNombre(),
            usuario.getApellidos(),
            reserva.getEspacio().getNumero(),
            reserva.getVehiculo().getPlaca(),
            reserva.getHoraFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        enviarCorreo(usuario.getEmail(), "Tiempo Agregado a Reserva", mensaje);
    }
    
    public void notificarDesaparcado(Reserva reserva, int tiempoNoUsado) {
        Usuario usuario = reserva.getUsuario();
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Su vehículo ha sido desaparcado:\n" +
            "Espacio: %s\n" +
            "Vehículo: %s\n" +
            "Tiempo no usado: %d minutos\n\n" +
            "Este tiempo ha sido agregado a su tiempo guardado.\n" +
            "Gracias por usar nuestro servicio de parqueo.",
            usuario.getNombre(),
            usuario.getApellidos(),
            reserva.getEspacio().getNumero(),
            reserva.getVehiculo().getPlaca(),
            tiempoNoUsado
        );
        
        enviarCorreo(usuario.getEmail(), "Vehículo Desaparcado", mensaje);
    }
    
    public void notificarMultaGenerada(Multa multa) {
        Usuario usuario = multa.getVehiculo().getPropietario();
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se ha generado una multa para su vehículo:\n" +
            "Vehículo: %s\n" +
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

    public void notificarMultaPagada(Multa multa) {
        Usuario usuario = multa.getVehiculo().getPropietario();
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se ha registrado el pago de la siguiente multa:\n" +
            "Vehículo: %s\n" +
            "Espacio: %s\n" +
            "Fecha y hora de la infracción: %s\n" +
            "Monto pagado: $%.2f\n\n" +
            "Gracias por regularizar su situación.",
            usuario.getNombre(),
            usuario.getApellidos(),
            multa.getVehiculo().getPlaca(),
            multa.getEspacio().getNumero(),
            multa.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            multa.getMonto()
        );
        
        enviarCorreo(usuario.getEmail(), "Confirmación de Pago de Multa", mensaje);
    }
    
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE_EMAIL, REMITENTE_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(mensaje);

            Transport.send(message);

            System.out.println("Correo enviado exitosamente a " + destinatario);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}