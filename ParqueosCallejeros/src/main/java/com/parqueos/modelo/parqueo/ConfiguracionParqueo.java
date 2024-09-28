package com.parqueos.modelo.parqueo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionParqueo {
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private int precioHora;
    private int tiempoMinimo;
    private int costoMulta;
    private List<EspacioParqueo> espacios;

    public ConfiguracionParqueo(LocalTime horarioInicio, LocalTime horarioFin, int precioHora, int tiempoMinimo, int costoMulta) {
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.precioHora = precioHora;
        this.tiempoMinimo = tiempoMinimo;
        this.costoMulta = costoMulta;
        this.espacios = new ArrayList<>();
    }

    public void agregarEspacios(int inicio, int fin) {
        for (int i = inicio; i <= fin; i++) {
            String numero = String.format("%05d", i);
            if (!existeEspacio(numero)) {
                espacios.add(new EspacioParqueo(numero));
            }
        }
    }

    public void eliminarEspacios(int inicio, int fin) {
        espacios.removeIf(espacio -> {
            int numero = Integer.parseInt(espacio.getNumero());
            return numero >= inicio && numero <= fin;
        });
    }

    private boolean existeEspacio(String numero) {
        return espacios.stream().anyMatch(espacio -> espacio.getNumero().equals(numero));
    }

    // Getters y setters
    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public int getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(int precioHora) {
        this.precioHora = precioHora;
    }

    public int getTiempoMinimo() {
        return tiempoMinimo;
    }

    public void setTiempoMinimo(int tiempoMinimo) {
        this.tiempoMinimo = tiempoMinimo;
    }

    public int getCostoMulta() {
        return costoMulta;
    }

    public void setCostoMulta(int costoMulta) {
        this.costoMulta = costoMulta;
    }

    public List<EspacioParqueo> getEspacios() {
        return new ArrayList<>(espacios);
    }

    @Override
    public String toString() {
        return "ConfiguracionParqueo{" +
                "horarioInicio=" + horarioInicio +
                ", horarioFin=" + horarioFin +
                ", precioHora=" + precioHora +
                ", tiempoMinimo=" + tiempoMinimo +
                ", costoMulta=" + costoMulta +
                ", espacios=" + espacios.size() +
                '}';
    }
}