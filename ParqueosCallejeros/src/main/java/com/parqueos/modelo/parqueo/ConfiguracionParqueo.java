package com.parqueos.modelo.parqueo;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.parqueos.util.GestorArchivos;

public class ConfiguracionParqueo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_CONFIGURACION = "configuracion.json";
    
    private static ConfiguracionParqueo instancia;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private int precioHora;
    private int tiempoMinimo;
    private int costoMulta;
    private final List<EspacioParqueo> espacios;

    private ConfiguracionParqueo() {
        // Valores por defecto
        this.horarioInicio = LocalTime.of(8, 0);
        this.horarioFin = LocalTime.of(18, 0);
        this.precioHora = 1000;
        this.tiempoMinimo = 30;
        this.costoMulta = 5000;
        this.espacios = new ArrayList<>();
    }

    public static synchronized ConfiguracionParqueo obtenerInstancia() {
        if (instancia == null) {
            instancia = cargarConfiguracion();
            if (instancia == null) {
                instancia = new ConfiguracionParqueo();
            }
        }
        return instancia;
    }

    private static ConfiguracionParqueo cargarConfiguracion() {
        List<ConfiguracionParqueo> configuraciones = GestorArchivos.cargarTodosLosElementos(ARCHIVO_CONFIGURACION, ConfiguracionParqueo.class);
        return configuraciones.isEmpty() ? null : configuraciones.get(0);
    }

    public void actualizarConfiguracion(LocalTime horarioInicio, LocalTime horarioFin, int precioHora, int tiempoMinimo, int costoMulta) {
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.precioHora = precioHora;
        this.tiempoMinimo = tiempoMinimo;
        this.costoMulta = costoMulta;
        guardar();
    }

    public void agregarEspacios(int inicio, int fin) {
        for (int i = inicio; i <= fin; i++) {
            String numero = String.format("%05d", i);
            if (!existeEspacio(numero)) {
                espacios.add(new EspacioParqueo(numero));
            }
        }
        guardar();
    }

    public void eliminarEspacios(int inicio, int fin) {
        espacios.removeIf(espacio -> {
            int numero = Integer.parseInt(espacio.getNumero());
            return numero >= inicio && numero <= fin;
        });
        guardar();
    }

    private boolean existeEspacio(String numero) {
        return espacios.stream().anyMatch(espacio -> espacio.getNumero().equals(numero));
    }

    // Getters
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public LocalTime getHorarioFin() { return horarioFin; }
    public int getPrecioHora() { return precioHora; }
    public int getTiempoMinimo() { return tiempoMinimo; }
    public int getCostoMulta() { return costoMulta; }
    public List<EspacioParqueo> getEspacios() { return new ArrayList<>(espacios); }

    // Setters
    public void setHorarioInicio(LocalTime horarioInicio) { this.horarioInicio = horarioInicio; }
    public void setHorarioFin(LocalTime horarioFin) { this.horarioFin = horarioFin; }
    public void setPrecioHora(int precioHora) { this.precioHora = precioHora; }
    public void setTiempoMinimo(int tiempoMinimo) { this.tiempoMinimo = tiempoMinimo; }
    public void setCostoMulta(int costoMulta) { this.costoMulta = costoMulta; }

    public void guardar() {
        List<ConfiguracionParqueo> configuraciones = new ArrayList<>();
        configuraciones.add(this);
        GestorArchivos.guardarTodo(configuraciones, ARCHIVO_CONFIGURACION);
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