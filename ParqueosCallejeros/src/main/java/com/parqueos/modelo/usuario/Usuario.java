package com.parqueos.modelo.usuario;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import com.parqueos.util.GestorArchivos;
import java.util.UUID;

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

    public enum TipoUsuario {
        ADMINISTRADOR,
        INSPECTOR,
        USUARIO_PARQUEO
    }

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
    

    public boolean validarPin(String pin) {
        return this.pin.equals(pin);
    }

    public void cambiarPin(String pinActual, String nuevoPin) {
        if (validarPin(pinActual)) {
            this.pin = nuevoPin;
            actualizarEnArchivo();
        } else {
            throw new IllegalArgumentException("PIN actual incorrecto");
        }
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void guardar() {
        List<Usuario> usuarios = cargarTodos();
        usuarios.add(this);
        GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
    }

    public void actualizarEnArchivo() {
        List<Usuario> usuarios = cargarTodos();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(this.id)) {
                usuarios.set(i, this);
                break;
            }
        }
        GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
    }

    public static Usuario cargar(String id) {
        List<Usuario> usuarios = cargarTodos();
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<Usuario> cargarTodos() {
        return GestorArchivos.cargarTodosLosElementos(ARCHIVO_USUARIOS, Usuario.class);
    }

    public void eliminar() {
        List<Usuario> usuarios = cargarTodos();
        usuarios.removeIf(u -> u.getId().equals(this.id));
        GestorArchivos.guardarTodo(usuarios, ARCHIVO_USUARIOS);
    }

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