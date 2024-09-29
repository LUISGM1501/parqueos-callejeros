package com.parqueos.modelo.multa;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.vehiculo.Vehiculo;

public class Multa implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String idMulta;
    private Vehiculo vehiculo;
    private EspacioParqueo espacio;
    private Inspector inspector;
    private LocalDateTime fechaHora;
    private double monto;

    public Multa(Vehiculo vehiculo, EspacioParqueo espacio, Inspector inspector) {
        this.idMulta = UUID.randomUUID().toString();
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.inspector = inspector;
        this.fechaHora = LocalDateTime.now();
        this.monto = calcularMonto(); // Deberia obtener el monto de la configuracion del parqueo
    }

    private double calcularMonto() {
        // Falta implementar la logica para obtener el monto de la multa de la configuracion del parqueo
        // Por ahora, retornamos un valor fijo
        return 50.0;
    }

    // Getters y setters
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
        this.monto = monto;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setInspector(Inspector inspector) {
        this.inspector = inspector;
    }   

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public void setEspacio(EspacioParqueo espacio) {
        this.espacio = espacio;
    }

    public String getIdMulta() {
        return idMulta;
    }


    @Override
    public String toString() {
        return "Multa{" +
                "vehiculo=" + vehiculo.getPlaca() +
                ", espacio=" + espacio.getNumero() +
                ", inspector=" + inspector.getIdUsuario() +
                ", fechaHora=" + fechaHora +
                ", monto=" + monto +
                '}';
    }
}