package com.parqueos.servicios;

import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;
import com.parqueos.builders.UsuarioBuilder;
import com.parqueos.builders.UsuarioParqueoBuilder;
import com.parqueos.builders.AdministradorBuilder;
import com.parqueos.builders.InspectorBuilder;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

public class GestorUsuarios {
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static final Logger LOGGER = Logger.getLogger(GestorUsuarios.class.getName());
    
    private List<Usuario> usuarios;
    private final AuthService authService;
    private final GestorVehiculos gestorVehiculos;

    public GestorUsuarios(AuthService authService, GestorVehiculos gestorVehiculos) {
        this.authService = authService;
        this.gestorVehiculos = gestorVehiculos;
        this.usuarios = new ArrayList<>();
        cargarUsuarios();
    }

    public void cargarUsuarios() {
        try {
            usuarios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS, Usuario.class);
            for (Usuario usuario : usuarios) {
                if (usuario instanceof UsuarioParqueo) {
                    UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
                    List<Vehiculo> vehiculos = gestorVehiculos.obtenerVehiculosPorUsuario(usuarioParqueo.getId());
                    usuarioParqueo.setVehiculos(vehiculos);
                }
                authService.registrarUsuario(usuario);
            }
            LOGGER.info("Usuarios cargados exitosamente. Total: " + usuarios.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar usuarios", e);
            usuarios = new ArrayList<>();
        }
    }

    public Usuario crearUsuario(String nombre, String apellidos, int telefono, String email, String direccion,
                                String idUsuario, String pin, Usuario.TipoUsuario tipoUsuario,
                                String numeroTarjeta, String fechaVencimiento, String codigoValidacion,
                                String terminalId, List<Vehiculo> vehiculos) {
        UsuarioBuilder builder;
        switch (tipoUsuario) {
            case ADMINISTRADOR:
                builder = new AdministradorBuilder();
                break;
            case INSPECTOR:
                builder = new InspectorBuilder();
                break;
            case USUARIO_PARQUEO:
                builder = new UsuarioParqueoBuilder();
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario no válido");
        }

        builder.conNombre(nombre)
               .conApellidos(apellidos)
               .conTelefono(telefono)
               .conEmail(email)
               .conDireccion(direccion)
               .conIdUsuario(idUsuario)
               .conPin(pin);

        Usuario nuevoUsuario;
        if (builder instanceof UsuarioParqueoBuilder) {
            ((UsuarioParqueoBuilder) builder)
                .conNumeroTarjeta(numeroTarjeta)
                .conFechaVencimientoTarjeta(fechaVencimiento)
                .conCodigoValidacionTarjeta(codigoValidacion);
            
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) builder.construir();
            for (Vehiculo vehiculo : vehiculos) {
                vehiculo.setPropietario(usuarioParqueo);
                gestorVehiculos.agregarVehiculo(vehiculo);
            }
            usuarioParqueo.setVehiculos(vehiculos);
            nuevoUsuario = usuarioParqueo;
        } else if (builder instanceof InspectorBuilder) {
            ((InspectorBuilder) builder).conTerminalId(terminalId);
            nuevoUsuario = builder.construir();
        } else {
            nuevoUsuario = builder.construir();
        }

        usuarios.add(nuevoUsuario);
        authService.registrarUsuario(nuevoUsuario);
        guardarUsuarios();
        LOGGER.info("Usuario creado: " + nuevoUsuario.getId());
        return nuevoUsuario;
    }

    public Usuario actualizarUsuario(String id, String nombre, String apellidos, int telefono, String email,
                                     String direccion, String idUsuario, String pin, Usuario.TipoUsuario tipoUsuario,
                                     String numeroTarjeta, String fechaVencimiento, String codigoValidacion,
                                     String terminalId, List<Vehiculo> vehiculos) {
        Usuario usuarioExistente = buscarUsuario(id);
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        int index = usuarios.indexOf(usuarioExistente);
        Usuario usuarioActualizado = crearUsuario(nombre, apellidos, telefono, email, direccion, idUsuario, pin,
                                                  tipoUsuario, numeroTarjeta, fechaVencimiento, codigoValidacion,
                                                  terminalId, vehiculos);
        
        usuarios.set(index, usuarioActualizado);
        guardarUsuarios();
        LOGGER.info("Usuario actualizado: " + id);
        return usuarioActualizado;
    }

    public void eliminarUsuario(String id) {
        Usuario usuario = buscarUsuario(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (usuario instanceof UsuarioParqueo) {
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
            for (Vehiculo vehiculo : usuarioParqueo.getVehiculos()) {
                gestorVehiculos.eliminarVehiculo(vehiculo.getId());
            }
        }
        
        boolean removido = usuarios.removeIf(u -> u.getId().equals(id));
        if (removido) {
            guardarUsuarios();
            LOGGER.info("Usuario eliminado: " + id);
        } else {
            LOGGER.warning("No se pudo eliminar el usuario: " + id);
        }
    }

    private void guardarUsuarios() {
        try {
            GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
            LOGGER.info("Usuarios guardados exitosamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar usuarios", e);
        }
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public Usuario buscarUsuario(String id) {
        return usuarios.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Usuario> buscarUsuariosPorTipo(Usuario.TipoUsuario tipo) {
        return usuarios.stream()
            .filter(u -> u.getTipoUsuario() == tipo)
            .collect(java.util.stream.Collectors.toList());
    }

    public void inicializarListaVacia() {
        this.usuarios = new ArrayList<>();
        LOGGER.info("Lista de usuarios inicializada vacía");
    }
}