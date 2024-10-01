package com.parqueos.ui.vistas;

import java.awt.GridLayout;

import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;
import com.parqueos.util.GestorArchivos;

public class VistaAdministrador extends VistaBase {

    // Agregar el boton para crear un nuevo usuario parqueo
    //private BotonCrearUsarioParqueo btnCrearUsuarioParqueo;

    private BotonPersonalizado btnConfigurarParqueo;
    private BotonPersonalizado btnGestionarUsuarios;
    private BotonPersonalizado btnGestionarEspacios;
    private BotonPersonalizado btnGenerarReporteIngresos;
    private BotonPersonalizado btnGenerarReporteMultas;
    private BotonPersonalizado btnGenerarReporteEspacios;
    private BotonPersonalizado btnGenerarReporteHistorial;
    private BotonPersonalizado btnGenerarReporteEstadisticas;

    public VistaAdministrador() {
        super("Panel de Administrador");
        inicializarComponentes();
    }

    @Override
    public void inicializarComponentes() {
        PanelPersonalizado panel = new PanelPersonalizado();
        setContentPane(panel);
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        btnConfigurarParqueo = new BotonPersonalizado("Configurar Parqueo");
        btnGestionarUsuarios = new BotonPersonalizado("Gestionar Usuarios");
        btnGestionarEspacios = new BotonPersonalizado("Gestionar Espacios");
        btnGenerarReporteIngresos = new BotonPersonalizado("Reporte de Ingresos");
        btnGenerarReporteMultas = new BotonPersonalizado("Reporte de Multas");
        btnGenerarReporteEspacios = new BotonPersonalizado("Reporte de Espacios");
        btnGenerarReporteHistorial = new BotonPersonalizado("Reporte de Historial");
        btnGenerarReporteEstadisticas = new BotonPersonalizado("Reporte de Estadísticas");

        // Agregar el boton para crear un nuevo usuario parqueo
        //btnCrearUsuarioParqueo = new BotonPersonalizado("Crear Usuario Parqueo");

        panel.add(btnConfigurarParqueo);
        panel.add(btnGestionarUsuarios);
        panel.add(btnGestionarEspacios);
        panel.add(btnGenerarReporteIngresos);
        panel.add(btnGenerarReporteMultas);
        panel.add(btnGenerarReporteEspacios);
        panel.add(btnGenerarReporteHistorial);
        panel.add(btnGenerarReporteEstadisticas);

        // Agregar el boton para crear un nuevo usuario parqueo
        //panel.add(btnCrearUsuarioParqueo);

        pack();
        setLocationRelativeTo(null);
    }

    // Getters para los botones
    public BotonPersonalizado getBtnConfigurarParqueo() { return btnConfigurarParqueo; }
    public BotonPersonalizado getBtnGestionarUsuarios() { return btnGestionarUsuarios; }
    public BotonPersonalizado getBtnGestionarEspacios() { return btnGestionarEspacios; }
    public BotonPersonalizado getBtnGenerarReporteIngresos() { return btnGenerarReporteIngresos; }
    public BotonPersonalizado getBtnGenerarReporteMultas() { return btnGenerarReporteMultas; }
    public BotonPersonalizado getBtnGenerarReporteEspacios() { return btnGenerarReporteEspacios; }
    public BotonPersonalizado getBtnGenerarReporteHistorial() { return btnGenerarReporteHistorial; }
    public BotonPersonalizado getBtnGenerarReporteEstadisticas() { return btnGenerarReporteEstadisticas; }
}