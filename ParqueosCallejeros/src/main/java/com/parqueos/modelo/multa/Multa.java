package com.parqueos.modelo.multa;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

public class Multa implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_MULTAS = "multas.json";
    
    private final String idMulta;
    private final Vehiculo vehiculo;
    private final EspacioParqueo espacio;
    private final Inspector inspector;
    private final LocalDateTime fechaHora;
    private double monto;
    private boolean pagada;

    // Constructor de la multa
    public Multa(Vehiculo vehiculo, EspacioParqueo espacio, Inspector inspector, double monto) {
        this.idMulta = UUID.randomUUID().toString();
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.inspector = inspector;
        this.fechaHora = LocalDateTime.now();
        this.monto = monto;
        this.pagada = false;
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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
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
                '}';
    }
}