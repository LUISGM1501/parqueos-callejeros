package com.parqueos.servicios;

import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.builders.AdministradorBuilder;
import com.parqueos.builders.InspectorBuilder;
import com.parqueos.builders.UsuarioBuilder;
import com.parqueos.builders.UsuarioParqueoBuilder;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

public class GestorUsuarios {
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private List<Usuario> usuarios;
    private final AuthService authService;

    public GestorUsuarios(AuthService authService) {
        this.authService = authService;
        cargarUsuarios();
    }

    public void cargarUsuarios() {
        usuarios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS, Usuario.class);
        for (Usuario usuario : usuarios) {
            authService.registrarUsuario(usuario);
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
                usuarioParqueo.agregarVehiculo(vehiculo);
            }
            nuevoUsuario = usuarioParqueo;
        } else if (builder instanceof InspectorBuilder) {
            ((InspectorBuilder) builder).conTerminalId(terminalId);
            nuevoUsuario = builder.construir();
        } else {
            nuevoUsuario = builder.construir();
        }

        registrarUsuario(nuevoUsuario);
        return nuevoUsuario;
    }

    public void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
        authService.registrarUsuario(usuario);
        guardarUsuarios();
    }

    public Usuario actualizarUsuario(String id, String nombre, String apellidos, int telefono, String email,
                                     String direccion, String idUsuario, String pin, Usuario.TipoUsuario tipoUsuario,
                                     String numeroTarjeta, String fechaVencimiento, String codigoValidacion,
                                     String terminalId, List<Vehiculo> vehiculos) {
        Usuario usuarioExistente = buscarUsuario(id);
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuarioActualizado = crearUsuario(nombre, apellidos, telefono, email, direccion, idUsuario, pin,
                                                  tipoUsuario, numeroTarjeta, fechaVencimiento, codigoValidacion,
                                                  terminalId, vehiculos);
        
        int index = usuarios.indexOf(usuarioExistente);
        usuarios.set(index, usuarioActualizado);
        guardarUsuarios();
        return usuarioActualizado;
    }

    public void eliminarUsuario(String id) {
        boolean removido = usuarios.removeIf(u -> u.getId().equals(id));
        if (removido) {
            guardarUsuarios();
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
    }

    private void guardarUsuarios() {
        GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
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
            .collect(Collectors.toList());
    }
}