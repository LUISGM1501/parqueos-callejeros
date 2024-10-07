package com.parqueos.modelo.parqueo;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import com.parqueos.util.GestorArchivos;

public class ConfiguracionParqueo implements Serializable   {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_CONFIGURACION = "configuracion.json";
    
    private static ConfiguracionParqueo instancia;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private int precioHora;
    private int tiempoMinimo;
    private int costoMulta;
    private List<EspacioParqueo> espacios;

    private ConfiguracionParqueo() {
        // Constructor privado para evitar instanciacion directa
        this.espacios = new ArrayList<>();
    }

    public static ConfiguracionParqueo obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionParqueo();
        }
        return instancia;
    }

    public static ConfiguracionParqueo setInstancia(ConfiguracionParqueo configuracion) {
        instancia = configuracion;
        return instancia;
    }

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

    public void guardar() {
        GestorArchivos.guardarElemento(this, ARCHIVO_CONFIGURACION);
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