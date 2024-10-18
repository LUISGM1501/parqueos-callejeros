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

// Clase para gestionar las notificaciones
public class GestorNotificaciones {
    // Variables de entorno para el remitente
    private static final String REMITENTE_EMAIL = System.getenv("REMITENTE_EMAIL");
    private static final String REMITENTE_PASSWORD = System.getenv("REMITENTE_PASSWORD");

    // Metodo para notificar una reserva creada
    public void notificarReservaCreada(Reserva reserva) {
        // Obtener el usuario de la reserva
        Usuario usuario = reserva.getUsuario();

        // Crear el mensaje de la notificacion
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
        
        // Enviar el correo
        enviarCorreo(usuario.getEmail(), "Reserva Creada", mensaje);
    }
    
    // Metodo para notificar el tiempo agregado a una reserva
    public void notificarTiempoAgregado(Reserva reserva) {
        // Obtener el usuario de la reserva
        Usuario usuario = reserva.getUsuario();

        // Crear el mensaje de la notificacion
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
        
        // Enviar el correo
        enviarCorreo(usuario.getEmail(), "Tiempo Agregado a Reserva", mensaje);
    }
    
    // Metodo para notificar el desaparcado de un vehiculo
    public void notificarDesaparcado(Reserva reserva, int tiempoNoUsado) {
        // Obtener el usuario de la reserva
        Usuario usuario = reserva.getUsuario();

        // Crear el mensaje de la notificacion
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
        
        // Enviar el correo
        enviarCorreo(usuario.getEmail(), "Vehículo Desaparcado", mensaje);
    }
    
    // Metodo para notificar una multa generada
    public void notificarMultaGenerada(Multa multa) {
        // Obtener el usuario del vehiculo
        Usuario usuario = multa.getVehiculo().getPropietario();

        // Crear el mensaje de la notificacion
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
        
        // Enviar el correo
        enviarCorreo(usuario.getEmail(), "Multa Generada", mensaje);
    }

    // Metodo para notificar el pago de una multa
    public void notificarMultaPagada(Multa multa) {
        // Obtener el usuario del vehiculo
        Usuario usuario = multa.getVehiculo().getPropietario();

        // Crear el mensaje de la notificacion
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
        
        // Enviar el correo
        enviarCorreo(usuario.getEmail(), "Confirmación de Pago de Multa", mensaje);
    }
    
    // Metodo para enviar un correo
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        // Configurar las propiedades del servidor de correo
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Crear una sesion de correo
        Session session = Session.getInstance(props, new Authenticator() {
            
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE_EMAIL, REMITENTE_PASSWORD);
            }
        });

        // Crear un mensaje mime
        try {
            // Un mensaje mime es un mensaje que puede contener texto, html, adjuntos, etc.
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(mensaje);

            // Enviar el mensaje
            Transport.send(message);

            // Mensaje de confirmacion
            System.out.println("Correo enviado exitosamente a " + destinatario);
        } catch (MessagingException e) {
            // Mensaje de error
            System.out.println("Error al enviar el correo: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}