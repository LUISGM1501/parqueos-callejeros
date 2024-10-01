package com.parqueos.servicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.modelo.parqueo.Reserva;
import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.reportes.Reporte;
import com.parqueos.util.GestorArchivos;

public class SistemaParqueo {
    private ConfiguracionParqueo configuracion;
    private List<EspacioParqueo> espacios;
    private List<Reserva> reservas;
    private List<Usuario> usuarios;
    private List<Multa> multas;
    private AuthService authService;
    private GestorNotificaciones gestorNotificaciones;

    private static final String ARCHIVO_CONFIGURACION = "configuracion.json";
    private static final String ARCHIVO_ESPACIOS = "espacios.json";
    private static final String ARCHIVO_RESERVAS = "reservas.json";
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static final String ARCHIVO_MULTAS = "multas.json";

    public SistemaParqueo() {
        this.espacios = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.multas = new ArrayList<>();
        this.gestorNotificaciones = new GestorNotificaciones();
        this.authService = new AuthService(gestorNotificaciones);
        cargarDatos();
    }

    private void cargarDatos() {
        // Cargar configuración
        List<ConfiguracionParqueo> configuraciones = GestorArchivos.cargarTodosLosElementos(ARCHIVO_CONFIGURACION, ConfiguracionParqueo.class);
        if (!configuraciones.isEmpty()) {
            configuracion = configuraciones.get(0);
        } else {
            configuracion = ConfiguracionParqueo.obtenerInstancia();
            guardarDatos(); // Guarda la configuración por defecto
        }

        // Cargar espacios
        espacios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ESPACIOS, EspacioParqueo.class);

        // Cargar reservas
        reservas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_RESERVAS, Reserva.class);

        // Cargar usuarios
        usuarios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS, Usuario.class);

        // Registrar usuarios en el AuthService
        for (Usuario usuario : usuarios) {
            authService.registrarUsuario(usuario);
        }

        // Cargar multas
        multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
    }

    public void guardarDatos() {
        GestorArchivos.guardarTodo(List.of(configuracion), ARCHIVO_CONFIGURACION);
        GestorArchivos.guardarTodo(espacios, ARCHIVO_ESPACIOS);
        GestorArchivos.guardarTodo(reservas, ARCHIVO_RESERVAS);
        GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    public String iniciarSesion(String idUsuario, String pin) {
        return authService.iniciarSesion(idUsuario, pin);
    }

    public void cerrarSesion(String token) {
        authService.cerrarSesion(token);
    }

    public void cambiarPin(String token, String pinActual, String nuevoPin) {
        authService.cambiarPin(token, pinActual, nuevoPin);
        guardarDatos();
    }

    public void solicitarRestablecimientoPin(String idUsuario) {
        authService.solicitarRestablecimientoPin(idUsuario);
    }

    public void setConfiguracion(String token, ConfiguracionParqueo configuracion) {
        if (authService.obtenerUsuarioAutenticado(token) instanceof Administrador) {
            this.configuracion = configuracion;
            guardarDatos();
        } else {
            throw new IllegalStateException("Usuario no autorizado");
        }
    }

    public ConfiguracionParqueo getConfiguracion(String token) {
        if (authService.estaAutenticado(token)) {
            return configuracion;
        } else {
            throw new IllegalStateException("Usuario no autenticado");
        }
    }

    public void agregarEspacio(String token, EspacioParqueo espacio) {
        if (authService.obtenerUsuarioAutenticado(token) instanceof Administrador) {
            espacios.add(espacio);
            guardarDatos();
        } else {
            throw new IllegalStateException("Usuario no autorizado");
        }
    }

    public void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
        authService.registrarUsuario(usuario);
        guardarDatos();
    }

    public void actualizarUsuario(Usuario usuario) {
        int index = usuarios.indexOf(usuario);
        if (index != -1) {
            usuarios.set(index, usuario);
            guardarDatos();
        }
    }

    public void eliminarUsuario(String idUsuario) {
        usuarios.removeIf(u -> u.getIdUsuario().equals(idUsuario));
        guardarDatos();
    }

    public Reserva crearReserva(UsuarioParqueo usuario, EspacioParqueo espacio, Vehiculo vehiculo, int tiempoComprado) {
        if (!espacio.estaDisponible()) {
            throw new IllegalStateException("El espacio no esta disponible");
        }
        Reserva reserva = new Reserva(usuario, espacio, vehiculo, tiempoComprado);
        reservas.add(reserva);
        espacio.ocupar(vehiculo);
        guardarDatos();
        return reserva;
    }

    public void finalizarReserva(Reserva reserva) {
        reserva.finalizarReserva();
        reserva.getEspacio().liberar();
        guardarDatos();
    }

    public List<EspacioParqueo> obtenerEspaciosDisponibles(String token) {
        if (authService.estaAutenticado(token)) {
            return espacios.stream()
                           .filter(EspacioParqueo::estaDisponible)
                           .toList();
        } else {
            throw new IllegalStateException("Usuario no autenticado");
        }
    }

    public void agregarMulta(Multa multa) {
        multas.add(multa);
        guardarDatos();
    }

    public void actualizarMulta(Multa multa) {
        int index = multas.indexOf(multa);
        if (index != -1) {
            multas.set(index, multa);
            guardarDatos();
        }
    }

    public void eliminarMulta(String idMulta) {
        multas.removeIf(m -> m.getIdMulta().equals(idMulta));
        guardarDatos();
    }

    // Metodos de la clase UsuarioParqueo
    // -------------------------------------------------------------------------------------------------   

    public Reserva parquear(String token, String numeroEspacio, String placaVehiculo, int tiempoComprado) {
        Usuario usuario = authService.obtenerUsuarioAutenticado(token);
        if (!(usuario instanceof UsuarioParqueo)) {
            throw new IllegalStateException("Usuario no autorizado para parquear");
        }
        UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
        
        EspacioParqueo espacio = buscarEspacio(numeroEspacio);
        Vehiculo vehiculo = buscarVehiculo(usuarioParqueo, placaVehiculo);
        
        Reserva reserva = usuarioParqueo.parquear(espacio, vehiculo, tiempoComprado);
        reservas.add(reserva);
        espacio.ocupar(vehiculo);
        guardarDatos();
        
        gestorNotificaciones.enviarCorreo(usuario.getEmail(), "Reserva de parqueo",
            "Se ha realizado una reserva para el espacio " + numeroEspacio + 
            " por " + tiempoComprado + " minutos.");
        
        return reserva;
    }

    public void agregarTiempo(String token, String idReserva, int tiempoAdicional) {
        Usuario usuario = authService.obtenerUsuarioAutenticado(token);
        if (!(usuario instanceof UsuarioParqueo)) {
            throw new IllegalStateException("Usuario no autorizado para agregar tiempo");
        }
        UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
        
        Reserva reserva = buscarReserva(idReserva);
        if (!reserva.getUsuario().equals(usuarioParqueo)) {
            throw new IllegalStateException("La reserva no pertenece a este usuario");
        }
        
        usuarioParqueo.agregarTiempo(reserva, tiempoAdicional);
        guardarDatos();
        
        gestorNotificaciones.enviarCorreo(usuario.getEmail(), "Tiempo adicional",
            "Se han agregado " + tiempoAdicional + " minutos a su reserva.");
    }

    public void desaparcar(String token, String idReserva) {
        Usuario usuario = authService.obtenerUsuarioAutenticado(token);
        if (!(usuario instanceof UsuarioParqueo)) {
            throw new IllegalStateException("Usuario no autorizado para desaparcar");
        }
        UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
        
        Reserva reserva = buscarReserva(idReserva);
        if (!reserva.getUsuario().equals(usuarioParqueo)) {
            throw new IllegalStateException("La reserva no pertenece a este usuario");
        }
        
        usuarioParqueo.desaparcar(reserva);
        guardarDatos();
        
        gestorNotificaciones.enviarCorreo(usuario.getEmail(), "Desaparcado",
            "Se ha desaparcado su vehiculo.");
    }

    private Reserva buscarReserva(String idReserva) {
        return reservas.stream()
                       .filter(r -> r.getIdReserva().equals(idReserva))
                       .findFirst()
                       .orElseThrow(() -> new IllegalStateException("Reserva no encontrada"));
    }

    private EspacioParqueo buscarEspacio(String numeroEspacio) {
        return espacios.stream()
                       .filter(e -> e.getNumero().equals(numeroEspacio))
                       .findFirst()
                       .orElseThrow(() -> new IllegalStateException("Espacio no encontrado"));
    }

    private Vehiculo buscarVehiculo(UsuarioParqueo usuarioParqueo, String placaVehiculo) {
        return usuarioParqueo.getVehiculos().stream()
                              .filter(v -> v.getPlaca().equals(placaVehiculo))
                              .findFirst()
                              .orElseThrow(() -> new IllegalStateException("Vehiculo no encontrado"));
    }   

    //Metodos de la clase Inspector
    // -------------------------------------------------------------------------------------------------   

    public Multa revisarParqueo(String token, String numeroEspacio) {
        Usuario usuario = authService.obtenerUsuarioAutenticado(token);
        if (!(usuario instanceof Inspector)) {
            throw new IllegalStateException("Usuario no autorizado para revisar parqueos");
        }
        Inspector inspector = (Inspector) usuario;
        
        EspacioParqueo espacio = buscarEspacio(numeroEspacio);
        Multa multa = inspector.revisarParqueo(espacio);
        
        if (multa != null) {
            agregarMulta(multa);
            String placaVehiculo = multa.getVehiculo().getPlaca();
            gestorNotificaciones.enviarCorreo(multa.getVehiculo().getPropietario().getEmail(),
                "Multa de parqueo",
                "Se ha generado una multa para su vehículo con placa " + placaVehiculo +
                " en el espacio " + numeroEspacio);
        }
        
        return multa;
    }

    public List<EspacioParqueo> obtenerEspaciosOcupados(String token) {
        if (authService.obtenerUsuarioAutenticado(token) instanceof Inspector) {
            return espacios.stream()
                           .filter(EspacioParqueo::estaOcupado)
                           .collect(Collectors.toList());
        } else {
            throw new IllegalStateException("Usuario no autorizado");
        }
    }

    public Reporte generarReporteEspaciosInspector(String token) {
        Usuario usuario = authService.obtenerUsuarioAutenticado(token);
        if (!(usuario instanceof Inspector)) {
            throw new IllegalStateException("Usuario no autorizado para generar este reporte");
        }
        Inspector inspector = (Inspector) usuario;
        return inspector.generarReporteEspacios(espacios);
    }

    public Reporte generarReporteMultasInspector(String token, LocalDate fechaInicio, LocalDate fechaFin) {
        Usuario usuario = authService.obtenerUsuarioAutenticado(token);
        if (!(usuario instanceof Inspector)) {
            throw new IllegalStateException("Usuario no autorizado para generar este reporte");
        }
        Inspector inspector = (Inspector) usuario;
        return inspector.generarReporteMultas(multas, fechaInicio, fechaFin);
    }
}
