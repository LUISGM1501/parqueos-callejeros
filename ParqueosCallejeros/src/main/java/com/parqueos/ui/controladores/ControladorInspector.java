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

// Controlador para el inspector
public class ControladorInspector extends ControladorBase {
    private final VistaInspector vista;
    private final SistemaParqueo sistemaParqueo;
    private final Inspector inspector;
    private final String token;

    // Constructor para inicializar el controlador
    public ControladorInspector(VistaInspector vista, SistemaParqueo sistemaParqueo, Inspector inspector, String token) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.inspector = inspector;
        this.token = token;
        inicializar();
    }

    @Override
    // Metodo para inicializar el controlador
    protected void inicializar() {
        vista.getBtnRevisarParqueo().addActionListener(e -> revisarParqueo());
        vista.getBtnGenerarMulta().addActionListener(e -> generarMulta());
        vista.getBtnVerReporteEspacios().addActionListener(e -> verReporteEspacios());
        vista.getBtnVerReporteMultas().addActionListener(e -> verReporteMultas());
        actualizarTablaMultas();
    }

    // Metodo para revisar el parqueo
    private void revisarParqueo() {
        // Obtener el numero del espacio
        String numeroEspacio = vista.getTxtEspacio().getText();

        // Buscar el espacio en el gestor de espacios
        EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio);
        
        // Si el espacio no existe
        if (espacio == null) {
            // Mostrar el mensaje de error
            vista.setResultadoRevision("El espacio no existe.");
            return;
        }

        // Si el espacio no esta ocupado
        if (!espacio.estaOcupado()) {
            // Mostrar el mensaje de error
            vista.setResultadoRevision("El espacio está vacío.");
            return;
        }

        // Si el espacio esta pagado
        if (espacio.estaPagado()) {
            // Mostrar el mensaje de error
            vista.setResultadoRevision("El espacio está ocupado y pagado correctamente.");
        } else {
            // Mostrar el mensaje de error
            vista.setResultadoRevision("El espacio está ocupado pero no está pagado. Se puede generar una multa.");
        }
    }

    // Metodo para generar una multa
    private void generarMulta() {
        // Obtener el numero del espacio
        String numeroEspacio = vista.getTxtEspacio().getText();
        // Obtener la placa del vehiculo
        String placa = vista.getTxtPlaca().getText();
        // Buscar el espacio en el gestor de espacios
        EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio);
        
        // Si el espacio no existe
        if (espacio == null) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "El espacio no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si el espacio no esta ocupado o esta pagado
        if (!espacio.estaOcupado() || espacio.estaPagado()) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "No se puede generar una multa para este espacio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generar la multa
        try {
            // Generar la multa
            Multa multa = inspector.generarMulta(espacio, sistemaParqueo.getConfiguracion(token).getCostoMulta());
            // Agregar la multa al gestor de multas
            sistemaParqueo.getGestorMultas().agregarMulta(multa);
            // Mostrar el mensaje de confirmacion
            JOptionPane.showMessageDialog(vista, "Multa generada con éxito.");
            // Actualizar la tabla de multas
            actualizarTablaMultas();
        } catch (Exception e) {
            // Mostrar el mensaje de error
            JOptionPane.showMessageDialog(vista, "Error al generar la multa: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo para ver el reporte de espacios
    private void verReporteEspacios() {
        // Obtener los espacios del gestor de espacios
        List<EspacioParqueo> espacios = sistemaParqueo.getGestorEspacios().getEspacios();
        // Generar el reporte
        Reporte reporte = inspector.generarReporteEspacios(espacios);
        // Mostrar el reporte
        mostrarReporte(reporte);
    }

    // Metodo para ver el reporte de multas
    private void verReporteMultas() {
        // Obtener las multas del gestor de multas
        LocalDate fechaInicio = LocalDate.now().minusDays(7); 
        LocalDate fechaFin = LocalDate.now();
        // Generar el reporte
        List<Multa> multas = sistemaParqueo.getGestorMultas().getMultas();
        Reporte reporte = inspector.generarReporteMultas(multas, fechaInicio, fechaFin);
        // Mostrar el reporte
        mostrarReporte(reporte);
    }

    // Metodo para mostrar el reporte
    private void mostrarReporte(Reporte reporte) {
        LocalDate fechaInicio = LocalDate.now().minusDays(7); 
        LocalDate fechaFin = LocalDate.now();

        // Crear el area de texto para el reporte
        JTextArea areaReporte = new JTextArea(20, 50);
        // Agregar el reporte al area de texto
        areaReporte.setText(reporte.generarReporte(fechaInicio, fechaFin));
        // No permitir editar el reporte
        areaReporte.setEditable(false);
        // Crear un panel de desplazamiento para el area de texto
        JScrollPane scrollPane = new JScrollPane(areaReporte);
        // Mostrar el reporte en un dialogo
        JOptionPane.showMessageDialog(vista, scrollPane, "Reporte", JOptionPane.PLAIN_MESSAGE);
    }

    // Metodo para actualizar la tabla de multas
    private void actualizarTablaMultas() {
        // Obtener las multas del gestor de multas
        List<Multa> multas = sistemaParqueo.getGestorMultas().getMultas();
        // Crear el modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel();
        // Agregar las columnas
        modelo.setColumnIdentifiers(new Object[]{"ID", "Espacio", "Placa", "Fecha", "Monto", "Pagada"});
        // Formato de la fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Agregar las multas al modelo
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
        
        // Actualizar la tabla
        vista.getTblMultasGeneradas().setModel(modelo);
    }
}