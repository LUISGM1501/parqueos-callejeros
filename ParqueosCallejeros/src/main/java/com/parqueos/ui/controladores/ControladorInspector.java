package com.parqueos.ui.controladores;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.reportes.Reporte;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaInspector;

public class ControladorInspector extends ControladorBase {
    private final VistaInspector vista;
    private final SistemaParqueo sistemaParqueo;
    private final Inspector inspector;
    private final String token;

    public ControladorInspector(VistaInspector vista, SistemaParqueo sistemaParqueo, Inspector inspector, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.inspector = inspector;
        this.token = token;
        inicializar();
    }

    @Override
    protected void inicializar() {
        vista.getBtnRevisarParqueo().addActionListener(e -> revisarParqueo());
        vista.getBtnGenerarMulta().addActionListener(e -> generarMulta());
        vista.getBtnVerReporteEspacios().addActionListener(e -> verReporteEspacios());
        vista.getBtnVerReporteMultas().addActionListener(e -> verReporteMultas());
        actualizarTablaMultas();
    }

    private void revisarParqueo() {
        String numeroEspacio = vista.getTxtEspacio().getText();
        EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio);
        
        if (espacio == null) {
            vista.setResultadoRevision("El espacio no existe.");
            return;
        }

        if (!espacio.estaOcupado()) {
            vista.setResultadoRevision("El espacio está vacío.");
            return;
        }

        if (espacio.estaPagado()) {
            vista.setResultadoRevision("El espacio está ocupado y pagado correctamente.");
        } else {
            vista.setResultadoRevision("El espacio está ocupado pero no está pagado. Se puede generar una multa.");
        }
    }

    private void generarMulta() {
        String numeroEspacio = vista.getTxtEspacio().getText();
        String placa = vista.getTxtPlaca().getText();
        EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio);
        
        if (espacio == null) {
            JOptionPane.showMessageDialog(vista, "El espacio no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!espacio.estaOcupado() || espacio.estaPagado()) {
            JOptionPane.showMessageDialog(vista, "No se puede generar una multa para este espacio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Multa multa = inspector.generarMulta(espacio, sistemaParqueo.getConfiguracion(token).getCostoMulta());
            sistemaParqueo.getGestorMultas().agregarMulta(multa);
            JOptionPane.showMessageDialog(vista, "Multa generada con éxito.");
            actualizarTablaMultas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al generar la multa: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verReporteEspacios() {
        List<EspacioParqueo> espacios = sistemaParqueo.getGestorEspacios().getEspacios();
        Reporte reporte = inspector.generarReporteEspacios(espacios);
        mostrarReporte(reporte);
    }

    private void verReporteMultas() {
        LocalDate fechaInicio = LocalDate.now().minusDays(7); 
        LocalDate fechaFin = LocalDate.now();
        List<Multa> multas = sistemaParqueo.getGestorMultas().getMultas();
        Reporte reporte = inspector.generarReporteMultas(multas, fechaInicio, fechaFin);
        mostrarReporte(reporte);
    }

    private void mostrarReporte(Reporte reporte) {
        LocalDate fechaInicio = LocalDate.now().minusDays(7); 
        LocalDate fechaFin = LocalDate.now();
        JTextArea areaReporte = new JTextArea(20, 50);
        areaReporte.setText(reporte.generarReporte(fechaInicio, fechaFin));
        areaReporte.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaReporte);
        JOptionPane.showMessageDialog(vista, scrollPane, "Reporte", JOptionPane.PLAIN_MESSAGE);
    }

    private void actualizarTablaMultas() {
        List<Multa> multas = sistemaParqueo.getGestorMultas().getMultas();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Placa", "Fecha", "Monto", "Pagada"});
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Multa multa : multas) {
            modelo.addRow(new Object[]{
                multa.getIdMulta(),
                multa.getEspacio().getNumero(),
                multa.getVehiculo().getPlaca(),
                multa.getFechaHora().format(formatter),
                multa.getMonto(),
                multa.getPagada() ? "Sí" : "No"
            });
        }
        
        vista.getTblMultasGeneradas().setModel(modelo);
    }
}