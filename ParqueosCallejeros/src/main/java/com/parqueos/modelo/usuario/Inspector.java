package com.parqueos.modelo.usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.reportes.Reporte;
import com.parqueos.reportes.ReporteFactory;
import com.parqueos.reportes.ReporteFactory.TipoReporte;
import com.parqueos.util.GeneradorPDF;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Inspector extends Usuario {
    @JsonProperty("terminalId")
    private String terminalId;

    // Constructor de inspector 
    public Inspector(String nombre, String apellidos, int telefono, String email, String direccion, 
                     String idUsuario, String pin, String terminalId) {
        super(nombre, apellidos, telefono, email, direccion, idUsuario, pin, TipoUsuario.INSPECTOR);
        this.terminalId = terminalId;
    }

    // Constructor sin argumentos para el JSON
    @JsonCreator
    public Inspector() {
        super("", "", 0, "", "", "", "", TipoUsuario.INSPECTOR);
    }

    // Getters y setters
    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        this.actualizarEnArchivo();
    }

    // Metodos
    public static List<Inspector> cargarTodosInspectors() {
        // Cargar todos los usuarios
        return Usuario.cargarTodos().stream()
                // Filtrar los inspectores
                .filter(u -> u.getTipoUsuario() == TipoUsuario.INSPECTOR)
                .map(u -> (Inspector) u)
                .collect(Collectors.toList());
    }

    // Metodo para revisar un parqueo
    public Multa revisarParqueo(EspacioParqueo espacio) {
        // Verificar si el espacio está ocupado y no pagado
        if (espacio.estaOcupado() && !espacio.estaPagado()) {
            // Obtener la configuracion del parqueo
            ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
            
            // Generar la multa
            return generarMulta(espacio, config.getCostoMulta());
        }
        return null;
    }

    // Metodo para generar una multa
    public Multa generarMulta(EspacioParqueo espacio, int monto) {
        // Crear la multa
        Multa multa = new Multa(espacio.getVehiculoActual(), espacio, this, monto);

        // Guardar la multa
        multa.guardar();
        
        // Generar PDF de la multa para dejar en el vehículo
        generarPDFMulta(multa);

        // Retornar la multa
        return multa;
    }

    // Metodo para generar los detalles de la infraccion
    public String generarDetallesInfraccion(EspacioParqueo espacio) {
        // Crear un StringBuilder para almacenar los detalles
        StringBuilder detalles = new StringBuilder();

        // Agregar los detalles de la infraccion
        detalles.append("INFRACCIÓN DE PARQUEO\n");
        detalles.append("Fecha y hora: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        detalles.append("Espacio: ").append(espacio.getNumero()).append("\n");
        detalles.append("Terminal de inspección: ").append(this.getTerminalId()).append("\n");
        detalles.append("Inspector: ").append(this.getNombre()).append(" ").append(this.getApellidos()).append("\n");
        detalles.append("Motivo: Vehículo estacionado sin pago válido\n");
        return detalles.toString();
    }

    // Metodo para generar el PDF de la multa
    private void generarPDFMulta(Multa multa) {
        try {
            // Crear el nombre del archivo
            String nombreArchivo = "multa_" + multa.getIdMulta() + ".pdf";

            // Generar el PDF
            GeneradorPDF.generarPDF(nombreArchivo, multa.getDetallesCompletos());
        } catch (Exception e) {
            // Lanzar una excepcion si ocurre un error al generar el PDF
            throw new RuntimeException("Error al generar PDF de la multa", e);
        }
    }

    // Metodo para generar un reporte de espacios
    public Reporte generarReporteEspacios(List<EspacioParqueo> espacios) {
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();

        // Retornar el reporte de espacios
        return ReporteFactory.crearReporte(TipoReporte.ESPACIOS, fechaActual, fechaActual, espacios, null, null, null);
    }

    // Metodo para generar un reporte de multas
    public Reporte generarReporteMultas(List<Multa> multas, LocalDate fechaInicio, LocalDate fechaFin) {
        // Filtrar las multas por fecha
        multas = multas.stream()
            .filter(m -> !m.getFechaHora().toLocalDate().isBefore(fechaInicio) && !m.getFechaHora().toLocalDate().isAfter(fechaFin))
            .collect(Collectors.toList());
        
        // Retornar el reporte de multas
        return ReporteFactory.crearReporte(TipoReporte.MULTAS, fechaInicio, fechaFin, null, null, null, multas);
    }

}
