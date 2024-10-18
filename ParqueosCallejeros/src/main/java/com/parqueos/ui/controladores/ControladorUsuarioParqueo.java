package com.parqueos.ui.controladores;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

        vista.getBtnParquear().addActionListener(e -> parquear());
        vista.getBtnAgregarTiempo().addActionListener(e -> agregarTiempo());
        vista.getBtnDesaparcar().addActionListener(e -> desaparcar());
        vista.getBtnVerEspaciosDisponibles().addActionListener(e -> verEspaciosDisponibles());
        vista.getBtnVerHistorial().addActionListener(e -> verHistorial());
        vista.getBtnVerMultas().addActionListener(e -> actualizarTablaMultas());
        vista.getBtnPagarMulta().addActionListener(e -> pagarMulta());
    }

    // Metodo para cargar los vehiculos del usuario
    private void cargarVehiculos() {
        // Crear el modelo del combo box
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        // Agregar los vehiculos al modelo
        for (Vehiculo vehiculo : usuario.getVehiculos()) {
            model.addElement(vehiculo.getPlaca());
        }
        // Asignar el modelo al combo box
        vista.getCmbVehiculos().setModel(model);
    }

    // Metodo para parquear un vehiculo
    private void parquear() {
        // Obtener la placa del vehiculo
        String placa = (String) vista.getCmbVehiculos().getSelectedItem();
        // Obtener el numero del espacio
        String numeroEspacio = vista.getTxtEspacio().getText();
        // Obtener el tiempo comprado
        int tiempoComprado = (Integer) vista.getSpnTiempo().getValue();

        try {
            // Crear la reserva
            Reserva reserva = sistemaParqueo.getGestorReservas().crearReserva(new Reserva(usuario, 
                sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio),
                usuario.getVehiculos().stream().filter(v -> v.getPlaca().equals(placa)).findFirst().orElseThrow(),
                tiempoComprado));
            // Mostrar el mensaje de confirmacion
            JOptionPane.showMessageDialog(vista, "Parqueo exitoso. ID de reserva: " + reserva.getIdReserva());
            // Actualizar la tabla de reservas activas
            actualizarTablaReservasActivas();
            // Actualizar el tiempo guardado
            actualizarTiempoGuardado();
        } catch (Exception e) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        // Obtener el id de la reserva
        String idReserva = JOptionPane.showInputDialog(vista, "Ingrese el ID de la reserva a finalizar:");
        
        try {
            // Buscar la reserva
            Reserva reserva = sistemaParqueo.getGestorReservas().buscarReserva(idReserva);
            // Verificar si la reserva pertenece al usuario
            if (reserva != null && reserva.getUsuario().equals(usuario)) {
                // Finalizar la reserva
                int tiempoNoUsado = reserva.finalizarReserva();
                // Actualizar el tiempo guardado
                usuario.setTiempoGuardado(usuario.getTiempoGuardado() + tiempoNoUsado);
                // Mostrar el mensaje de confirmacion
                JOptionPane.showMessageDialog(vista, "Vehículo desaparcado exitosamente. Tiempo guardado: " + tiempoNoUsado + " minutos.");
                // Actualizar la tabla de reservas activas
                actualizarTablaReservasActivas();
                // Actualizar el tiempo guardado
                actualizarTiempoGuardado();
            } else {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, "Reserva no encontrada o no pertenece al usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        // Obtener el historial de reservas
        List<Reserva> historial = sistemaParqueo.getGestorReservas().obtenerHistorialReservas(usuario);
        // Verificar si el historial esta vacio
        if (historial.isEmpty()) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "No tiene reservas en su historial.");
        } else {
            // Crear el modelo de la tabla
            DefaultTableModel modelo = new DefaultTableModel();
            // Asignar los encabezados de la tabla
            modelo.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Vehículo", "Inicio", "Fin"});
            // Formatear la fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            // Agregar las reservas al modelo
            for (Reserva reserva : historial) {
                modelo.addRow(new Object[]{
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNumero(),
                    reserva.getVehiculo().getPlaca(),
                    reserva.getHoraInicio().format(formatter),
                    reserva.getHoraFin().format(formatter)
                });
            }
            // Crear la tabla
            JTable tablaHistorial = new JTable(modelo);
            // Crear el panel de desplazamiento
            JScrollPane scrollPane = new JScrollPane(tablaHistorial);
            // Mostrar la tabla
            JOptionPane.showMessageDialog(vista, scrollPane, "Historial de Reservas", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // Metodo para actualizar la tabla de reservas activas
    private void actualizarTablaReservasActivas() {
        // Obtener las reservas activas
        List<Reserva> reservasActivas = usuario.getReservasActivas();
        // Crear el modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel();
        // Asignar los encabezados de la tabla
        modelo.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Vehículo", "Inicio", "Fin"});
        // Formatear la fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        // Agregar las reservas al modelo
        for (Reserva reserva : reservasActivas) {
            modelo.addRow(new Object[]{
                reserva.getIdReserva(),
                reserva.getEspacio().getNumero(),
                reserva.getVehiculo().getPlaca(),
                reserva.getHoraInicio().format(formatter),
                reserva.getHoraFin().format(formatter)
            });
        }
        // Asignar el modelo a la tabla
        vista.getTblReservasActivas().setModel(modelo);
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
}