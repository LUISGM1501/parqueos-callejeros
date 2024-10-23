package com.parqueos.ui.controladores;

import java.awt.Dimension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
            if (vista.getCmbVehiculos().getSelectedItem() == null) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar un vehículo");
                return;
            }

            // Validar que se haya ingresado un espacio
            String numeroEspacio = vista.getTxtEspacio().getText().trim();
            if (numeroEspacio.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Debe ingresar un número de espacio");
                return;
            }

            // Obtener el vehículo seleccionado
            String placa = (String) vista.getCmbVehiculos().getSelectedItem();
            Vehiculo vehiculo = usuario.getVehiculos().stream()
                .filter(v -> v.getPlaca().equals(placa))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Vehículo no encontrado"));

            // Verificar si el vehículo ya está parqueado
            List<Reserva> reservasActivas = sistemaParqueo.getGestorReservas().getReservas().stream()
                .filter(r -> r.estaActiva() && r.getVehiculo().getPlaca().equals(placa))
                .collect(Collectors.toList());

            if (!reservasActivas.isEmpty()) {
                JOptionPane.showMessageDialog(vista, 
                    "El vehículo ya se encuentra parqueado en el espacio " + 
                    reservasActivas.get(0).getEspacio().getNumero(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar el espacio
            EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio);
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

            String idReserva = (String) vista.getTblReservasActivas().getValueAt(filaSeleccionada, 0);
            if (idReserva == null || idReserva.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, 
                    "Error: ID de reserva inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Reserva reserva = sistemaParqueo.getGestorReservas().buscarReserva(idReserva);
            if (reserva == null || !reserva.estaActiva()) {
                JOptionPane.showMessageDialog(vista, 
                    "La reserva no está activa o ya no existe",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                actualizarTablaReservasActivas();
                return;
            }

            // Confirmar la acción
            int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro que desea desaparcar el vehículo " + reserva.getVehiculo().getPlaca() + 
                " del espacio " + reserva.getEspacio().getNumero() + "?",
                "Confirmar Desaparcado",
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                int tiempoNoUsado = reserva.finalizarReserva();
                usuario.setTiempoGuardado(usuario.getTiempoGuardado() + tiempoNoUsado);
                
                // Enviar notificación
                sistemaParqueo.getGestorNotificaciones().notificarDesaparcado(reserva, tiempoNoUsado);

                JOptionPane.showMessageDialog(vista, 
                    "Vehículo desaparcado exitosamente.\n" +
                    "Tiempo no usado: " + tiempoNoUsado + " minutos\n" +
                    "Este tiempo ha sido agregado a su tiempo guardado.");

                actualizarTablaReservasActivas();
                actualizarTiempoGuardado();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, 
                "Error al desaparcar: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
            List<Reserva> historialReservas = todasLasReservas.stream()
                .filter(r -> r.getUsuario().getId().equals(usuario.getId()))
                .sorted((r1, r2) -> r2.getHoraInicio().compareTo(r1.getHoraInicio()))
                .collect(Collectors.toList());

            if (historialReservas.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "No tiene reservas en su historial.");
                return;
            }

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
            JOptionPane.showMessageDialog(vista,
                "Error al mostrar historial: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Metodo para actualizar la tabla de reservas activas
    private void actualizarTablaReservasActivas() {
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
        
        List<Reserva> reservasActivas = sistemaParqueo.getGestorReservas()
            .getReservas().stream()
            .filter(r -> r.getUsuario().getId().equals(usuario.getId()) && r.estaActiva())
            .collect(Collectors.toList());

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
        // Ajustar el ancho de las columnas
        vista.getTblReservasActivas().getColumnModel().getColumn(0).setPreferredWidth(200); // ID Reserva
        vista.getTblReservasActivas().getColumnModel().getColumn(1).setPreferredWidth(80);  // Espacio
        vista.getTblReservasActivas().getColumnModel().getColumn(2).setPreferredWidth(100); // Vehículo
        vista.getTblReservasActivas().getColumnModel().getColumn(3).setPreferredWidth(130); // Inicio
        vista.getTblReservasActivas().getColumnModel().getColumn(4).setPreferredWidth(130); // Fin
        vista.getTblReservasActivas().getColumnModel().getColumn(5).setPreferredWidth(100); // Tiempo Restante
    }

    // Metodo para actualizar la tabla de multas
    private void actualizarTablaMultas() {
        // Obtener las multas del usuario
        List<Multa> multas = sistemaParqueo.getGestorMultas().obtenerMultasUsuario(usuario);
        // Crear el modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel();
        // Asignar los encabezados de la tabla
        modelo.setColumnIdentifiers(new Object[]{"ID", "Vehículo", "Espacio", "Fecha", "Monto", "Pagada"});
        // Formatear la fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        // Agregar las multas al modelo
        for (Multa multa : multas) {
            modelo.addRow(new Object[]{
                multa.getIdMulta(),
                multa.getVehiculo().getPlaca(),
                multa.getEspacio().getNumero(),
                multa.getFechaHora().format(formatter),
                multa.getMonto(),
                multa.getPagada() ? "Sí" : "No"
            });
        }
        // Asignar el modelo a la tabla
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
        
        for (Vehiculo vehiculo : usuario.getVehiculos()) {
            model.addElement(vehiculo.getPlaca());
        }
    }
    
}