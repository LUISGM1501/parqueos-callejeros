package com.parqueos.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.parqueos.builders.AdministradorBuilder;
import com.parqueos.builders.InspectorBuilder;
import com.parqueos.builders.UsuarioParqueoBuilder;
import com.parqueos.modelo.usuario.Administrador;
import com.parqueos.modelo.usuario.Inspector;
import com.parqueos.modelo.usuario.Usuario;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.modelo.vehiculo.Vehiculo;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar los usuarios
public class GestorUsuarios {
    // Archivos de usuarios por tipo
    private static final String ARCHIVO_ADMINISTRADORES = "administradores.json";
    private static final String ARCHIVO_INSPECTORES = "inspectores.json"; 
    private static final String ARCHIVO_USUARIOS_PARQUEO = "usuarios_parqueo.json";
    private static final Logger LOGGER = Logger.getLogger(GestorUsuarios.class.getName());
    
    private List<Administrador> administradores;
    private List<Inspector> inspectores;
    private List<UsuarioParqueo> usuariosParqueo;
    private final AuthService authService;
    private final GestorVehiculos gestorVehiculos;
    private final SistemaParqueo sistemaParqueo;

    // get para los json de usuarios
    public static String getArchivoAdministradores() {
        return ARCHIVO_ADMINISTRADORES;
    }

    public static String getArchivoInspectores() {
        return ARCHIVO_INSPECTORES;
    }

    public static String getArchivoUsuariosParqueo() {
        return ARCHIVO_USUARIOS_PARQUEO;
    }

    // Constructor para inicializar el gestor de usuarios
    public GestorUsuarios(AuthService authService, GestorVehiculos gestorVehiculos, SistemaParqueo sistemaParqueo) {
        this.authService = authService;
        this.gestorVehiculos = gestorVehiculos;
        this.sistemaParqueo = sistemaParqueo;
        this.administradores = new ArrayList<>();
        this.inspectores = new ArrayList<>();
        this.usuariosParqueo = new ArrayList<>();
        cargarUsuarios();
    }

    // Metodo para cargar los usuarios
    public void cargarUsuarios() {
        try {
            // Cargar cada tipo de usuario por separado
            administradores = GestorArchivos.cargarTodosLosElementos(ARCHIVO_ADMINISTRADORES, Administrador.class);
            inspectores = GestorArchivos.cargarTodosLosElementos(ARCHIVO_INSPECTORES, Inspector.class);
            usuariosParqueo = GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS_PARQUEO, UsuarioParqueo.class);

            if (administradores == null) administradores = new ArrayList<>();
            if (inspectores == null) inspectores = new ArrayList<>();
            if (usuariosParqueo == null) usuariosParqueo = new ArrayList<>();

            // Procesar usuarios parqueo
            for (UsuarioParqueo usuario : usuariosParqueo) {
                List<Vehiculo> vehiculos = gestorVehiculos.obtenerVehiculosPorUsuario(usuario.getId());
                usuario.setVehiculos(vehiculos);
                authService.registrarUsuario(usuario);
            }

            // Registrar administradores e inspectores
            administradores.forEach(authService::registrarUsuario);
            inspectores.forEach(authService::registrarUsuario);

            LOGGER.info("Usuarios cargados -> Administradores: " + administradores.size() + 
                       ", Inspectores: " + inspectores.size() + 
                       ", Usuarios Parqueo: " + usuariosParqueo.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar usuarios", e);
            inicializarListasVacias();
        }
    }

    // Metodo para crear un usuario
    public Usuario crearUsuario(String nombre, String apellidos, int telefono, String email, String direccion,
                              String idUsuario, String pin, Usuario.TipoUsuario tipoUsuario,
                              String numeroTarjeta, String fechaVencimiento, String codigoValidacion,
                              String terminalId, List<Vehiculo> vehiculos) {
        
        Usuario nuevoUsuario = null;
        
        try {
            switch (tipoUsuario) {
                case ADMINISTRADOR:
                    Administrador admin = new AdministradorBuilder()
                        .conNombre(nombre)
                        .conApellidos(apellidos)
                        .conTelefono(telefono)
                        .conEmail(email)
                        .conDireccion(direccion)
                        .conIdUsuario(idUsuario)
                        .conPin(pin)
                        .construir();
                    administradores.add(admin);
                    nuevoUsuario = admin;
                    break;
                    
                case INSPECTOR:
                    Inspector inspector = new InspectorBuilder()
                        .conNombre(nombre)
                        .conApellidos(apellidos)
                        .conTelefono(telefono)
                        .conEmail(email)
                        .conDireccion(direccion)
                        .conIdUsuario(idUsuario)
                        .conPin(pin)
                        .conTerminalId(terminalId)
                        .construir();
                    inspectores.add(inspector);
                    nuevoUsuario = inspector;
                    break;
                    
                case USUARIO_PARQUEO:
                    UsuarioParqueo usuarioParqueo = new UsuarioParqueoBuilder()
                        .conNombre(nombre)
                        .conApellidos(apellidos)
                        .conTelefono(telefono)
                        .conEmail(email)
                        .conDireccion(direccion)
                        .conIdUsuario(idUsuario)
                        .conPin(pin)
                        .conNumeroTarjeta(numeroTarjeta)
                        .conFechaVencimientoTarjeta(fechaVencimiento)
                        .conCodigoValidacionTarjeta(codigoValidacion)
                        .construir();
                        
                    if (vehiculos != null) {
                        for (Vehiculo vehiculo : vehiculos) {
                            vehiculo.setPropietario(usuarioParqueo);
                            gestorVehiculos.agregarVehiculo(vehiculo);
                        }
                        usuarioParqueo.setVehiculos(new ArrayList<>(vehiculos));
                    }
                    
                    usuariosParqueo.add(usuarioParqueo);
                    nuevoUsuario = usuarioParqueo;
                    break;
            }
            
            guardarUsuarios();
            authService.registrarUsuario(nuevoUsuario);
            LOGGER.info("Usuario creado: " + nuevoUsuario.getId());
            return nuevoUsuario;
            
        } catch (Exception e) {
            LOGGER.severe("Error al crear usuario: " + e.getMessage());
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    // Metodo para guardar los usuarios
    private void guardarUsuarios() {
        try {
            GestorArchivos.guardarTodo(administradores, ARCHIVO_ADMINISTRADORES);
            GestorArchivos.guardarTodo(inspectores, ARCHIVO_INSPECTORES);
            GestorArchivos.guardarTodo(usuariosParqueo, ARCHIVO_USUARIOS_PARQUEO);
            LOGGER.info("Usuarios guardados exitosamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar usuarios", e);
        }
    }

    // Metodo para eliminar un usuario
    public void eliminarUsuario(String id) {
        try {
            // Eliminar de cada lista según corresponda
            administradores.removeIf(a -> a.getId().equals(id));
            inspectores.removeIf(i -> i.getId().equals(id));
            
            // Manejo especial para usuarios parqueo
            usuariosParqueo.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .ifPresent(usuario -> {
                    // Eliminar vehículos asociados
                    usuario.getVehiculos().forEach(v -> 
                        gestorVehiculos.eliminarVehiculo(v.getId())
                    );
                });
            
            usuariosParqueo.removeIf(u -> u.getId().equals(id));
            
            guardarUsuarios();
            LOGGER.info("Usuario eliminado: " + id);
        } catch (Exception e) {
            LOGGER.severe("Error al eliminar usuario: " + e.getMessage());
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    // Metodo para actualizar un usuario
    public Usuario actualizarUsuario(String id, String nombre, String apellidos, int telefono, 
                               String email, String direccion, String idUsuario, String pin,
                               Usuario.TipoUsuario tipoUsuario, String numeroTarjeta,
                               String fechaVencimiento, String codigoValidacion,
                               String terminalId, List<Vehiculo> vehiculos) {
    
    try {
        // Buscar y remover el usuario existente de todas las listas
        Usuario usuarioExistente = null;
        
        // Buscar en administradores
        usuarioExistente = administradores.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);
        if (usuarioExistente != null) {
            administradores.removeIf(a -> a.getId().equals(id));
        }
        
        // Buscar en inspectores
        if (usuarioExistente == null) {
            usuarioExistente = inspectores.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
            if (usuarioExistente != null) {
                inspectores.removeIf(i -> i.getId().equals(id));
            }
        }
        
        // Buscar en usuarios parqueo
        if (usuarioExistente == null) {
            usuarioExistente = usuariosParqueo.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
            if (usuarioExistente != null) {
                usuariosParqueo.removeIf(u -> u.getId().equals(id));
            }
        }

        if (usuarioExistente == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }

        // Crear el usuario actualizado manteniendo el mismo ID
        Usuario usuarioActualizado;
        switch (tipoUsuario) {
            case ADMINISTRADOR:
                Administrador admin = new AdministradorBuilder()
                    .conNombre(nombre)
                    .conApellidos(apellidos)
                    .conTelefono(telefono)
                    .conEmail(email)
                    .conDireccion(direccion)
                    .conIdUsuario(idUsuario)
                    .conPin(pin)
                    .construir();
                admin.setId(id); // Mantener el mismo ID
                administradores.add(admin);
                usuarioActualizado = admin;
                break;

            case INSPECTOR:
                Inspector inspector = new InspectorBuilder()
                    .conNombre(nombre)
                    .conApellidos(apellidos)
                    .conTelefono(telefono)
                    .conEmail(email)
                    .conDireccion(direccion)
                    .conIdUsuario(idUsuario)
                    .conPin(pin)
                    .conTerminalId(terminalId)
                    .construir();
                inspector.setId(id); // Mantener el mismo ID
                inspectores.add(inspector);
                usuarioActualizado = inspector;
                break;

            case USUARIO_PARQUEO:
                UsuarioParqueo usuarioParqueo = new UsuarioParqueoBuilder()
                    .conNombre(nombre)
                    .conApellidos(apellidos)
                    .conTelefono(telefono)
                    .conEmail(email)
                    .conDireccion(direccion)
                    .conIdUsuario(idUsuario)
                    .conPin(pin)
                    .conNumeroTarjeta(numeroTarjeta)
                    .conFechaVencimientoTarjeta(fechaVencimiento)
                    .conCodigoValidacionTarjeta(codigoValidacion)
                    .construir();
                usuarioParqueo.setId(id); // Mantener el mismo ID

                // Actualizar vehículos
                if (vehiculos != null) {
                    // Eliminar los vehículos antiguos
                    List<Vehiculo> vehiculosAntiguos = gestorVehiculos.obtenerVehiculosPorUsuario(id);
                    for (Vehiculo vehiculo : vehiculosAntiguos) {
                        gestorVehiculos.eliminarVehiculo(vehiculo.getId());
                    }

                    // Agregar los nuevos vehículos
                    for (Vehiculo vehiculo : vehiculos) {
                        vehiculo.setPropietario(usuarioParqueo);
                        vehiculo.setPropietarioId(usuarioParqueo.getId());
                        gestorVehiculos.agregarVehiculo(vehiculo);
                    }
                    usuarioParqueo.setVehiculos(new ArrayList<>(vehiculos));
                }

                usuariosParqueo.add(usuarioParqueo);
                usuarioActualizado = usuarioParqueo;
                break;

            default:
                throw new IllegalArgumentException("Tipo de usuario no válido");
        }

        // Guardar los cambios en los archivos
        guardarUsuarios();

        // Actualizar el usuario en el servicio de autenticación
        authService.registrarUsuario(usuarioActualizado);

        // Enviar notificación de actualización
        sistemaParqueo.getGestorNotificaciones().enviarNotificacion(
            usuarioActualizado.getEmail(),
            "Actualización de datos",
            "Sus datos han sido actualizados exitosamente."
        );

        LOGGER.info("Usuario actualizado exitosamente: " + id);
        return usuarioActualizado;

    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error al actualizar usuario", e);
        throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
    }
}

    // Metodo para obtener todos los usuarios
    public List<Usuario> getUsuarios() {
        // Crear una lista para almacenar todos los usuarios
        List<Usuario> todosLosUsuarios = new ArrayList<>();
        // Agregar todos los administradores a la lista
        todosLosUsuarios.addAll(administradores);
        // Agregar todos los inspectores a la lista
        todosLosUsuarios.addAll(inspectores);
        // Agregar todos los usuarios parqueo a la lista
        todosLosUsuarios.addAll(usuariosParqueo);
        // Retornar la lista de todos los usuarios
        return todosLosUsuarios;
    }

    // Metodo para buscar un usuario por id
    public Usuario buscarUsuario(String id) {
        // Buscar el usuario por id
        return getUsuarios().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    // Metodo para buscar usuarios por tipo
    public List<Usuario> buscarUsuariosPorTipo(Usuario.TipoUsuario tipo) {
        // Buscar los usuarios por tipo
        switch (tipo) {
            case ADMINISTRADOR:
                return new ArrayList<>(administradores);
            case INSPECTOR:
                return new ArrayList<>(inspectores);
            case USUARIO_PARQUEO:
                return new ArrayList<>(usuariosParqueo);
            default:
                return new ArrayList<>();
        }
    }


    // Metodo para inicializar las listas vacías
    private void inicializarListasVacias() {
        this.administradores = new ArrayList<>();
        this.inspectores = new ArrayList<>();
        this.usuariosParqueo = new ArrayList<>();
        LOGGER.info("Listas de usuarios inicializadas vacías");
    }
}
