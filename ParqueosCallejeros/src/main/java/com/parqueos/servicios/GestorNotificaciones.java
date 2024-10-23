package com.parqueos.servicios;

import java.time.format.DateTimeFormatter;

import java.util.Properties;
import java.util.List;

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
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;



// Clase para gestionar las notificaciones
public class GestorNotificaciones {
    // Variables de entorno para el remitente
    private static final String REMITENTE_EMAIL = "eazofp2106@gmail.com";
    private static final String REMITENTE_PASSWORD = "zjpf lihk iypt inoa";

    // Metodo para notificar una reserva creada
    public void notificarReservaCreada(Reserva reserva) {
        // Obtener el usuario de la reserva
        Usuario usuario = reserva.getUsuario();

        // Crear el mensaje de la notificación
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Su reserva ha sido creada exitosamente:\n" +
            "Espacio: %s\n" +
            "Vehículo: %s\n" +
            "Inicio: %s\n" +
            "Fin: %s\n\n" +
            "Gracias por usar nuestro servicio de parqueo.\n\n" +
            "**ID Reserva: %s**",  // Texto en negrita para el ID de la reserva
            usuario.getNombre(),
            usuario.getApellidos(),
            reserva.getEspacio().getNumero(),
            reserva.getVehiculo().getPlaca(),
            reserva.getHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            reserva.getHoraFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            reserva.getIdReserva()  // Obtener el ID de la reserva
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
    public void notificarMultaGenerada(Multa multa, SistemaParqueo sistema) {
        // Obtener el usuario del vehiculo
        String id = multa.getVehiculo().getPropietario();
        Usuario usuario = sistema.getGestorUsuarios().buscarUsuario(id);

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
    // Método para notificar sobre cambios en la configuración
    public void notificarCambioConfiguracion(SistemaParqueo sistema, ConfiguracionParqueo config, String token) {
        // Obtener el usuario del vehículo
        Usuario usuario = sistema.getAuthService().obtenerUsuarioAutenticado(token);

        // Verificar que el usuario no sea nulo
        if (usuario == null) {
            System.out.println("Error: Usuario no autenticado o token inválido.");
            return; // O lanzar una excepción, según el caso
        }

        // Crear el mensaje de la notificación
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
            (float) config.getPrecioHora(), // Asegúrate que este sea un float o double
            config.getTiempoMinimo() // Este debe ser un int
        );

        // Enviar el correo
        enviarCorreo(usuario.getEmail(), "Cambio en la Configuración del Parqueo", mensaje);
    }


    
    //método para notificar sobre cambio en configuración
    public void notificarMultaPagada(Multa multa, SistemaParqueo sistema) {
        // Obtener el usuario del vehiculo
        String id = multa.getVehiculo().getPropietario();
        Usuario usuario = sistema.getGestorUsuarios().buscarUsuario(id);
        
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
    
    //método para enviar datos actulizados
    public void notificarActualizacionDatos(Usuario usuario) {
        // Verificar que el usuario no sea nulo
        if (usuario == null) {
            System.out.println("Error: Usuario nulo.");
            return;
        }

        // Crear el mensaje de la notificación con los datos actualizados
        String mensaje = String.format(
            "Estimado/a %s %s,\n\n" +
            "Sus datos han sido actualizados en nuestro sistema. A continuación, los detalles actualizados de su perfil:\n" +
            "Nombre: %s\n" +
            "Apellidos: %s\n" +
            "Teléfono: %d\n" +
            "Email: %s\n" +
            "Dirección: %s\n" +
            "ID de Usuario: %s\n" +
            "PIN: %s\n",
            usuario.getNombre(),
            usuario.getApellidos(),
            usuario.getNombre(),
            usuario.getApellidos(),
            usuario.getTelefono(),
            usuario.getEmail(),
            usuario.getDireccion(),
            usuario.getIdUsuario(),
            usuario.getPin()
        );

        // Verificar si el usuario es de tipo UsuarioParqueo y agregar detalles adicionales
        if (usuario instanceof UsuarioParqueo) {
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
            mensaje += String.format(
                "Número de Tarjeta: %s\n" +
                "Fecha de Vencimiento: %s\n" +
                "Código de Validación: %s\n",
                usuarioParqueo.getNumeroTarjeta(),
                usuarioParqueo.getFechaVencimientoTarjeta(),
                usuarioParqueo.getCodigoValidacionTarjeta()
            );

            // Obtener y agregar la lista de vehículos
            List<Vehiculo> vehiculos = usuarioParqueo.getVehiculos();
            if (vehiculos.isEmpty()) {
                mensaje += "No tiene vehículos registrados.\n";
            } else {
                mensaje += "Vehículos registrados:\n";
                for (Vehiculo vehiculo : vehiculos) {
                    mensaje += String.format("- Placa: %s, Marca: %s, Modelo: %s\n", 
                        vehiculo.getPlaca(), 
                        vehiculo.getMarca(), 
                        vehiculo.getModelo());
                }
            }
        }

        // Verificar si el usuario es de tipo Inspector y agregar el terminal ID
        if (usuario instanceof Inspector) {
            Inspector inspector = (Inspector) usuario;
            mensaje += String.format(
                "Terminal ID: %s\n",
                inspector.getTerminalId()
            );
        }

        mensaje += "\nPor favor, revise la información para asegurarse de que esté correcta.\n" +
                   "Gracias por usar nuestro servicio.";

        // Enviar el correo
        enviarCorreo(usuario.getEmail(), "Actualización de Datos", mensaje);
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