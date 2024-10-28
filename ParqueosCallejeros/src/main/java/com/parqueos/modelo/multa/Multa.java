package com.parqueos.modelo.multa;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

import java.util.Comparator;
import java.time.LocalTime;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Multa implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_MULTAS = "multas.json";
    
    @JsonProperty("idMulta")
    private final String idMulta;
    
    @JsonProperty("vehiculo")
    private final Vehiculo vehiculo;
    
    @JsonProperty("espacio")
    private final EspacioParqueo espacio;
    
    @JsonProperty("inspector")
    private final Inspector inspector;
    
    @JsonProperty("fechaHora")
    private final LocalDateTime fechaHora;
    
    @JsonProperty("monto")
    private int monto;
    
    @JsonProperty("pagada")
    private boolean pagada;

    @JsonProperty("detallesInfraccion")
    private final String detallesInfraccion;

    @JsonCreator
    public Multa() {
        this.idMulta = UUID.randomUUID().toString();
        this.fechaHora = LocalDateTime.now();
        this.monto = 0;
        this.pagada = false;
        this.detallesInfraccion = "";
        this.vehiculo = null;
        this.espacio = null;
        this.inspector = null;
    }

    // Constructor de la multa
    public Multa(Vehiculo vehiculo, EspacioParqueo espacio, Inspector inspector, int montoBase) {
        this.idMulta = UUID.randomUUID().toString();
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.inspector = inspector;
        this.fechaHora = LocalDateTime.now();
        this.pagada = false;
        
        // Calcular el monto total incluyendo el tiempo sin pagar
        this.monto = calcularMontoTotal();
        this.detallesInfraccion = generarDetallesInfraccion();
    }

    // Metodo para cargar las referencias del vehiculo
    public void cargarReferencias() {
        // Cargar el propietario del vehiculo
        if (vehiculo != null && vehiculo.getPropietarioId() != null) {
            // Si el propietario del vehiculo existe, cargar el usuario parqueo
            UsuarioParqueo propietario = (UsuarioParqueo) Usuario.cargar(vehiculo.getPropietarioId());
            
            // Asignar el propietario al vehiculo
            vehiculo.setPropietario(propietario);
        }
    }

    // Metodo para calcular el monto total de la multa
    private int calcularMontoTotal( ) {
        ConfiguracionParqueo config = ConfiguracionParqueo.obtenerInstancia();
        int precioHora = config.getPrecioHora();
        int costoMulta = config.getCostoMulta();
        int montoTotal = 0;

        if (espacio != null && espacio.estaOcupado()) {
            // Buscar la última reserva para este espacio
            List<Reserva> reservas = Reserva.cargarTodas().stream()
                .filter(r -> r.getEspacio().getId().equals(espacio.getId()))
                .sorted((r1, r2) -> r2.getHoraFin().compareTo(r1.getHoraFin()))
                .collect(Collectors.toList());

            if (!reservas.isEmpty()) {
                Reserva ultimaReserva = reservas.get(0);
                LocalDateTime horaFinReserva = ultimaReserva.getHoraFin();
                LocalDateTime horaActual = LocalDateTime.now();

                // 1. Calcular el costo de la reserva original
                long minutosReservados = ChronoUnit.MINUTES.between(
                    ultimaReserva.getHoraInicio(), 
                    ultimaReserva.getHoraFin()
                );
                double horasReservadas = minutosReservados / 60.0;
                montoTotal += (int)(horasReservadas * precioHora);

                // 2. Si hay tiempo extra después de la reserva, aplicar la multa
                if (horaActual.isAfter(horaFinReserva)) {
                    long minutosExtra = ChronoUnit.MINUTES.between(horaFinReserva, horaActual);
                    // Convertir a horas redondeando hacia arriba
                    int horasExtra = (int) Math.ceil(minutosExtra / 60.0);
                    montoTotal += horasExtra * costoMulta;
                }
            }
        }

        return montoTotal;
    }

    // Metodo para calcular las horas en el horario de regulación
    private long calcularHorasEnHorarioRegulacion(LocalDateTime inicio, LocalDateTime fin, ConfiguracionParqueo config) {
        // Inicializar el contador de horas
        long horasTotal = 0;
        LocalDateTime actual = inicio;

        // Mientras la fecha y hora actual sea antes de la fecha y hora de fin  
        while (actual.isBefore(fin)) {
            // Obtener la hora actual
            LocalTime tiempoActual = actual.toLocalTime();

            // Si la hora actual es después del horario de inicio y antes del horario de fin
            if (tiempoActual.isAfter(config.getHorarioInicio()) && 
                tiempoActual.isBefore(config.getHorarioFin())) {
                // Incrementar el contador de horas
                horasTotal++;
            }

            // Incrementar la hora actual en una hora
            actual = actual.plusHours(1);
        }

        // Retornar el total de horas en el horario de regulación
        return horasTotal;
    }

    // Metodo para obtener la reserva actual
    public Reserva obtenerReservaActual() {
        // Cargar todas las reservas
        List<Reserva> reservas = Reserva.cargarTodas();

        // Filtrar las reservas por el espacio y que esten activas
        return reservas.stream()
            .filter(r -> r.getEspacioId().equals(espacio.getId()) && r.estaActiva())
            .findFirst()
            .orElse(null);
    }

    // Metodo para generar los detalles de la infraccion
    private String generarDetallesInfraccion() {
        // Crear un StringBuilder para almacenar los detalles
        StringBuilder detalles = new StringBuilder();

        // Agregar los detalles de la infraccion
        detalles.append("INFRACCIÓN DE PARQUEO\n");
        detalles.append("Fecha y hora: ").append(fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        detalles.append("Espacio: ").append(espacio.getNumero()).append("\n");
        detalles.append("Terminal de inspección: ").append(inspector.getTerminalId()).append("\n");
        detalles.append("Inspector: ").append(inspector.getNombre()).append(" ").append(inspector.getApellidos()).append("\n");
        detalles.append("Motivo: Vehículo estacionado sin pago válido\n");
        detalles.append("Monto base de multa: ₡").append(String.format("%d", ConfiguracionParqueo.obtenerInstancia().getCostoMulta())).append("\n");
        detalles.append("Monto total: ₡").append(String.format("%d", monto)).append("\n");
        
        // Si el vehiculo tiene un propietario, agregar los detalles del propietario
        if(vehiculo != null && vehiculo.getPropietario() != null) {
            // Agregar los detalles del propietario
            detalles.append("\nPropietario registrado: ").append(vehiculo.getPropietario().getNombre())
                   .append(" ").append(vehiculo.getPropietario().getApellidos());
        }

        // Retornar los detalles de la infraccion
        return detalles.toString();
    }

    // Metodo para obtener los detalles completos de la multa
    public String getDetallesCompletos() {
        // Crear un StringBuilder para almacenar los detalles
        StringBuilder detalles = new StringBuilder(detallesInfraccion);

        // Agregar el monto de la multa
        detalles.append("\nMonto de la multa: ₡").append(String.format("%d", monto));

        // Si el vehiculo existe, agregar los detalles del vehiculo
        if (vehiculo != null) {
            detalles.append("\nPlaca del vehículo: ").append(vehiculo.getPlaca());
            if (vehiculo.getPropietario() != null) {
                // Obtener el propietario del vehiculo  
                UsuarioParqueo propietario = vehiculo.getPropietario();
                // Agregar los detalles del propietario
                detalles.append("\nPropietario: ")
                        .append(propietario.getNombre())
                        .append(" ")
                        .append(propietario.getApellidos());
            }
        }

        // Retornar los detalles completos de la multa
        return detalles.toString();
    }

    // Metodo para guardar la multa
    public void guardar() {
        // Cargar todas las multas del json y agregar la multa actual
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
        multas.add(this);

        // Guardar las multas en el json
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    // Metodo para actualizar la multa
    public void actualizar() {
        // Cargar todas las multas del json 
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);

        // Buscar la multa actual
        for (int i = 0; i < multas.size(); i++) {
            if (multas.get(i).getIdMulta().equals(this.idMulta)) {
                // Actualizar la multa cuando se encuentre
                multas.set(i, this);
                break;
            }
        }

        // Guardar las multas en el json
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    // Metodo para eliminar la multa
    public void eliminar() {
        // Cargar todas las multas del json
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);

        // Eliminar la multa actual
        multas.removeIf(m -> m.getIdMulta().equals(this.idMulta));

        // Guardar las multas en el json
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    // Metodo para cargar la multa
    public static Multa cargar(String id) {
        // Cargar todas las multas del json
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);

        // Buscar la multa actual
        return multas.stream()
                .filter(m -> m.getIdMulta().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Metodo para cargar todas las multas
    public static List<Multa> cargarTodas() {
        // Cargar todas las multas del json
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
    }

    // Metodo para pagar la multa
    public void pagar() {
        // Excepcion para cuando la multa ya ha sido pagada
        if (this.pagada) {
            throw new IllegalStateException("Esta multa ya ha sido pagada");
        }

        // Pagar la multa
        this.pagada = true;
        this.actualizar();
    }

    // Getters y setters
    public String getIdMulta() {
        return idMulta;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public EspacioParqueo getEspacio() {
        return espacio;
    }

    public Inspector getInspector() {
        return inspector;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        if (monto <= 0) {
            // Excepcion para cuando el monto es negativo o cero
            throw new IllegalArgumentException("El monto de la multa debe ser positivo");
        }
        this.monto = monto;
        this.actualizar();
    }

    public boolean getPagada() {
        return pagada;
    }

    public String getDetallesInfraccion() {
        return detallesInfraccion;
    }

    @Override
    public String toString() {
        return "Multa{" +
                "idMulta='" + idMulta + '\'' +
                ", vehiculo=" + vehiculo.getPlaca() +
                ", espacio=" + espacio.getNumero() +
                ", inspector=" + inspector.getIdUsuario() +
                ", fechaHora=" + fechaHora +
                ", monto=" + monto +
                ", pagada=" + pagada +
                ", detallesInfraccion='" + detallesInfraccion + '\'' +
                '}';
    }
}