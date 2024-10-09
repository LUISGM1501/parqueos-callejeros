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

public class ControladorUsuarioParqueo extends ControladorBase {
    private VistaUsuarioParqueo vista;
    private SistemaParqueo sistemaParqueo;
    private UsuarioParqueo usuario;
    private String token;

    public ControladorUsuarioParqueo(VistaUsuarioParqueo vista, SistemaParqueo sistemaParqueo, UsuarioParqueo usuario, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.usuario = usuario;
        this.token = token;
        inicializar();
    }

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

    private void cargarVehiculos() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Vehiculo vehiculo : usuario.getVehiculos()) {
            model.addElement(vehiculo.getPlaca());
        }
        vista.getCmbVehiculos().setModel(model);
    }

    private void parquear() {
        String placa = (String) vista.getCmbVehiculos().getSelectedItem();
        String numeroEspacio = vista.getTxtEspacio().getText();
        int tiempoComprado = (Integer) vista.getSpnTiempo().getValue();

        try {
            Reserva reserva = sistemaParqueo.getGestorReservas().crearReserva(new Reserva(usuario, 
                sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio),
                usuario.getVehiculos().stream().filter(v -> v.getPlaca().equals(placa)).findFirst().orElseThrow(),
                tiempoComprado));
            JOptionPane.showMessageDialog(vista, "Parqueo exitoso. ID de reserva: " + reserva.getIdReserva());
            actualizarTablaReservasActivas();
            actualizarTiempoGuardado();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarTiempo() {
        String idReserva = JOptionPane.showInputDialog(vista, "Ingrese el ID de la reserva:");
        int tiempoAdicional = (Integer) vista.getSpnTiempo().getValue();
        
        try {
            Reserva reserva = sistemaParqueo.getGestorReservas().buscarReserva(idReserva);
            if (reserva != null && reserva.getUsuario().equals(usuario)) {
                reserva.extenderTiempo(tiempoAdicional);
                JOptionPane.showMessageDialog(vista, "Tiempo agregado exitosamente.");
                actualizarTablaReservasActivas();
            } else {
                JOptionPane.showMessageDialog(vista, "Reserva no encontrada o no pertenece al usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desaparcar() {
        String idReserva = JOptionPane.showInputDialog(vista, "Ingrese el ID de la reserva a finalizar:");
        
        try {
            Reserva reserva = sistemaParqueo.getGestorReservas().buscarReserva(idReserva);
            if (reserva != null && reserva.getUsuario().equals(usuario)) {
                int tiempoNoUsado = reserva.finalizarReserva();
                usuario.setTiempoGuardado(usuario.getTiempoGuardado() + tiempoNoUsado);
                JOptionPane.showMessageDialog(vista, "Vehículo desaparcado exitosamente. Tiempo guardado: " + tiempoNoUsado + " minutos.");
                actualizarTablaReservasActivas();
                actualizarTiempoGuardado();
            } else {
                JOptionPane.showMessageDialog(vista, "Reserva no encontrada o no pertenece al usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verEspaciosDisponibles() {
        List<EspacioParqueo> espaciosDisponibles = sistemaParqueo.getGestorEspacios().obtenerEspaciosDisponibles();
        StringBuilder mensaje = new StringBuilder("Espacios disponibles:\n");
        for (EspacioParqueo espacio : espaciosDisponibles) {
            mensaje.append("Espacio ").append(espacio.getNumero()).append("\n");
        }
        JOptionPane.showMessageDialog(vista, mensaje.toString());
    }

    private void verHistorial() {
        List<Reserva> historial = sistemaParqueo.getGestorReservas().obtenerHistorialReservas(usuario);
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No tiene reservas en su historial.");
        } else {
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Vehículo", "Inicio", "Fin"});
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Reserva reserva : historial) {
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
            JOptionPane.showMessageDialog(vista, scrollPane, "Historial de Reservas", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void actualizarTablaReservasActivas() {
        List<Reserva> reservasActivas = usuario.getReservasActivas();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Vehículo", "Inicio", "Fin"});
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Reserva reserva : reservasActivas) {
            modelo.addRow(new Object[]{
                reserva.getIdReserva(),
                reserva.getEspacio().getNumero(),
                reserva.getVehiculo().getPlaca(),
                reserva.getHoraInicio().format(formatter),
                reserva.getHoraFin().format(formatter)
            });
        }
        vista.getTblReservasActivas().setModel(modelo);
    }

    private void actualizarTablaMultas() {
        List<Multa> multas = sistemaParqueo.getGestorMultas().obtenerMultasUsuario(usuario);
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Vehículo", "Espacio", "Fecha", "Monto", "Pagada"});
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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
        vista.getTblMultas().setModel(modelo);
    }

    private void pagarMulta() {
        int filaSeleccionada = vista.getTblMultas().getSelectedRow();
        if (filaSeleccionada != -1) {
            String idMulta = (String) vista.getTblMultas().getValueAt(filaSeleccionada, 0);
            try {
                Multa multa = sistemaParqueo.getGestorMultas().buscarMulta(idMulta);
                if (multa != null && !multa.getPagada()) {
                    multa.pagar();
                    JOptionPane.showMessageDialog(vista, "Multa pagada exitosamente.");
                    actualizarTablaMultas();
                } else {
                    JOptionPane.showMessageDialog(vista, "La multa no existe o ya ha sido pagada.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione una multa para pagar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTiempoGuardado() {
        vista.actualizarTiempoGuardado(usuario.getTiempoGuardado());
    }
}