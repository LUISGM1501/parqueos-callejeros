package com.parqueos.ui.controladores;

import java.awt.Dimension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaUsuarioParqueo;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;

// Controlador para el usuario parqueo
public class ControladorUsuarioParqueo extends ControladorBase {
    private final VistaUsuarioParqueo vista;
    private final SistemaParqueo sistemaParqueo;
    private final UsuarioParqueo usuario;
    private final String token;
    private static final Logger LOGGER = Logger.getLogger(ControladorUsuarioParqueo.class.getName());
    
    // Constructor para inicializar el controlador
    public ControladorUsuarioParqueo(VistaUsuarioParqueo vista, SistemaParqueo sistemaParqueo, UsuarioParqueo usuario, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.usuario = usuario;
        this.token = token;
        inicializar();
    }

    // Metodo para inicializar el controlador
    @Override
    protected void inicializar() {
        cargarVehiculos();
        actualizarTiempoGuardado();
        actualizarTablaReservasActivas();
        actualizarTablaMultas();
        actualizarComboVehiculos();

        // Agregar los listeners a los botones
        vista.getBtnParquear().addActionListener(e -> parquear());
        vista.getBtnAgregarTiempo().addActionListener(e -> agregarTiempo());
        vista.getBtnDesaparcar().addActionListener(e -> desaparcar());
        vista.getBtnVerEspaciosDisponibles().addActionListener(e -> verEspaciosDisponibles());
        vista.getBtnVerHistorial().addActionListener(e -> verHistorial());
        vista.getBtnVerMultas().addActionListener(e -> actualizarTablaMultas());
        vista.getBtnPagarMulta().addActionListener(e -> pagarMulta());

        // Iniciar un timer para actualizar la tabla de reservas activas cada minuto
        Timer timer = new Timer(60000, e -> actualizarTablaReservasActivas());
        timer.start();
        
        // Timer para actualizar tabla de multas cada 30 segundos
        Timer timerMultas = new Timer(30000, e -> actualizarTablaMultas());
        timerMultas.start();
    }

    // Metodo para cargar los vehiculos del usuario
    private void cargarVehiculos() {
        // Crear el modelo del combo box
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        vista.getCmbVehiculos().setModel(model);
    
        // Obtener los vehículos del usuario
        List<Vehiculo> vehiculos = sistemaParqueo.getGestorVehiculos()
            .obtenerVehiculosPorUsuario(usuario.getId());
    
        // Agregar las placas al combo box
        for (Vehiculo vehiculo : vehiculos) {
            model.addElement(vehiculo.getPlaca());
        }
    }

    // Metodo para parquear un vehiculo
    private boolean vehiculoEstaParqueado(String placa) {
        try {
            // Verificar reservas activas
            boolean tieneReservaActiva = sistemaParqueo.getGestorReservas().getReservas().stream()
                .anyMatch(r -> r.estaActiva() && 
                             r.getVehiculo() != null && 
                             r.getVehiculo().getPlaca().equals(placa));

            if (tieneReservaActiva) {
                return true;
            }

            // Verificar multas pendientes
            boolean tieneMultaPendiente = sistemaParqueo.getGestorMultas().getMultas().stream()
                .anyMatch(m -> !m.getPagada() && 
                             m.getVehiculo() != null && 
                             m.getVehiculo().getPlaca().equals(placa));

            // También verificar si hay reservas vencidas sin multa
            boolean tieneReservaVencida = sistemaParqueo.getGestorReservas().getReservas().stream()
                .anyMatch(r -> r.getVehiculo() != null && 
                             r.getVehiculo().getPlaca().equals(placa) && 
                             r.getHoraFin().isBefore(LocalDateTime.now()) && 
                             r.estaActiva());

            return tieneMultaPendiente || tieneReservaVencida;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void parquear() {
        try {
            // Validar que se haya seleccionado un vehículo
            String placa = (String) vista.getCmbVehiculos().getSelectedItem();
            if (placa == null || placa.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar un vehículo");
                return;
            }

            // Verificar si el vehículo está parqueado
            if (vehiculoEstaParqueado(placa)) {
                // Buscar detalles de la situación actual del vehículo
                String mensaje = "No se puede parquear el vehículo porque:\n";
                
                // Verificar si tiene una reserva activa
                Optional<Reserva> reservaActiva = sistemaParqueo.getGestorReservas().getReservas().stream()
                    .filter(r -> r.estaActiva() && 
                               r.getVehiculo() != null && 
                               r.getVehiculo().getPlaca().equals(placa))
                    .findFirst();

                if (reservaActiva.isPresent()) {
                    Reserva reserva = reservaActiva.get();
                    mensaje += String.format("- Ya tiene una reserva activa en el espacio %s hasta las %s\n",
                        reserva.getEspacio().getNumero(),
                        reserva.getHoraFin().format(DateTimeFormatter.ofPattern("HH:mm")));
                }

                // Verificar si tiene una multa pendiente
                Optional<Multa> multaPendiente = sistemaParqueo.getGestorMultas().getMultas().stream()
                    .filter(m -> !m.getPagada() && 
                               m.getVehiculo() != null && 
                               m.getVehiculo().getPlaca().equals(placa))
                    .findFirst();

                if (multaPendiente.isPresent()) {
                    mensaje += "- Tiene una multa pendiente de pago\n";
                }

                // Verificar si tiene una reserva vencida
                Optional<Reserva> reservaVencida = sistemaParqueo.getGestorReservas().getReservas().stream()
                    .filter(r -> r.getVehiculo() != null && 
                               r.getVehiculo().getPlaca().equals(placa) && 
                               r.getHoraFin().isBefore(LocalDateTime.now()) && 
                               r.estaActiva())
                    .findFirst();

                if (reservaVencida.isPresent()) {
                    mensaje += "- Tiene una reserva vencida que requiere atención\n";
                }

                mensaje += "\nDebe resolver la situación antes de poder parquear el vehículo nuevamente.";
                
                JOptionPane.showMessageDialog(vista, mensaje, "No se puede parquear", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar que se haya ingresado un espacio
            String numeroEspacio = vista.getTxtEspacio().getText();
            if (numeroEspacio == null || numeroEspacio.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Debe ingresar un número de espacio");
                return;
            }

            // Obtener el vehículo seleccionado
            Vehiculo vehiculo = usuario.getVehiculos().stream()
                .filter(v -> placa.equals(v.getPlaca()))
                .findFirst()
                .orElse(null);

            if (vehiculo == null) {
                JOptionPane.showMessageDialog(vista, "Error: Vehículo no encontrado");
                return;
            }

            // Buscar el espacio
            EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio.trim());
            if (espacio == null) {
                JOptionPane.showMessageDialog(vista, "El espacio no existe");
                return;
            }

            if (!espacio.estaDisponible()) {
                JOptionPane.showMessageDialog(vista, "El espacio no está disponible");
                return;
            }

            // Obtener el tiempo
            int tiempoComprado = (Integer) vista.getSpnTiempo().getValue();

            // Crear la reserva
            Reserva reserva = new Reserva(usuario, espacio, vehiculo, tiempoComprado);
            sistemaParqueo.getGestorReservas().crearReserva(reserva);
            
            // Enviar notificación
            sistemaParqueo.getGestorNotificaciones().notificarReservaCreada(reserva);

            JOptionPane.showMessageDialog(vista, 
                "Parqueo exitoso.\nEspacio: " + espacio.getNumero() + 
                "\nTiempo: " + tiempoComprado + " minutos" +
                "\nID Reserva: " + reserva.getIdReserva());

            // Actualizar interfaces
            actualizarTablaReservasActivas();
            actualizarTiempoGuardado();
            
            // Limpiar campos
            vista.getTxtEspacio().setText("");
            vista.getSpnTiempo().setValue(30);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, 
                "Error al parquear: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo para agregar tiempo a una reserva
    private void agregarTiempo() {
        // Obtener el id de la reserva
        String idReserva = JOptionPane.showInputDialog(vista, "Ingrese el ID de la reserva:");
        // Obtener el tiempo adicional
        int tiempoAdicional = (Integer) vista.getSpnTiempo().getValue();
        
        try {
            // Buscar la reserva
            Reserva reserva = sistemaParqueo.getGestorReservas().buscarReserva(idReserva);
            // Verificar si la reserva pertenece al usuario
            if (reserva != null && reserva.getUsuario().equals(usuario)) {
                reserva.extenderTiempo(tiempoAdicional);
                JOptionPane.showMessageDialog(vista, "Tiempo agregado exitosamente.");
                // Actualizar la tabla de reservas activas
                actualizarTablaReservasActivas();
            } else {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, "Reserva no encontrada o no pertenece al usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo para desaparcar un vehiculo
    private void desaparcar() {
        try {
            int filaSeleccionada = vista.getTblReservasActivas().getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(vista, 
                    "Por favor, seleccione una reserva para desaparcar.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idReserva = vista.getTblReservasActivas().getValueAt(filaSeleccionada, 0).toString();
            if (idReserva.startsWith("MULTA-")) {
                JOptionPane.showMessageDialog(vista,
                    "No se puede desaparcar un vehículo con multa pendiente. Por favor, pague la multa primero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Reserva reserva = sistemaParqueo.getGestorReservas().buscarReserva(idReserva);
            if (reserva == null || !reserva.estaActiva()) {
                actualizarTablaReservasActivas();
                return;
            }

            LocalDateTime ahora = LocalDateTime.now();
            if (ahora.isBefore(reserva.getHoraFin())) {
                int confirmacion = JOptionPane.showConfirmDialog(vista,
                    "El tiempo de reserva aún no ha terminado. Si desaparca ahora, se guardará el tiempo restante.\n" +
                    "¿Desea continuar?",
                    "Confirmar Desaparcar",
                    JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    int tiempoNoUsado = reserva.finalizarReserva();
                    usuario.setTiempoGuardado(usuario.getTiempoGuardado() + tiempoNoUsado);
                    
                    sistemaParqueo.getGestorNotificaciones().notificarDesaparcado(reserva, tiempoNoUsado);
                    
                    JOptionPane.showMessageDialog(vista, 
                        String.format("Vehículo desaparcado exitosamente.\n" +
                                    "Tiempo no usado: %d minutos\n" +
                                    "Este tiempo ha sido agregado a su tiempo guardado.",
                                    tiempoNoUsado));
                }
            } else {
                reserva.finalizarReserva();
                JOptionPane.showMessageDialog(vista, "Vehículo desaparcado exitosamente.");
            }

            actualizarTablaReservasActivas();
            actualizarTiempoGuardado();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, 
                "Error al desaparcar: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo para ver los espacios disponibles
    private void verEspaciosDisponibles() {
        // Obtener los espacios disponibles
        List<EspacioParqueo> espaciosDisponibles = sistemaParqueo.getGestorEspacios().obtenerEspaciosDisponibles();
        // Crear el mensaje
        StringBuilder mensaje = new StringBuilder("Espacios disponibles:\n");
        // Agregar los espacios disponibles al mensaje
        for (EspacioParqueo espacio : espaciosDisponibles) {
            mensaje.append("Espacio ").append(espacio.getNumero()).append("\n");
        }
        // Mostrar el mensaje
        JOptionPane.showMessageDialog(vista, mensaje.toString());
    }

    // Metodo para ver el historial de reservas
    private void verHistorial() {
        try {
            List<Reserva> todasLasReservas = sistemaParqueo.getGestorReservas().getReservas();
            List<Reserva> historialReservas = new ArrayList<>();
            
            // Recorrer y validar cada reserva antes de filtrar
            for (Reserva reserva : todasLasReservas) {
                try {
                    // Intentar cargar el usuario si es null
                    if (reserva.getUsuario() == null && reserva.getUsuarioId() != null) {
                        Usuario usuarioCargado = Usuario.cargar(reserva.getUsuarioId());
                        if (usuarioCargado instanceof UsuarioParqueo) {
                            reserva.setUsuario((UsuarioParqueo)usuarioCargado);
                        }
                    }

                    // Solo agregar si la reserva tiene usuario válido y coincide con el usuario actual
                    if (reserva.getUsuario() != null && 
                        reserva.getUsuario().getId().equals(usuario.getId())) {
                        historialReservas.add(reserva);
                    }
                } catch (Exception e) {
                    Logger.getLogger(ControladorUsuarioParqueo.class.getName())
                          .warning("Error procesando reserva: " + reserva.getIdReserva());
                }
            }

            if (historialReservas.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "No tiene reservas en su historial.");
                return;
            }

            // Ordenar por fecha descendente
            historialReservas.sort((r1, r2) -> r2.getHoraInicio().compareTo(r1.getHoraInicio()));

            // Crear el modelo de tabla y mostrar datos...
            DefaultTableModel modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            modelo.addColumn("ID");
            modelo.addColumn("Espacio");
            modelo.addColumn("Vehículo");
            modelo.addColumn("Inicio");
            modelo.addColumn("Fin");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Reserva reserva : historialReservas) {
                modelo.addRow(new Object[]{
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNumero(),
                    reserva.getVehiculo().getPlaca(),
                    reserva.getHoraInicio().format(formatter),
                    reserva.getHoraFin().format(formatter)
                });
            }

            JTable tablaHistorial = new JTable(modelo);
            JScrollPane scrollPane = new JScrollPane(tablaHistorial);
            scrollPane.setPreferredSize(new Dimension(800, 300));

            JOptionPane.showMessageDialog(vista, scrollPane, 
                "Historial de Reservas", 
                JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            Logger.getLogger(ControladorUsuarioParqueo.class.getName())
                  .log(Level.SEVERE, "Error al mostrar historial", e);
            JOptionPane.showMessageDialog(vista,
                "Error al mostrar historial: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo para actualizar la tabla de reservas activas
    private void actualizarTablaReservasActivas() {
        try {
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.setColumnIdentifiers(new Object[]{
                "ID Reserva", "Espacio", "Vehículo", "Inicio", "Fin", "Tiempo Restante (min)", "Estado"
            });

            // Obtener todas las reservas activas
            List<Reserva> reservasActivas = sistemaParqueo.getGestorReservas().getReservas().stream()
                .filter(r -> r.getUsuario().getId().equals(usuario.getId()) && r.estaActiva())
                .collect(Collectors.toList());

            // Obtener todos los vehículos con multas pendientes
            List<Multa> multasPendientes = sistemaParqueo.getGestorMultas().getMultas().stream()
                .filter(m -> !m.getPagada() && 
                            m.getVehiculo().getPropietarioId() != null && 
                            m.getVehiculo().getPropietarioId().equals(usuario.getId()))
                .collect(Collectors.toList());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime ahora = LocalDateTime.now();

            // Agregar reservas activas
            for (Reserva reserva : reservasActivas) {
                long minutosRestantes = ChronoUnit.MINUTES.between(ahora, reserva.getHoraFin());
                modelo.addRow(new Object[]{
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNumero(),
                    reserva.getVehiculo().getPlaca(),
                    reserva.getHoraInicio().format(formatter),
                    reserva.getHoraFin().format(formatter),
                    Math.max(0, minutosRestantes),
                    "Reserva Activa"
                });
            }

            // Agregar vehículos con multas pendientes
            for (Multa multa : multasPendientes) {
                modelo.addRow(new Object[]{
                    "MULTA-" + multa.getIdMulta(),
                    multa.getEspacio().getNumero(),
                    multa.getVehiculo().getPlaca(),
                    multa.getFechaHora().format(formatter),
                    "Pendiente de pago",
                    "-",
                    "Multa Pendiente"
                });
            }

            vista.getTblReservasActivas().setModel(modelo);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, 
                "Error al actualizar tabla de reservas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desaparcarAutomaticamente(Multa multa) {
        // Buscar la reserva asociada
        List<Reserva> reservas = sistemaParqueo.getGestorReservas().getReservas().stream()
            .filter(r -> r.getEspacio().getId().equals(multa.getEspacio().getId()) 
                    && r.getVehiculo().getPlaca().equals(multa.getVehiculo().getPlaca())
                    && r.estaActiva())
            .collect(Collectors.toList());

        // Finalizar la reserva si existe
        if (!reservas.isEmpty()) {
            Reserva reserva = reservas.get(0);
            usuario.desaparcar(reserva);
        }

        // Liberar el espacio
        multa.getEspacio().liberar();
    }

    // Metodo para pagar una multa
    private void pagarMulta() {
        try {
            int filaSeleccionada = vista.getTblMultas().getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(vista, 
                    "Por favor, seleccione una multa para pagar.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String idMulta = (String) vista.getTblMultas().getValueAt(filaSeleccionada, 0);
            Multa multa = sistemaParqueo.getGestorMultas().buscarMulta(idMulta);
            
            if (multa != null && !multa.getPagada()) {
                int montoTotal = multa.getMonto();
                int tiempoGuardado = usuario.getTiempoGuardado();
                int descuento = 0;

                if (tiempoGuardado > 0) {
                    // Calcular el valor del tiempo guardado
                    ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
                    int valorPorHora = config.getPrecioHora();
                    double horasGuardadas = tiempoGuardado / 60.0;
                    descuento = (int)(horasGuardadas * valorPorHora);

                    if (descuento > montoTotal) {
                        descuento = montoTotal;
                    }

                    int opcion = JOptionPane.showConfirmDialog(vista,
                        String.format("Tiene %d minutos guardados (equivalente a ₡%d).\n" +
                                    "Monto de la multa: ₡%d\n" +
                                    "¿Desea usar el tiempo guardado como descuento?",
                                    tiempoGuardado, descuento, montoTotal),
                        "Usar tiempo guardado",
                        JOptionPane.YES_NO_OPTION);

                    if (opcion == JOptionPane.YES_OPTION) {
                        // Calcular cuántos minutos se usarán
                        int minutosUsados = (int)((descuento * 60.0) / valorPorHora);
                        usuario.setTiempoGuardado(tiempoGuardado - minutosUsados);
                        montoTotal -= descuento;
                    }
                }

                int confirmacion = JOptionPane.showConfirmDialog(vista,
                    String.format("Monto a pagar: ₡%d\n" +
                                "Descuento aplicado: ₡%d\n" +
                                "Total final: ₡%d\n" +
                                "¿Desea proceder con el pago?",
                                multa.getMonto(), descuento, montoTotal),
                    "Confirmar Pago",
                    JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    multa.pagar();
                    desaparcarAutomaticamente(multa);
                    actualizarTablaMultas();
                    actualizarTablaReservasActivas();
                    actualizarTiempoGuardado();
                    JOptionPane.showMessageDialog(vista, "Multa pagada exitosamente.");
                }
            } else {
                JOptionPane.showMessageDialog(vista, 
                    "La multa seleccionada no existe o ya ha sido pagada.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista,
                "Error al pagar la multa: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo para actualizar el tiempo guardado
    private void actualizarTiempoGuardado() {
        // Actualizar el tiempo guardado en la vista
        vista.actualizarTiempoGuardado(usuario.getTiempoGuardado());
    }

    // Metodo para sincronizar los vehiculos del usuario
    private void actualizarComboVehiculos() {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) vista.getCmbVehiculos().getModel();
        model.removeAllElements();
        
        // Sincronizar vehículos del usuario
        usuario.sincronizarVehiculos();
        
        // Iterar sobre los vehículos del usuario
        for (Vehiculo vehiculo : usuario.getVehiculos()) {
            // Agregar el vehículo al combo box
            model.addElement(vehiculo.getPlaca());
        }
    }
    
    // Método para actualizar la tabla de multas
    private void actualizarTablaMultas() {
        try {
            DefaultTableModel modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            modelo.setColumnIdentifiers(new Object[]{"ID Multa", "Vehículo", "Espacio", "Fecha", "Monto", "Estado"});

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            List<Multa> multas = sistemaParqueo.getGestorMultas().obtenerMultasUsuario(usuario);

            for (Multa multa : multas) {
                modelo.addRow(new Object[]{
                    multa.getIdMulta(),
                    multa.getVehiculo().getPlaca(),
                    multa.getEspacio().getNumero(),
                    multa.getFechaHora().format(formatter),
                    String.format("₡%d", multa.getMonto()),
                    multa.getPagada() ? "Pagada" : "Pendiente"
                });
            }

            vista.getTblMultas().setModel(modelo);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista,
                "Error al actualizar tabla de multas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}