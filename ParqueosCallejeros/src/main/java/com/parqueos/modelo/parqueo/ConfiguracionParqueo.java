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

    // Constructor de la configuracion del parqueo
    private ConfiguracionParqueo() {
        // Valores por defecto
        this.horarioInicio = LocalTime.of(8, 0);
        this.horarioFin = LocalTime.of(18, 0);
        this.precioHora = 1000;
        this.tiempoMinimo = 30;
        this.costoMulta = 5000;
        this.espacios = new ArrayList<>();
    }

    // Metodo para obtener la instancia de la configuracion del parqueo
    public static synchronized ConfiguracionParqueo obtenerInstancia() {
        // Si la instancia no existe, cargar la configuracion del parqueo
        if (instancia == null) {
            instancia = cargarConfiguracion();
            // Si la configuracion no existe, crear una nueva configuracion
            if (instancia == null) {
                instancia = new ConfiguracionParqueo();
            }
        }
        // Si la configuracion existe, devolver la configuracion
        return instancia;
    }

    // Metodo para cargar la configuracion del parqueo
    private static ConfiguracionParqueo cargarConfiguracion() {
        // Cargar la configuracion del json    
        List<ConfiguracionParqueo> configuraciones = GestorArchivos.cargarTodosLosElementos(ARCHIVO_CONFIGURACION, ConfiguracionParqueo.class);
        
        // Si la configuracion no existe, devolver null
        return configuraciones.isEmpty() ? null : configuraciones.get(0);
    }

    // Metodo para actualizar la configuracion del parqueo
    public void actualizarConfiguracion(LocalTime horarioInicio, LocalTime horarioFin, int precioHora, int tiempoMinimo, int costoMulta) {
        // Actualizar la configuracion del parqueo
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.precioHora = precioHora;
        this.tiempoMinimo = tiempoMinimo;
        this.costoMulta = costoMulta;

        // Guardar la configuracion del parqueo
        guardar();
    }

    // Metodo para agregar espacios al parqueo
    public void agregarEspacios(int inicio, int fin) {
        // Agregar espacios al parqueo
        for (int i = inicio; i <= fin; i++) {
            // Formatear el numero del espacio
            String numero = String.format("%05d", i);

            // Si el espacio no existe, agregar el espacio al parqueo
            if (!existeEspacio(numero)) {
                espacios.add(new EspacioParqueo(numero));
            }
        }
        // Guardar la configuracion del parqueo
        guardar();
    }

    // Metodo para eliminar espacios del parqueo
    public void eliminarEspacios(int inicio, int fin) {
        // Eliminar espacios del parqueo
        espacios.removeIf(espacio -> {
            // Eliminar el espacio si el numero del espacio es mayor o igual al inicio y menor o igual al fin
            int numero = Integer.parseInt(espacio.getNumero());
            return numero >= inicio && numero <= fin;
        });

        // Guardar la configuracion del parqueo
        guardar();
    }

    // Metodo para verificar si existe un espacio en el parqueo
    private boolean existeEspacio(String numero) {
        // Verificar si existe un espacio con el numero
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

    // Metodo para guardar la configuracion del parqueo
    public void guardar() {
        // Crear una lista de configuraciones
        List<ConfiguracionParqueo> configuraciones = new ArrayList<>();

        // Agregar la configuracion actual
        configuraciones.add(this);

        // Guardar la configuracion en el json
        GestorArchivos.guardarTodo(configuraciones, ARCHIVO_CONFIGURACION);
    }

    // Metodo para convertir la configuracion del parqueo a un string
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