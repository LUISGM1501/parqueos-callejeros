package com.parqueos.modelo.usuario;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.parqueos.util.GestorArchivos;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    
    private String id; 
    protected String nombre;
    protected String apellidos;
    protected int telefono;
    protected String email;
    protected String direccion;
    protected String idUsuario;
    private String pin;
    protected LocalDate fechaIngreso;
    private final TipoUsuario tipoUsuario;

    // Enum para los tipos de usuarios
    public enum TipoUsuario {
        ADMINISTRADOR,
        INSPECTOR,
        USUARIO_PARQUEO
    }

    // Constructor de usuario
    public Usuario(String nombre, String apellidos, int telefono, String email, String direccion, String idUsuario, String pin, TipoUsuario tipoUsuario) {
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

    // Metodo para guardar un usuario
    public void guardar() {
        // Cargar todos los usuarios
        List<Usuario> usuarios = cargarTodos();

        // Agregar el usuario actual
        usuarios.add(this);

        // Guardar todos los usuarios
        guardarTodos(usuarios);
    }

    // Metodo para actualizar un usuario en el archivo
    public void actualizarEnArchivo() {
        // Cargar todos los usuarios
        List<Usuario> usuarios = cargarTodos();

        for (int i = 0; i < usuarios.size(); i++) {
            // Buscar el usuario actual
            if (usuarios.get(i).getId().equals(this.id)) {
                // Actualizar el usuario
                usuarios.set(i, this);
                break;
            }
        }

        // Guardar todos los usuarios
        guardarTodos(usuarios);
    }

    // Metodo para cargar un usuario por id
    public static Usuario cargar(String id) {
        // Cargar todos los usuarios
        List<Usuario> usuarios = cargarTodos();

        // Buscar el usuario por id
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Metodo para cargar todos los usuarios
    public static List<Usuario> cargarTodos() {
        // Cargar todos los usuarios del archivo json
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS, Usuario.class);
    }

    // Metodo para guardar todos los usuarios
    public static void guardarTodos(List<Usuario> usuarios) {
        // Guardar todos los usuarios en el archivo json
        GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
    }

    // Metodo para eliminar un usuario
    public void eliminar() {
        // Cargar todos los usuarios
        List<Usuario> usuarios = cargarTodos();

        // Eliminar el usuario actual
        usuarios.removeIf(u -> u.getId().equals(this.id));

        // Guardar todos los usuarios
        guardarTodos(usuarios);
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