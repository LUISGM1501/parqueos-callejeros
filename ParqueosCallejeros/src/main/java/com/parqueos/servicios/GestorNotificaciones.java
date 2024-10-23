package com.parqueos.servicios;

import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.Usuario;

// Clase para gestionar las notificaciones
public class GestorNotificaciones {
    private static final Logger LOGGER = Logger.getLogger(GestorNotificaciones.class.getName());
    
    // Variables de configuración de correo
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String REMITENTE_EMAIL = System.getenv("REMITENTE_EMAIL");
    private static final String REMITENTE_PASSWORD = System.getenv("REMITENTE_PASSWORD");
    private static boolean emailConfigured = false;

    public GestorNotificaciones() {
        // Verificar si la configuración de correo está disponible
        emailConfigured = verificarConfiguracionEmail();
        if (!emailConfigured) {
            LOGGER.warning("Configuración de correo no disponible. Las notificaciones serán mostradas en consola.");
        }
    }

    private boolean verificarConfiguracionEmail() {
        return REMITENTE_EMAIL != null && !REMITENTE_EMAIL.isEmpty() 
            && REMITENTE_PASSWORD != null && !REMITENTE_PASSWORD.isEmpty();
    }

    // Metodo para notificar una reserva creada
    public void notificarReservaCreada(Reserva reserva) {
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Su reserva ha sido creada exitosamente:\n" +
            "Espacio: %s\n" +
            "Vehículo: %s\n" +
            "Inicio: %s\n" +
            "Fin: %s\n\n" +
            "Gracias por usar nuestro servicio de parqueo.",
            reserva.getUsuario().getNombre(),
            reserva.getUsuario().getApellidos(),
            reserva.getEspacio().getNumero(),
            reserva.getVehiculo().getPlaca(),
            reserva.getHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            reserva.getHoraFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        enviarNotificacion(reserva.getUsuario().getEmail(), "Reserva Creada", mensaje);
    }
    
    // Metodo para notificar el tiempo agregado a una reserva
    public void notificarTiempoAgregado(Reserva reserva) {
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se ha agregado tiempo a su reserva:\n" +
            "Espacio: %s\n" +
            "Vehículo: %s\n" +
            "Nueva hora de fin: %s\n\n" +
            "Gracias por usar nuestro servicio de parqueo.",
            reserva.getUsuario().getNombre(),
            reserva.getUsuario().getApellidos(),
            reserva.getEspacio().getNumero(),
            reserva.getVehiculo().getPlaca(),
            reserva.getHoraFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        enviarNotificacion(reserva.getUsuario().getEmail(), "Tiempo Agregado a Reserva", mensaje);
    }
    
    // Metodo para notificar el desaparcado de un vehiculo
    public void notificarDesaparcado(Reserva reserva, int tiempoNoUsado) {
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Su vehículo ha sido desaparcado:\n" +
            "Espacio: %s\n" +
            "Vehículo: %s\n" +
            "Tiempo no usado: %d minutos\n\n" +
            "Este tiempo ha sido agregado a su tiempo guardado.\n" +
            "Gracias por usar nuestro servicio de parqueo.",
            reserva.getUsuario().getNombre(),
            reserva.getUsuario().getApellidos(),
            reserva.getEspacio().getNumero(),
            reserva.getVehiculo().getPlaca(),
            tiempoNoUsado
        );
        
        enviarNotificacion(reserva.getUsuario().getEmail(), "Vehículo Desaparcado", mensaje);
    }
    
    // Metodo para notificar una multa generada
    public void notificarMultaGenerada(Multa multa) {
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se ha generado una multa para su vehículo:\n" +
            "Vehículo: %s\n" +
            "Espacio: %s\n" +
            "Fecha y hora: %s\n" +
            "Monto: $%.2f\n\n" +
            "Por favor, realice el pago lo antes posible para evitar cargos adicionales.",
            multa.getVehiculo().getPropietario().getNombre(),
            multa.getVehiculo().getPropietario().getApellidos(),
            multa.getVehiculo().getPlaca(),
            multa.getEspacio().getNumero(),
            multa.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            multa.getMonto()
        );
        
        enviarNotificacion(multa.getVehiculo().getPropietario().getEmail(), "Multa Generada", mensaje);
    }

    // Método para notificar sobre cambios en la configuración
    public void notificarCambioConfiguracion(SistemaParqueo sistema, ConfiguracionParqueo config, String token) {
        Usuario usuario = sistema.getAuthService().obtenerUsuarioAutenticado(token);

        if (usuario == null) {
            LOGGER.warning("Error: Usuario no autenticado o token inválido.");
            return;
        }

        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se han realizado cambios en la configuración del parqueo. A continuación, los detalles:\n" +
            "Horario de Apertura: %s\n" +
            "Horario de Cierre: %s\n" +
            "Precio por Hora: $%.2f\n" +
            "Tiempo Mínimo de Estancia: %d minutos\n\n" +
            "Por favor, tome en cuenta estos cambios para su próxima visita.\n" +
            "Gracias por usar nuestro servicio de parqueo.",
            usuario.getNombre(),
            usuario.getApellidos(),
            config.getHorarioInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
            config.getHorarioFin().format(DateTimeFormatter.ofPattern("HH:mm")),
            (float) config.getPrecioHora(),
            config.getTiempoMinimo()
        );

        enviarNotificacion(usuario.getEmail(), "Cambio en la Configuración del Parqueo", mensaje);
    }

    // Método para notificar sobre el pago de una multa
    public void notificarMultaPagada(Multa multa) {
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Se ha registrado el pago de la siguiente multa:\n" +
            "Vehículo: %s\n" +
            "Espacio: %s\n" +
            "Fecha y hora de la infracción: %s\n" +
            "Monto pagado: $%.2f\n\n" +
            "Gracias por regularizar su situación.",
            multa.getVehiculo().getPropietario().getNombre(),
            multa.getVehiculo().getPropietario().getApellidos(),
            multa.getVehiculo().getPlaca(),
            multa.getEspacio().getNumero(),
            multa.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            multa.getMonto()
        );
        
        enviarNotificacion(multa.getVehiculo().getPropietario().getEmail(), "Confirmación de Pago de Multa", mensaje);
    }
    
    // Método para enviar una notificación
    public void enviarNotificacion(String destinatario, String asunto, String mensaje) {
        if (!emailConfigured) {
            // Si el correo no está configurado, mostrar en consola
            LOGGER.info("Notificación (simulada) para: " + destinatario);
            LOGGER.info("Asunto: " + asunto);
            LOGGER.info("Mensaje: " + mensaje);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        try {
            if (destinatario == null || destinatario.trim().isEmpty()) {
                LOGGER.warning("Dirección de correo de destinatario no válida");
                return;
            }

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(REMITENTE_EMAIL, REMITENTE_PASSWORD);
                }
            });

            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(REMITENTE_EMAIL));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mimeMessage.setSubject(asunto);
            mimeMessage.setText(mensaje);

            Transport.send(mimeMessage);
            LOGGER.info("Correo enviado exitosamente a " + destinatario);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error al enviar correo: " + e.getMessage(), e);
            // No reenviar la excepción para que no interrumpa el flujo de la aplicación
        }
    }
}
