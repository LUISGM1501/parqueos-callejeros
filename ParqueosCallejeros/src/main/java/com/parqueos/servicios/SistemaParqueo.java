package com.parqueos.servicios;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class SistemaParqueo {
    private ConfiguracionParqueo configuracion;
    private List<EspacioParqueo> espacios;
    private List<Reserva> reservas;
    private List<Usuario> usuarios;

    public SistemaParqueo() {
        this.espacios = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.usuarios = new ArrayList<>();
    }

    public void setConfiguracion(ConfiguracionParqueo configuracion) {
        this.configuracion = configuracion;
    }

    public ConfiguracionParqueo getConfiguracion() {
        return configuracion;
    }

    public void agregarEspacio(EspacioParqueo espacio) {
        espacios.add(espacio);
    }

    public void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public Reserva crearReserva(UsuarioParqueo usuario, EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        if (!espacio.estaDisponible()) {
            throw new IllegalStateException("El espacio no esta disponible");
        }
        Reserva reserva = new Reserva(usuario, espacio, vehiculo, tiempoComprado);
        reservas.add(reserva);
        espacio.ocupar(vehiculo);
        return reserva;
    }

    public void finalizarReserva(Reserva reserva) {
        reserva.finalizarReserva();
        reserva.getEspacio().liberar();
    }

    public List<EspacioParqueo> obtenerEspaciosDisponibles() {
        return espacios.stream()
                       .filter(EspacioParqueo::estaDisponible)
                       .toList();
    }

}