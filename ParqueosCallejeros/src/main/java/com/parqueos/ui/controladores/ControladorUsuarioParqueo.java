package com.parqueos.ui.controladores;

import com.parqueos.ui.vistas.VistaUsuarioParqueo;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.modelo.usuario.UsuarioParqueo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorUsuarioParqueo extends ControladorBase {
    private VistaUsuarioParqueo vista;
    private SistemaParqueo sistemaParqueo;
    private UsuarioParqueo usuario;

    public ControladorUsuarioParqueo(VistaUsuarioParqueo vista, SistemaParqueo sistemaParqueo, UsuarioParqueo usuario) {
        this.vista = vista;
        this.sistemaParqueo = sistemaParqueo;
        this.usuario = usuario;
        inicializar();
    }

    @Override
    protected void inicializar() {
        vista.getBtnParquear().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parquear();
            }
        });

        vista.getBtnAgregarTiempo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarTiempo();
            }
        });

        vista.getBtnDesaparcar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desaparcar();
            }
        });

        vista.getBtnVerEspaciosDisponibles().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verEspaciosDisponibles();
            }
        });

        vista.getBtnVerHistorial().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verHistorial();
            }
        });

        vista.getBtnVerMultas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verMultas();
            }
        });

        actualizarTiempoGuardado();
    }

    private void parquear() {
        // Falta la logica para parquear
        System.out.println("Iniciando proceso de parqueo");
    }

    private void agregarTiempo() {
        // Falta la logica para agregar tiempo
        System.out.println("Agregando tiempo al parqueo actual");
    }

    private void desaparcar() {
        // Falta la logica para desaparcar
        System.out.println("Desaparcando vehículo");
    }

    private void verEspaciosDisponibles() {
        // Falta la logica para mostrar espacios disponibles
        System.out.println("Mostrando espacios disponibles");
    }

    private void verHistorial() {
        // Falta la logica para mostrar historial
        System.out.println("Mostrando historial de parqueos");
    }

    private void verMultas() {
        // Falta la logica para mostrar multas
        System.out.println("Mostrando multas del usuario");
    }

    private void actualizarTiempoGuardado() {
        vista.actualizarTiempoGuardado(usuario.getTiempoGuardado());
    }
}