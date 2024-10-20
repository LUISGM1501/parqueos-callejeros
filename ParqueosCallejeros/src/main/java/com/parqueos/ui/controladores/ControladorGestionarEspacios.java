package com.parqueos.ui.controladores;

import com.parqueos.modelo.parqueo.EspacioParqueo;
import com.parqueos.servicios.SistemaParqueo;
import com.parqueos.ui.vistas.VistaGestionarEspacios;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorGestionarEspacios {

    private static final Logger LOGGER = Logger.getLogger(ControladorGestionUsuarios.class.getName());

    private VistaGestionarEspacios vistaGestEspacios;
    private SistemaParqueo sistemaParqueo;
    private String token;

    public ControladorGestionarEspacios(VistaGestionarEspacios vistaGestEspacios, SistemaParqueo sistemaParqueo, String token) {
        this.vistaGestEspacios = vistaGestEspacios;
        this.sistemaParqueo = sistemaParqueo;
        this.token = token;
        inicializarControlador();
    }

    private void inicializarControlador() {
        cargarEspacios();
        vistaGestEspacios.getBtnAgregar().addActionListener(e -> agregarEspacios());
        vistaGestEspacios.getBtnEliminar().addActionListener(e -> eliminarEspacios());
        vistaGestEspacios.getBtnCancelar().addActionListener(e -> vistaGestEspacios.dispose());
    }

    //Cargar la tabla
    private void cargarEspacios() {
        // Obtener los espacios del sistema
        List<EspacioParqueo> espacios = sistemaParqueo.getGestorEspacios().obtenerEspaciosDisponibles();

        // Ordenar los espacios por su número
        espacios.sort((espacio1, espacio2) -> Integer.compare(Integer.parseInt(espacio1.getNumero()), Integer.parseInt(espacio2.getNumero())));

        // Crear el modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel();

        // Agregar las columnas
        modelo.addColumn("Número");
        modelo.addColumn("Ocupado");

        // Agregar los espacios al modelo
        for (EspacioParqueo espacio : espacios) {
            modelo.addRow(new Object[]{
                espacio.getNumero(),
                espacio.estaOcupado() ? "Sí" : "No"
            });
        }

        // Setear el modelo de la tabla
        vistaGestEspacios.getTblEspacios().setModel(modelo);

        // Log de la cantidad de espacios cargados
        LOGGER.info("Espacios cargados en la tabla: " + espacios.size());
    }


    //funcionalidad del botón agregar espacio
    // funcionalidad del botón agregar espacio
    private void agregarEspacios() {
        int e1, e2;
        String txtEspacio1, txtEspacio2;
        EspacioParqueo espacio;
        List<Integer> listaNums = new ArrayList<>();
        try {
            if (vistaGestEspacios.getRdbUnEspacio().isSelected()) {
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                if (!esNumeroValido(txtEspacio1)) {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El número de espacio debe tener entre 1 y 5 dígitos y ser solo numérico.");
                    return;
                }
                if (sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1) == null) {
                    espacio = new EspacioParqueo(txtEspacio1);
                    sistemaParqueo.getGestorEspacios().agregarEspacio(espacio);
                    cargarEspacios();
                    JOptionPane.showMessageDialog(vistaGestEspacios, "Espacio agregado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " ya existe.");
                }

            } else if (vistaGestEspacios.getRdbVariosEspacios().isSelected()) {
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                txtEspacio2 = vistaGestEspacios.getTxtLimiteEspacios().getText();

                // Validar ambos números
                if (!esNumeroValido(txtEspacio1) || !esNumeroValido(txtEspacio2)) {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "Los números de espacio deben tener entre 1 y 5 dígitos y ser solo numéricos.");
                    return;
                }

                // Convertir a enteros
                e1 = Integer.parseInt(txtEspacio1);
                e2 = Integer.parseInt(txtEspacio2);

                // Validar que el primer número sea menor o igual que el segundo
                if (e1 > e2) {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El número de inicio debe ser menor o igual que el número final.");
                    return;
                }

                // Longitud de los caracteres del espacio
                int longitud = txtEspacio1.length();
                String formato = "%0" + longitud + "d";

                for (int i = e1; i <= e2; i++) {
                    listaNums.add(i);
                }

                for (int num : listaNums) {
                    txtEspacio1 = String.format(formato, num);
                    if (sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1) == null) {
                        espacio = new EspacioParqueo(txtEspacio1);
                        sistemaParqueo.getGestorEspacios().agregarEspacio(espacio);
                    } else {
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " ya existe.");
                    }
                }
                cargarEspacios();
                JOptionPane.showMessageDialog(vistaGestEspacios, "Espacios agregados con éxito.");
            } else {
                JOptionPane.showMessageDialog(vistaGestEspacios, "Seleccione una opción antes de iniciar.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaGestEspacios, "Error al intentar añadir espacio");
        }
    }

// funcionalidad botón eliminar espacio
    private void eliminarEspacios() {
        int e1, e2;
        String txtEspacio1, txtEspacio2;
        EspacioParqueo espacio;
        List<Integer> listaNums = new ArrayList<>();
        try {
            if (vistaGestEspacios.getRdbUnEspacio().isSelected()) {
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                if (!esNumeroValido(txtEspacio1)) {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El número de espacio debe tener entre 1 y 5 dígitos y ser solo numérico.");
                    return;
                }
                espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1);

                if (espacio != null) {
                    if (!espacio.estaOcupado()) {
                        sistemaParqueo.getGestorEspacios().eliminarEspacio(txtEspacio1);
                        cargarEspacios();
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio fue eliminado con éxito.");
                    } else {
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " está ocupado.");
                    }
                } else {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " no existe.");
                }

            } else if (vistaGestEspacios.getRdbVariosEspacios().isSelected()) {
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                txtEspacio2 = vistaGestEspacios.getTxtLimiteEspacios().getText();

                // Validar ambos números
                if (!esNumeroValido(txtEspacio1) || !esNumeroValido(txtEspacio2)) {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "Los números de espacio deben tener entre 1 y 5 dígitos y ser solo numéricos.");
                    return;
                }

                // Convertir a enteros
                e1 = Integer.parseInt(txtEspacio1);
                e2 = Integer.parseInt(txtEspacio2);

                // Validar que el primer número sea menor o igual que el segundo
                if (e1 > e2) {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El número de inicio debe ser menor o igual que el número final.");
                    return;
                }

                // Longitud de los caracteres del espacio
                int longitud = txtEspacio1.length();
                String formato = "%0" + longitud + "d";

                for (int i = e1; i <= e2; i++) {
                    listaNums.add(i);
                }

                for (int num : listaNums) {
                    txtEspacio1 = String.format(formato, num);
                    espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1);
                    if (espacio != null) {
                        if (!espacio.estaOcupado()) {
                            sistemaParqueo.getGestorEspacios().eliminarEspacio(txtEspacio1);
                        } else {
                            JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " está ocupado.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " no existe.");
                    }
                }
                cargarEspacios();
                JOptionPane.showMessageDialog(vistaGestEspacios, "Espacios eliminados con éxito.");
            } else {
                JOptionPane.showMessageDialog(vistaGestEspacios, "Seleccione una opción antes de iniciar.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaGestEspacios, "Error al intentar eliminar espacio");
        }
    }

    //método de soporte validar solo numeros
    private boolean esNumeroValido(String numero) {
        // Verificar que el número no sea nulo, tenga entre 1 y 5 caracteres y contenga solo dígitos
        return numero != null && numero.length() >= 1 && numero.length() <= 5 && numero.matches("\\d+");
    }

}
