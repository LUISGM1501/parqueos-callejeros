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

    public Multa(Vehiculo vehiculo, EspacioParqueo espacio, Inspector inspector, double monto) {
        this.idMulta = UUID.randomUUID().toString();
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.inspector = inspector;
        this.fechaHora = LocalDateTime.now();
        this.monto = monto;
        this.pagada = false;
    }

    public void guardar() {
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
        multas.add(this);
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    public void actualizar() {
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
        for (int i = 0; i < multas.size(); i++) {
            if (multas.get(i).getIdMulta().equals(this.idMulta)) {
                multas.set(i, this);
                break;
            }
        }
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    public void eliminar() {
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
        multas.removeIf(m -> m.getIdMulta().equals(this.idMulta));
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    public static Multa cargar(String id) {
        List<Multa> multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
        return multas.stream()
                .filter(m -> m.getIdMulta().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<Multa> cargarTodas() {
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
    }

    public void pagar() {
        if (this.pagada) {
            throw new IllegalStateException("Esta multa ya ha sido pagada");
        }
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