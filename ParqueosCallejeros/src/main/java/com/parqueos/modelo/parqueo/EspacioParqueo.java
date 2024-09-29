package com.parqueos.modelo.parqueo;

import com.parqueos.modelo.vehiculo.Vehiculo;

public class EspacioParqueo {
    private String numero;
    private boolean ocupado;
    private boolean pagado;
    private Vehiculo vehiculoActual;

    public EspacioParqueo(String numero) {
        this.numero = numero;
        this.ocupado = false;
        this.pagado = false;
        this.vehiculoActual = null;
    }

    public void ocupar(Vehiculo vehiculo) {
        this.ocupado = true;
        this.vehiculoActual = vehiculo;
    }

    public void liberar() {
        this.ocupado = false;
        this.pagado = false;
        this.vehiculoActual = null;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public boolean estaPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    // Getters y setters
    public String getNumero() {
        return numero;
    }

    public Vehiculo getVehiculoActual() {
        return vehiculoActual;
    }

    public boolean estaDisponible() {
        return !ocupado;
    }

    @Override
    public String toString() {
        return "EspacioParqueo{" +
                "numero='" + numero + '\'' +
                ", ocupado=" + ocupado +
                ", pagado=" + pagado +
                ", vehiculoActual=" + (vehiculoActual != null ? vehiculoActual.getPlaca() : "ninguno") +
                '}';
    }
}