package com.parqueos.servicios;

import java.util.ArrayList;
import java.util.List;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

public class SistemaParqueo {
    private ConfiguracionParqueo configuracion;
    private List<EspacioParqueo> espacios;
    private List<Reserva> reservas;
    private List<Usuario> usuarios;
    private List<Multa> multas;

    private static final String ARCHIVO_CONFIGURACION = "configuracion.dat";
    private static final String ARCHIVO_ESPACIOS = "espacios.dat";
    private static final String ARCHIVO_RESERVAS = "reservas.dat";
    private static final String ARCHIVO_USUARIOS = "usuarios.dat";
    private static final String ARCHIVO_MULTAS = "multas.dat";

    public SistemaParqueo() {
        this.espacios = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.multas = new ArrayList<>();
        cargarDatos();
    }

    private void cargarDatos() {
        configuracion = (ConfiguracionParqueo) GestorArchivos.cargarObjetos(ARCHIVO_CONFIGURACION);
        espacios = GestorArchivos.cargarObjetos(ARCHIVO_ESPACIOS);
        reservas = GestorArchivos.cargarObjetos(ARCHIVO_RESERVAS);
        usuarios = GestorArchivos.cargarObjetos(ARCHIVO_USUARIOS);
        multas = GestorArchivos.cargarObjetos(ARCHIVO_MULTAS);
    }

    public void guardarDatos() {
        GestorArchivos.guardarObjeto(configuracion, ARCHIVO_CONFIGURACION);
        GestorArchivos.guardarObjeto(espacios, ARCHIVO_ESPACIOS);
        GestorArchivos.guardarObjeto(reservas, ARCHIVO_RESERVAS);
        GestorArchivos.guardarObjeto(usuarios, ARCHIVO_USUARIOS);
        GestorArchivos.guardarObjeto(multas, ARCHIVO_MULTAS);
    }

    public void setConfiguracion(ConfiguracionParqueo configuracion) {
        this.configuracion = configuracion;
        GestorArchivos.guardarObjeto(configuracion, ARCHIVO_CONFIGURACION);
    }

    public ConfiguracionParqueo getConfiguracion() {
        return configuracion;
    }

    public void agregarEspacio(EspacioParqueo espacio) {
        espacios.add(espacio);
        GestorArchivos.agregarObjeto(espacio, ARCHIVO_ESPACIOS);
    }

    public void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
        GestorArchivos.agregarObjeto(usuario, ARCHIVO_USUARIOS);
    }

    public void actualizarUsuario(Usuario usuario) {
        int index = usuarios.indexOf(usuario);
        if (index != -1) {
            usuarios.set(index, usuario);
            GestorArchivos.actualizarObjeto(usuario, usuario.getIdUsuario(), ARCHIVO_USUARIOS);
        }
    }

    public void eliminarUsuario(String idUsuario) {
        usuarios.removeIf(u -> u.getIdUsuario().equals(idUsuario));
        GestorArchivos.eliminarObjeto(idUsuario, ARCHIVO_USUARIOS);
    }

    public Reserva crearReserva(UsuarioParqueo usuario, EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        if (!espacio.estaDisponible()) {
            throw new IllegalStateException("El espacio no esta disponible");
        }
        Reserva reserva = new Reserva(usuario, espacio, vehiculo, tiempoComprado);
        reservas.add(reserva);
        espacio.ocupar(vehiculo);
        GestorArchivos.agregarObjeto(reserva, ARCHIVO_RESERVAS);
        return reserva;
    }

    public void finalizarReserva(Reserva reserva) {
        reserva.finalizarReserva();
        reserva.getEspacio().liberar();
        GestorArchivos.actualizarObjeto(reserva, reserva.getIdReserva(), ARCHIVO_RESERVAS);
    }

    public List<EspacioParqueo> obtenerEspaciosDisponibles() {
        return espacios.stream()
                       .filter(EspacioParqueo::estaDisponible)
                       .toList();
    }

    public void agregarMulta(Multa multa) {
        multas.add(multa);
        GestorArchivos.agregarObjeto(multa, ARCHIVO_MULTAS);
    }

    public void actualizarMulta(Multa multa) {
        int index = multas.indexOf(multa);
        if (index != -1) {
            multas.set(index, multa);
            GestorArchivos.actualizarObjeto(multa, multa.getIdMulta(), ARCHIVO_MULTAS);
        }
    }

    public void eliminarMulta(String idMulta) {
        multas.removeIf(m -> m.getIdMulta().equals(idMulta));
        GestorArchivos.eliminarObjeto(idMulta, ARCHIVO_MULTAS);
    }
}