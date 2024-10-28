package com.parqueos.ui.controladores;

import java.awt.Dimension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private void parquear() {
        try {
            // Validar que se haya seleccionado un vehículo
            String placa = (String) vista.getCmbVehiculos().getSelectedItem();
            if (placa == null || placa.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar un vehículo");
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

            // Verificar si el vehículo ya está parqueado
            boolean vehiculoYaParqueado = sistemaParqueo.getGestorReservas().getReservas().stream()
                .filter(Reserva::estaActiva)
                .anyMatch(r -> r.getVehiculo() != null && 
                             r.getVehiculo().getPlaca() != null && 
                             r.getVehiculo().getPlaca().equals(placa));

            if (vehiculoYaParqueado) {
                JOptionPane.showMessageDialog(vista, "Este vehículo ya se encuentra parqueado");
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
                    "Por favor, seleccione una reserva de la tabla para desaparcar",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object idReservaObj = vista.getTblReservasActivas().getValueAt(filaSeleccionada, 0);
            if (idReservaObj == null) {
                JOptionPane.showMessageDialog(vista, "Error: No se pudo obtener el ID de la reserva");
                return;
            }

            String idReserva = idReservaObj.toString().trim();
            if (idReserva.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Error: ID de reserva inválido");
                return;
            }

            Reserva reserva = sistemaParqueo.getGestorReservas().buscarReserva(idReserva);
            if (reserva == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró la reserva");
                return;
            }

            if (!reserva.estaActiva()) {
                JOptionPane.showMessageDialog(vista, "La reserva ya no está activa");
                actualizarTablaReservasActivas();
                return;
            }

            // Confirmar desaparcar
            int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro que desea desaparcar el vehículo?" +
                "\nEspacio: " + reserva.getEspacio().getNumero() +
                "\nVehículo: " + reserva.getVehiculo().getPlaca(),
                "Confirmar Desaparcar",
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                int tiempoNoUsado = reserva.finalizarReserva();
                usuario.setTiempoGuardado(usuario.getTiempoGuardado() + tiempoNoUsado);
                
                sistemaParqueo.getGestorNotificaciones().notificarDesaparcado(reserva, tiempoNoUsado);

                JOptionPane.showMessageDialog(vista, 
                    "Vehículo desaparcado exitosamente.\n" +
                    "Tiempo no usado: " + tiempoNoUsado + " minutos\n" +
                    "Este tiempo ha sido agregado a su tiempo guardado.");

                actualizarTablaReservasActivas();
                actualizarTiempoGuardado();
            }
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
            DefaultTableModel modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            modelo.addColumn("ID Reserva");
            modelo.addColumn("Espacio");
            modelo.addColumn("Vehículo");
            modelo.addColumn("Inicio");
            modelo.addColumn("Fin");
            modelo.addColumn("Tiempo Restante (min)");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime ahora = LocalDateTime.now();
            
            List<Reserva> todasLasReservas = sistemaParqueo.getGestorReservas().getReservas();
            List<Reserva> reservasActivas = new ArrayList<>();
            
            // Procesar cada reserva individualmente
            for (Reserva reserva : todasLasReservas) {
                try {
                    // Cargar el usuario si es necesario
                    if (reserva.getUsuario() == null && reserva.getUsuarioId() != null) {
                        Usuario usuarioCargado = Usuario.cargar(reserva.getUsuarioId());
                        if (usuarioCargado instanceof UsuarioParqueo) {
                            reserva.setUsuario((UsuarioParqueo)usuarioCargado);
                        }
                    }

                    // Verificar si la reserva pertenece al usuario actual y está activa
                    if (reserva.getUsuario() != null && 
                        reserva.getUsuario().getId().equals(usuario.getId()) && 
                        reserva.estaActiva()) {
                        reservasActivas.add(reserva);
                    }
                } catch (Exception e) {
                    Logger.getLogger(ControladorUsuarioParqueo.class.getName())
                          .warning("Error procesando reserva: " + reserva.getIdReserva());
                }
            }

            // Agregar las reservas activas a la tabla
            for (Reserva reserva : reservasActivas) {
                long minutosRestantes = ChronoUnit.MINUTES.between(ahora, reserva.getHoraFin());
                if (minutosRestantes < 0) minutosRestantes = 0;
                
                modelo.addRow(new Object[]{
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNumero(),
                    reserva.getVehiculo().getPlaca(),
                    reserva.getHoraInicio().format(formatter),
                    reserva.getHoraFin().format(formatter),
                    minutosRestantes
                });
            }
            
            vista.getTblReservasActivas().setModel(modelo);
            
        } catch (Exception e) {
            Logger.getLogger(ControladorUsuarioParqueo.class.getName())
                  .log(Level.SEVERE, "Error al actualizar tabla de reservas activas", e);
        }
    }

    private boolean esReservaValida(Reserva reserva) {
        return reserva != null && 
               reserva.getUsuario() != null && 
               reserva.getEspacio() != null && 
               reserva.getVehiculo() != null;
    }

    private void agregarFilaReserva(DefaultTableModel modelo, Reserva reserva, 
                                  LocalDateTime ahora, DateTimeFormatter formatter) {
        try {
            long minutosRestantes = ChronoUnit.MINUTES.between(ahora, reserva.getHoraFin());
            if (minutosRestantes < 0) minutosRestantes = 0;
            
            modelo.addRow(new Object[]{
                reserva.getIdReserva(),
                reserva.getEspacio().getNumero(),
                reserva.getVehiculo().getPlaca(),
                reserva.getHoraInicio().format(formatter),
                reserva.getHoraFin().format(formatter),
                minutosRestantes
            });
        } catch (Exception e) {
            LOGGER.warning("Error al procesar reserva: " + reserva.getIdReserva());
        }
    }

    private void ajustarColumnasTabla() {
        vista.getTblReservasActivas().getColumnModel().getColumn(0).setPreferredWidth(200); // ID Reserva
        vista.getTblReservasActivas().getColumnModel().getColumn(1).setPreferredWidth(80);  // Espacio
        vista.getTblReservasActivas().getColumnModel().getColumn(2).setPreferredWidth(100); // Vehículo
        vista.getTblReservasActivas().getColumnModel().getColumn(3).setPreferredWidth(130); // Inicio
        vista.getTblReservasActivas().getColumnModel().getColumn(4).setPreferredWidth(130); // Fin
        vista.getTblReservasActivas().getColumnModel().getColumn(5).setPreferredWidth(100); // Tiempo Restante
    }

    // Metodo para actualizar la tabla de multas
    private void actualizarTablaMultas() {
        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Vehículo", "Espacio", "Fecha", "Monto Total", "Estado"},
            0
        );

        List<Multa> multasUsuario = sistemaParqueo.getGestorMultas().getMultas().stream()
            .filter(m -> usuario.getVehiculos().stream()
                .anyMatch(v -> v.getPlaca().equals(m.getVehiculo().getPlaca())))
            .sorted((m1, m2) -> m2.getFechaHora().compareTo(m1.getFechaHora()))
            .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Multa multa : multasUsuario) {
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
    }

    // Metodo para pagar una multa
    private void pagarMulta() {
        // Obtener la fila seleccionada
        int filaSeleccionada = vista.getTblMultas().getSelectedRow();
        // Verificar si se selecciono una fila
        if (filaSeleccionada != -1) {
            // Obtener el id de la multa
            String idMulta = (String) vista.getTblMultas().getValueAt(filaSeleccionada, 0);
            try {
                // Buscar la multa
                Multa multa = sistemaParqueo.getGestorMultas().buscarMulta(idMulta);
                // Verificar si la multa no esta pagada
                if (multa != null && !multa.getPagada()) {
                    // Pagar la multa
                    multa.pagar();
                    // Mostrar el mensaje de confirmacion
                    JOptionPane.showMessageDialog(vista, "Multa pagada exitosamente.");
                    // Actualizar la tabla de multas
                    actualizarTablaMultas();
                } else {
                    // Mostrar el mensaje de error
                    JOptionPane.showMessageDialog(vista, "La multa no existe o ya ha sido pagada.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione una multa para pagar.", "Error", JOptionPane.ERROR_MESSAGE);
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
    
}