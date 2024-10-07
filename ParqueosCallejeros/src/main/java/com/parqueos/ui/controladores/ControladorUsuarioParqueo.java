package com.parqueos.ui.controladores;

import com.parqueos.ui.vistas.VistaUsuarioParqueo;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
        vista.getBtnParquear().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parquear();
            }
        });

        vista.getBtnAgregarTiempo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarTiempo();
            }
        });

        vista.getBtnDesaparcar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desaparcar();
            }
        });

        vista.getBtnVerEspaciosDisponibles().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verEspaciosDisponibles();
            }
        });

        vista.getBtnVerHistorial().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verHistorial();
            }
        });

        vista.getBtnVerMultas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verMultas();
            }
        });

        vista.getBtnVerMultas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verMultas();
            }
        });

        vista.getBtnPagarMulta().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pagarMulta();
            }
        });

        actualizarTablaReservasActivas();
        actualizarTablaMultas();
        actualizarTiempoGuardado();
    }

    private void parquear() {
        String numeroEspacio = JOptionPane.showInputDialog(vista, "Ingrese el número de espacio:");
        String placaVehiculo = JOptionPane.showInputDialog(vista, "Ingrese la placa del vehículo:");
        String tiempoCompradoStr = JOptionPane.showInputDialog(vista, "Ingrese el tiempo a comprar en minutos:");
        
        try {
            int tiempoComprado = Integer.parseInt(tiempoCompradoStr);
            Reserva reserva = sistemaParqueo.parquear(token, numeroEspacio, placaVehiculo, tiempoComprado);
            JOptionPane.showMessageDialog(vista, "Parqueo exitoso. ID de reserva: " + reserva.getIdReserva());
            actualizarTiempoGuardado();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Tiempo inválido. Por favor ingrese un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarTiempo() {
        String idReserva = JOptionPane.showInputDialog(vista, "Ingrese el ID de la reserva:");
        String tiempoAdicionalStr = JOptionPane.showInputDialog(vista, "Ingrese el tiempo adicional en minutos:");
        
        try {
            int tiempoAdicional = Integer.parseInt(tiempoAdicionalStr);
            sistemaParqueo.agregarTiempo(token, idReserva, tiempoAdicional);
            JOptionPane.showMessageDialog(vista, "Tiempo agregado exitosamente.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Tiempo inválido. Por favor ingrese un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desaparcar() {
        String idReserva = JOptionPane.showInputDialog(vista, "Ingrese el ID de la reserva a finalizar:");
        
        try {
            sistemaParqueo.desaparcar(token, idReserva);
            JOptionPane.showMessageDialog(vista, "Vehículo desaparcado exitosamente.");
            actualizarTiempoGuardado();
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verEspaciosDisponibles() {
        try {
            List<EspacioParqueo> espaciosDisponibles = sistemaParqueo.obtenerEspaciosDisponibles(token);
            StringBuilder mensaje = new StringBuilder("Espacios disponibles:\n");
            for (EspacioParqueo espacio : espaciosDisponibles) {
                mensaje.append("Espacio ").append(espacio.getNumero()).append("\n");
            }
            JOptionPane.showMessageDialog(vista, mensaje.toString());
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verHistorial() {
        List<Reserva> historial = sistemaParqueo.obtenerHistorialReservas(token);
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No tiene reservas en su historial.");
        } else {
            // Crear y mostrar una nueva ventana con el historial
            JFrame frameHistorial = new JFrame("Historial de Reservas");
            JTable tablaHistorial = new JTable();
            DefaultTableModel modeloHistorial = new DefaultTableModel();
            modeloHistorial.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Vehículo", "Inicio", "Fin"});
            for (Reserva reserva : historial) {
                modeloHistorial.addRow(new Object[]{
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNumero(),
                    reserva.getVehiculo().getPlaca(),
                    reserva.getHoraInicio(),
                    reserva.getHoraFin()
                });
            }
            tablaHistorial.setModel(modeloHistorial);
            frameHistorial.add(new JScrollPane(tablaHistorial));
            frameHistorial.pack();
            frameHistorial.setVisible(true);
        }
    }

    private void actualizarTablaReservasActivas() {
        List<Reserva> reservasActivas = usuario.getReservasActivas();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Vehículo", "Inicio", "Fin"});
        for (Reserva reserva : reservasActivas) {
            modelo.addRow(new Object[]{
                reserva.getIdReserva(),
                reserva.getEspacio().getNumero(),
                reserva.getVehiculo().getPlaca(),
                reserva.getHoraInicio(),
                reserva.getHoraFin()
            });
        }
        vista.getTblReservasActivas().setModel(modelo);
    }

    private void actualizarTablaMultas() {
        try {
            List<Multa> multas = sistemaParqueo.obtenerMultasUsuario(token);
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.setColumnIdentifiers(new Object[]{"ID", "Vehículo", "Espacio", "Fecha", "Monto", "Pagada"});
            for (Multa multa : multas) {
                modelo.addRow(new Object[]{
                    multa.getIdMulta(),
                    multa.getVehiculo().getPlaca(),
                    multa.getEspacio().getNumero(),
                    multa.getFechaHora(),
                    multa.getMonto(),
                    multa.getPagada() ? "Sí" : "No"
                });
            }
            vista.getTblMultas().setModel(modelo);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verMultas() {
        try {
            List<Multa> multas = sistemaParqueo.obtenerMultasUsuario(token);
            if (multas.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "No tiene multas registradas.");
            } else {
                StringBuilder mensaje = new StringBuilder("Multas:\n\n");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (Multa multa : multas) {
                    mensaje.append("ID: ").append(multa.getIdMulta())
                           .append("\nVehículo: ").append(multa.getVehiculo().getPlaca())
                           .append("\nEspacio: ").append(multa.getEspacio().getNumero())
                           .append("\nFecha y hora: ").append(multa.getFechaHora().format(formatter))
                           .append("\nMonto: $").append(String.format("%.2f", multa.getMonto()))
                           .append("\n\n");
                }
                JOptionPane.showMessageDialog(vista, mensaje.toString(), "Multas", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pagarMulta() {
        int filaSeleccionada = vista.getTblMultas().getSelectedRow();
        if (filaSeleccionada != -1) {
            String idMulta = (String) vista.getTblMultas().getValueAt(filaSeleccionada, 0);
            try {
                sistemaParqueo.pagarMulta(token, idMulta);
                JOptionPane.showMessageDialog(vista, "Multa pagada exitosamente.");
                actualizarTablaMultas();
            } catch (IllegalStateException | IllegalArgumentException e) {
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