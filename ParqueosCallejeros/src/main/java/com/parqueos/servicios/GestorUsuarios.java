package com.parqueos.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.parqueos.builders.AdministradorBuilder;
import com.parqueos.builders.InspectorBuilder;
import com.parqueos.builders.UsuarioBuilder;
import com.parqueos.builders.UsuarioParqueoBuilder;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar los usuarios
public class GestorUsuarios {
    // Archivo de usuarios
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static final Logger LOGGER = Logger.getLogger(GestorUsuarios.class.getName());
    
    private List<Usuario> usuarios;
    private final AuthService authService;
    private final GestorVehiculos gestorVehiculos;

    // Constructor para inicializar el gestor de usuarios
    public GestorUsuarios(AuthService authService, GestorVehiculos gestorVehiculos) {
        this.authService = authService;
        this.gestorVehiculos = gestorVehiculos;
        this.usuarios = new ArrayList<>();
        cargarUsuarios();
    }

    // Metodo para cargar los usuarios
    public void cargarUsuarios() {
        try {
            // Cargar los usuarios del archivo json
            usuarios = GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS, Usuario.class);

            // Recorrer la lista de usuarios
            for (Usuario usuario : usuarios) {
                // Si el usuario es un usuario parqueo, obtener los vehiculos del usuario parqueo
                if (usuario instanceof UsuarioParqueo) {
                    // Obtener los vehiculos del usuario parqueo
                    UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
                    List<Vehiculo> vehiculos = gestorVehiculos.obtenerVehiculosPorUsuario(usuarioParqueo.getId());
                    // Asignar los vehiculos al usuario parqueo
                    usuarioParqueo.setVehiculos(vehiculos);
                }
                // Registrar el usuario en el servicio de autenticacion
                authService.registrarUsuario(usuario);
            }
            // Mensaje de confirmacion
            LOGGER.info("Usuarios cargados exitosamente. Total: " + usuarios.size());
        } catch (Exception e) {
            // Mensaje de error
            LOGGER.log(Level.SEVERE, "Error al cargar usuarios", e);
            // Inicializar la lista de usuarios
            inicializarListaVacia();
        }
    }

    // Metodo para crear un usuario
    public Usuario crearUsuario(String nombre, String apellidos, int telefono, String email, String direccion,
                                String idUsuario, String pin, Usuario.TipoUsuario tipoUsuario,
                                String numeroTarjeta, String fechaVencimiento, String codigoValidacion,
                                String terminalId, List<Vehiculo> vehiculos) {
        // Crear un builder para crear el usuario
        UsuarioBuilder builder;
        // Switch para crear el usuario segun el tipo de usuario
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

        // Asignar los atributos del usuario
        builder.conNombre(nombre)
               .conApellidos(apellidos)
               .conTelefono(telefono)
               .conEmail(email)
               .conDireccion(direccion)
               .conIdUsuario(idUsuario)
               .conPin(pin);

        Usuario nuevoUsuario;
        // Si el usuario es un usuario parqueo, asignar los atributos del usuario parqueo
        if (builder instanceof UsuarioParqueoBuilder) {
            ((UsuarioParqueoBuilder) builder)
                .conNumeroTarjeta(numeroTarjeta)
                .conFechaVencimientoTarjeta(fechaVencimiento)
                .conCodigoValidacionTarjeta(codigoValidacion);
            
            // Construir el usuario parqueo
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) builder.construir();

            // Recorrer la lista de vehiculos
            for (Vehiculo vehiculo : vehiculos) {
                // Asignar el propietario del vehiculo
                vehiculo.setPropietario(usuarioParqueo.getId());
                // Agregar el vehiculo al gestor de vehiculos
                gestorVehiculos.agregarVehiculo(vehiculo);
            }
            // Asignar los vehiculos al usuario parqueo
            usuarioParqueo.setVehiculos(vehiculos);
            // Asignar el nuevo usuario parqueo
            nuevoUsuario = usuarioParqueo;

        // Si el usuario es un inspector, asignar el terminal id
        } else if (builder instanceof InspectorBuilder) {
            ((InspectorBuilder) builder).conTerminalId(terminalId);
            nuevoUsuario = builder.construir();
            
        // Si el usuario es un administrador, no asignar terminal id
        } else {
            nuevoUsuario = builder.construir();
        }

        // Agregar el nuevo usuario a la lista de usuarios
        usuarios.add(nuevoUsuario);
        // Registrar el usuario en el servicio de autenticacion
        authService.registrarUsuario(nuevoUsuario);
        // Guardar los usuarios
        guardarUsuarios();
        // Mensaje de confirmacion
        LOGGER.info("Usuario creado: " + nuevoUsuario.getId());
        // Retornar el nuevo usuario
        return nuevoUsuario;
    }

    // Metodo para actualizar un usuario
    // Metodo para actualizar un usuario
    public Usuario actualizarUsuario(String id, String nombre, String apellidos, int telefono, String email,
                                     String direccion, String idUsuario, String pin, Usuario.TipoUsuario tipoUsuario,
                                     String numeroTarjeta, String fechaVencimiento, String codigoValidacion,
                                     String terminalId, List<Vehiculo> vehiculos) {
        // Buscar el usuario existente
        Usuario usuarioExistente = buscarUsuario(id);

        // Si el usuario no existe, lanzar una excepcion
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Actualizar los datos del usuario existente
        usuarioExistente.setNombre(nombre);
        usuarioExistente.setApellidos(apellidos);
        usuarioExistente.setTelefono(telefono);
        usuarioExistente.setEmail(email);
        usuarioExistente.setDireccion(direccion);
        usuarioExistente.setIdUsuario(idUsuario);
        usuarioExistente.setPin(pin);

        // Si es un usuario parqueo, actualizamos los detalles adicionales
        if (usuarioExistente instanceof UsuarioParqueo) {
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuarioExistente;
            usuarioParqueo.setNumeroTarjeta(numeroTarjeta);
            usuarioParqueo.setFechaVencimientoTarjeta(fechaVencimiento);
            usuarioParqueo.setCodigoValidacionTarjeta(codigoValidacion);

            // Actualizar los vehiculos
            usuarioParqueo.setVehiculos(vehiculos);
        }

        // Si es un inspector, actualizamos el terminal ID
        if (usuarioExistente instanceof Inspector) {
            Inspector usuarioParqueo = (Inspector) usuarioExistente;
            usuarioParqueo.setTerminalId(terminalId);
        }

        // Guardar los usuarios en el archivo
        guardarUsuarios();

        // Mensaje de confirmacion
        LOGGER.info("Usuario actualizado: " + id);

        // Retornar el usuario actualizado
        return usuarioExistente;
    }

    
    // Metodo para eliminar un usuario
    public void eliminarUsuario(String id) {
        // Buscar el usuario existente
        Usuario usuario = buscarUsuario(id);

        // Si el usuario no existe, lanzar una excepcion
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Si el usuario es un usuario parqueo, eliminar los vehiculos del usuario parqueo
        if (usuario instanceof UsuarioParqueo) {
            UsuarioParqueo usuarioParqueo = (UsuarioParqueo) usuario;
            // Recorrer la lista de vehiculos
            for (Vehiculo vehiculo : usuarioParqueo.getVehiculos()) {
                // Eliminar el vehiculo del gestor de vehiculos
                gestorVehiculos.eliminarVehiculo(vehiculo.getId());
            }
        }
        
        // Eliminar el usuario de la lista de usuarios
        boolean removido = usuarios.removeIf(u -> u.getId().equals(id));

        // Si se elimino el usuario, guardar los usuarios
        if (removido) {
            guardarUsuarios();
            LOGGER.info("Usuario eliminado: " + id);
        } else {
            // Mensaje de error
            LOGGER.warning("No se pudo eliminar el usuario: " + id);
        }
    }

    // Metodo para guardar los usuarios
    private void guardarUsuarios() {
        try {
            // Guardar los usuarios en el archivo json
            GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
            LOGGER.info("Usuarios guardados exitosamente");
        } catch (Exception e) {
            // Mensaje de error
            LOGGER.log(Level.SEVERE, "Error al guardar usuarios", e);
        }
    }

    // Metodo para obtener los usuarios
    public List<Usuario> getUsuarios() {
        // Retornar una copia de la lista de usuarios
        return new ArrayList<>(usuarios);
    }

    // Metodo para buscar un usuario por id
    public Usuario buscarUsuario(String id) {
        // Buscar el usuario en la lista de usuarios
        return usuarios.stream()
            // Filtrar el usuario por id
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    // Metodo para buscar usuarios por tipo
    public List<Usuario> buscarUsuariosPorTipo(Usuario.TipoUsuario tipo) {
        // Retornar una lista de usuarios filtrados por tipo
        return usuarios.stream()
            // Filtrar el usuario por tipo
            .filter(u -> u.getTipoUsuario() == tipo)
            .collect(java.util.stream.Collectors.toList());
    }

    // Metodo para inicializar la lista de usuarios
    public void inicializarListaVacia() {
        // Se usa para inicializar la lista de usuarios en caso de que no se pueda cargar del archivo json
        this.usuarios = new ArrayList<>();
        LOGGER.info("Lista de usuarios inicializada vacía");
    }
}