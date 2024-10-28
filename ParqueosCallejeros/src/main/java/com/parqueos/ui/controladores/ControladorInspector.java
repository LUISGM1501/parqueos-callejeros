package com.parqueos.ui.controladores;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
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
        try {
            // Validar que se haya ingresado un espacio
            String numeroEspacio = vista.getTxtEspacio().getText().trim();
            if (numeroEspacio.isEmpty()) {
                vista.setResultadoRevision("Debe ingresar un número de espacio.");
                return;
            }

            // Buscar el espacio en el sistema
            EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio);
            
            // Verificar si el espacio existe
            if (espacio == null) {
                vista.setResultadoRevision("El espacio no existe.");
                return;
            }

            // Verificar si el espacio está ocupado
            if (!espacio.estaOcupado()) {
                vista.setResultadoRevision("El espacio está vacío.");
                return;
            }

            // Construir mensaje detallado
            StringBuilder resultado = new StringBuilder();
            resultado.append("Estado del espacio ").append(espacio.getNumero()).append(":\n\n");
            resultado.append("- Ocupado: Sí\n");
            resultado.append("- Pagado: ").append(espacio.estaPagado() ? "Sí" : "No").append("\n");
            
            // Verificar si el espacio tiene un vehículo
            if (espacio.getVehiculoActual() != null) {
                resultado.append("- Vehículo: ").append(espacio.getVehiculoActual().getPlaca()).append("\n");
                
                // Verificar si el espacio no está pagado
                if (!espacio.estaPagado()) {
                    resultado.append("\nATENCIÓN: Se puede generar una multa para este espacio.");
                    // Autocompletar la placa para facilitar la generación de multa
                    vista.getTxtPlaca().setText(espacio.getVehiculoActual().getPlaca());
                }
            }

            // Mostrar el resultado
            vista.setResultadoRevision(resultado.toString());
            
        } catch (Exception e) {
            vista.setResultadoRevision("Error al revisar el espacio: " + e.getMessage());
        }
    }

    // Metodo para generar una multa
    private void generarMulta() {
        try {
            // Validar campos
            String numeroEspacio = vista.getTxtEspacio().getText().trim();
            String placa = vista.getTxtPlaca().getText().trim();

            // Verificar si los campos están vacíos
            if (numeroEspacio.isEmpty() || placa.isEmpty()) {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vista, 
                    "Debe ingresar el número de espacio y la placa del vehículo.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar el espacio en el sistema
            EspacioParqueo espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(numeroEspacio);
            
            // Verificar si el espacio existe
            if (espacio == null) {
                JOptionPane.showMessageDialog(vista, 
                    "El espacio especificado no existe.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el espacio está ocupado
            if (!espacio.estaOcupado()) {
                JOptionPane.showMessageDialog(vista, 
                    "El espacio no está ocupado.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Asegurarnos de obtener un monto válido de la configuración
            ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
            int costoMulta = config.getCostoMulta();
            
            // Verificar si el costo de la multa es válido
            if (costoMulta <= 0) {
                JOptionPane.showMessageDialog(vista,
                    "El costo de la multa no está configurado correctamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Logs para ver por que no funciona xd
            System.out.println("Costo multa configurado: " + costoMulta);
            System.out.println("Espacio: " + espacio.getNumero());
            System.out.println("Vehículo: " + espacio.getVehiculoActual().getPlaca());
            System.out.println("Inspector ID: " + inspector.getId());
            System.out.println("Estado del espacio - Ocupado: " + espacio.estaOcupado());
            System.out.println("Estado del espacio - Pagado: " + espacio.estaPagado());

            // Crear la multa con el monto correcto
            Multa multa = inspector.generarMulta(espacio, costoMulta);
            sistemaParqueo.getGestorMultas().agregarMulta(multa);
            System.out.println("Multa creada con ID: " + multa.getIdMulta());
            
            multa.guardar();
            System.out.println("Multa guardada exitosamente");

            // Notificar al propietario si está registrado
            if (multa.getVehiculo() != null && multa.getVehiculo().getPropietario() != null) {
                System.out.println("Notificando al propietario: " + multa.getVehiculo().getPropietario().getNombre());
                sistemaParqueo.getGestorNotificaciones().notificarMultaGenerada(multa);
            } else {
                System.out.println("No se pudo notificar: vehículo o propietario no registrado");
            }

            JOptionPane.showMessageDialog(vista, 
                String.format("Multa generada con éxito.\nMonto base: ₡%d\nMonto total: ₡%d", 
                    costoMulta,
                    multa.getMonto()),
                "Multa Generada",
                JOptionPane.INFORMATION_MESSAGE);

            // Limpiar campos y actualizar vista
            vista.getTxtEspacio().setText("");
            vista.getTxtPlaca().setText("");
            vista.setResultadoRevision("");
            actualizarTablaMultas();

        } catch (Exception e) {
            // Mostrar el mensaje de error
            System.out.println("Error detallado al generar multa:");
            e.printStackTrace(); // Esto ayudará a ver el error en la consola
            System.out.println("Mensaje de error: " + e.getMessage());
            System.out.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "No hay causa"));
            
            JOptionPane.showMessageDialog(vista, 
                "Error al generar la multa: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
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
        // Crear el modelo de la tabla 
        DefaultTableModel modelo = new DefaultTableModel(
            // Crear las columnas
            new String[]{"ID", "Esp.", "Placa", "Fecha", "Mont. Base", "Mont. Total", "Est.", "Propietario"},
            // Crear las filas
            0
        );

        // Crear el formateador de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Obtener las multas del gestor de multas
        List<Multa> multas = sistemaParqueo.getGestorMultas().getMultas().stream()
            // Filtrar las multas por el inspector
            .filter(m -> m.getInspector().getId().equals(inspector.getId()))
            // Ordenar las multas por fecha
            .sorted((m1, m2) -> m2.getFechaHora().compareTo(m1.getFechaHora()))
            // Convertir a lista
            .collect(Collectors.toList());

        // Iterar sobre las multas
        for (Multa multa : multas) {
            String propietario = "No registrado";
            // Verificar si el vehículo tiene un propietario
            if (multa.getVehiculo() != null && multa.getVehiculo().getPropietarioId() != null) {
                // Buscar el propietario en el sistema
                UsuarioParqueo prop = (UsuarioParqueo) Usuario.cargar(multa.getVehiculo().getPropietarioId());
                // Verificar si el propietario existe
                if (prop != null) {
                    propietario = prop.getNombre() + " " + prop.getApellidos();
                }
            }

            // Agregar la fila a la tabla
            modelo.addRow(new Object[]{
                multa.getIdMulta(),
                multa.getEspacio().getNumero(),
                multa.getVehiculo().getPlaca(),
                multa.getFechaHora().format(formatter),
                String.format("₡%d", ConfiguracionParqueo.obtenerInstancia().getCostoMulta()),
                String.format("₡%d", multa.getMonto()),
                multa.getPagada() ? "Pagada" : "Pendiente",
                propietario
            });
        }

        // Actualizar la tabla
        vista.getTblMultasGeneradas().setModel(modelo);
    }
}