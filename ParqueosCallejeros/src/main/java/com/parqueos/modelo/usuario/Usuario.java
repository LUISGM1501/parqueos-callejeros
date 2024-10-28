package com.parqueos.modelo.usuario;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parqueos.servicios.GestorUsuarios;
import com.parqueos.util.GestorArchivos;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
        
    @JsonProperty("id")
    private String id; 
    @JsonProperty("nombre")
    protected String nombre;
    @JsonProperty("apellidos")
    protected String apellidos;
    @JsonProperty("telefono")
    protected int telefono;
    @JsonProperty("email")
    protected String email;
    @JsonProperty("direccion")
    protected String direccion;
    @JsonProperty("idUsuario")
    protected String idUsuario;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("fechaIngreso")
    protected LocalDate fechaIngreso;
    @JsonProperty("tipoUsuario")
    private final TipoUsuario tipoUsuario;

    // Enum para los tipos de usuarios
    public enum TipoUsuario {
        ADMINISTRADOR,
        INSPECTOR,
        USUARIO_PARQUEO
    }

    @JsonCreator
    public Usuario() {
        this("", "", 0, "", "", "", "", TipoUsuario.USUARIO_PARQUEO);
    }

    // Constructor de usuario
    public Usuario(String nombre, String apellidos, int telefono, String email, 
                  String direccion, String idUsuario, String pin, TipoUsuario tipoUsuario) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.idUsuario = idUsuario;
        this.pin = pin;
        this.fechaIngreso = LocalDate.now();
        this.tipoUsuario = tipoUsuario;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPin() {
        return pin;
    }   

    public void setPin(String pin) {
        this.pin = pin;
    }
    
    // Metodo para validar el pin
    public boolean validarPin(String pin) {
        return this.pin.equals(pin);
    }

    // Metodo para cambiar el pin
    public void cambiarPin(String pinActual, String nuevoPin) {
        // Validar el pin actual
        if (validarPin(pinActual)) {
            // Cambiar el pin
            this.pin = nuevoPin;

            // Actualizar el usuario en el archivo
            actualizarEnArchivo();
        } else {
            // Lanzar una excepcion si el pin actual es incorrecto
            throw new IllegalArgumentException("PIN actual incorrecto");
        }
    }

    // Metodo para obtener el tipo de usuario
    public TipoUsuario getTipoUsuario() {
        // Retornar el tipo de usuario que puede ser administrador, inspector o usuario parqueo
        // Segun los enum de la clase
        return tipoUsuario;
    }

    // Metodo para obtener el archivo correspondiente según el tipo de usuario
    private String obtenerArchivoSegunTipo() {
        return switch (tipoUsuario) {
            case ADMINISTRADOR -> GestorUsuarios.getArchivoAdministradores();
            case INSPECTOR -> GestorUsuarios.getArchivoInspectores();
            case USUARIO_PARQUEO -> GestorUsuarios.getArchivoUsuariosParqueo();
        };
    }

    // Metodo para guardar un usuario
    public void guardar() {
        // Obtener la lista de usuarios según el tipo
        List<?> usuarios = switch (tipoUsuario) {
            // Cargar todos los administradores
            case ADMINISTRADOR -> GestorArchivos.cargarTodosLosElementos(
                GestorUsuarios.getArchivoAdministradores(), 
                Administrador.class
            );
            // Cargar todos los inspectores
            case INSPECTOR -> GestorArchivos.cargarTodosLosElementos(
                GestorUsuarios.getArchivoInspectores(), 
                Inspector.class
            );
            // Cargar todos los usuarios parqueo
            case USUARIO_PARQUEO -> GestorArchivos.cargarTodosLosElementos(
                GestorUsuarios.getArchivoUsuariosParqueo(), 
                UsuarioParqueo.class
            );
        };

        // Crear una lista de usuarios
        List<Usuario> listaUsuarios = new ArrayList<>();
        if (usuarios != null) {
            // Agregar todos los usuarios a la lista
            listaUsuarios.addAll((Collection<? extends Usuario>) usuarios);
        }

        // Agregar el usuario actual
        listaUsuarios.add(this);

        // Guardar en el archivo correspondiente
        GestorArchivos.guardarTodo(listaUsuarios, obtenerArchivoSegunTipo());
    }

    // Metodo para actualizar un usuario en el archivo
    public void actualizarEnArchivo() {
        try {
            // Obtener la lista de usuarios según el tipo
            List<?> usuarios = switch (tipoUsuario) {
                // Cargar todos los administradores
                case ADMINISTRADOR -> GestorArchivos.cargarTodosLosElementos(
                    GestorUsuarios.getArchivoAdministradores(), 
                    Administrador.class
                );
                // Cargar todos los inspectores
                case INSPECTOR -> GestorArchivos.cargarTodosLosElementos(
                    GestorUsuarios.getArchivoInspectores(), 
                    Inspector.class
                );
                // Cargar todos los usuarios parqueo
                case USUARIO_PARQUEO -> GestorArchivos.cargarTodosLosElementos(
                    GestorUsuarios.getArchivoUsuariosParqueo(), 
                    UsuarioParqueo.class
                );
            };
            
            // Crear una lista de usuarios
            List<Usuario> listaUsuarios = new ArrayList<>();
            if (usuarios != null) {
                // Agregar todos los usuarios a la lista
                listaUsuarios.addAll((Collection<? extends Usuario>) usuarios);
            }
            
            // Eliminar el usuario existente
            listaUsuarios.removeIf(u -> u.getId().equals(this.id));
            
            // Agregar el usuario actualizado
            listaUsuarios.add(this);
            
            // Guardar en el archivo correspondiente
            GestorArchivos.guardarTodo(listaUsuarios, obtenerArchivoSegunTipo());
            
        } catch (Exception e) {
            // Lanzar una excepcion si ocurre un error al actualizar el usuario en el archivo
            throw new RuntimeException("Error al actualizar el usuario en el archivo", e);
        }
    }

    // Metodo para eliminar un usuario
    public void eliminar() {
        // Obtener la lista de usuarios según el tipo
        List<?> usuarios = switch (tipoUsuario) {
            // Cargar todos los administradores
            case ADMINISTRADOR -> GestorArchivos.cargarTodosLosElementos(
                GestorUsuarios.getArchivoAdministradores(), 
                Administrador.class
            );
            // Cargar todos los inspectores
            case INSPECTOR -> GestorArchivos.cargarTodosLosElementos(
                GestorUsuarios.getArchivoInspectores(), 
                Inspector.class
            );
            // Cargar todos los usuarios parqueo
            case USUARIO_PARQUEO -> GestorArchivos.cargarTodosLosElementos(
                GestorUsuarios.getArchivoUsuariosParqueo(), 
                UsuarioParqueo.class
            );
        };

        // Crear una lista de usuarios
        List<Usuario> listaUsuarios = new ArrayList<>();
        if (usuarios != null) {
            // Agregar todos los usuarios a la lista
            listaUsuarios.addAll((Collection<? extends Usuario>) usuarios);
        }

        // Eliminar el usuario actual
        listaUsuarios.removeIf(u -> u.getId().equals(this.id));

        // Guardar en el archivo correspondiente
        GestorArchivos.guardarTodo(listaUsuarios, obtenerArchivoSegunTipo());
    }

    // Metodo para cargar un usuario por id
    public static Usuario cargar(String id) {
        // Primero intentar cargar de administradores
        List<Administrador> administradores = GestorArchivos.cargarTodosLosElementos(
            GestorUsuarios.getArchivoAdministradores(), 
            Administrador.class
        );
        if (administradores != null) {
            // Buscar el usuario en la lista
            for (Administrador admin : administradores) {
                // Retornar el usuario si se encuentra
                if (admin.getId().equals(id)) return admin;
            }
        }

        // Si no se encuentra, intentar cargar de inspectores
        List<Inspector> inspectores = GestorArchivos.cargarTodosLosElementos(
            GestorUsuarios.getArchivoInspectores(), 
            Inspector.class
        );
        if (inspectores != null) {
            // Buscar el usuario en la lista
            for (Inspector inspector : inspectores) {
                // Retornar el usuario si se encuentra
                if (inspector.getId().equals(id)) return inspector;
            }
        }

        // Si no se encuentra, intentar cargar de usuarios parqueo
        List<UsuarioParqueo> usuariosParqueo = GestorArchivos.cargarTodosLosElementos(
            GestorUsuarios.getArchivoUsuariosParqueo(), 
            UsuarioParqueo.class
        );
        if (usuariosParqueo != null) {
            // Buscar el usuario en la lista
            for (UsuarioParqueo usuarioParqueo : usuariosParqueo) {
                // Retornar el usuario si se encuentra
                if (usuarioParqueo.getId().equals(id)) return usuarioParqueo;
            }
        }

        return null;
    }

    // Metodo para cargar todos los usuarios
    public static List<Usuario> cargarTodos() {
        List<Usuario> todosLosUsuarios = new ArrayList<>();
        
        // Cargar administradores
        List<Administrador> administradores = GestorArchivos.cargarTodosLosElementos(
            GestorUsuarios.getArchivoAdministradores(), 
            Administrador.class
        );
        if (administradores != null) {
            // Agregar todos los usuarios a la lista
            todosLosUsuarios.addAll(administradores);
        }

        // Cargar inspectores
        List<Inspector> inspectores = GestorArchivos.cargarTodosLosElementos(
            GestorUsuarios.getArchivoInspectores(), 
            Inspector.class
        );
        if (inspectores != null) {
            // Agregar todos los usuarios a la lista
            todosLosUsuarios.addAll(inspectores);
        }

        // Cargar usuarios parqueo
        List<UsuarioParqueo> usuariosParqueo = GestorArchivos.cargarTodosLosElementos(
            GestorUsuarios.getArchivoUsuariosParqueo(), 
            UsuarioParqueo.class
        );
        if (usuariosParqueo != null) {
            // Agregar todos los usuarios a la lista
            todosLosUsuarios.addAll(usuariosParqueo);
        }

        return todosLosUsuarios;
    }

    // Metodo para convertir a string
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", telefono=" + telefono +
                ", email='" + email + '\'' +
                ", direccion='" + direccion + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                ", tipoUsuario=" + tipoUsuario +
                '}';
    }
}