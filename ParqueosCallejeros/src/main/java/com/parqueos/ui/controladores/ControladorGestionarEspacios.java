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


    // Funcionalidad del botón agregar espacio
    private void agregarEspacios() {
        // Variables para almacenar los números de espacio
        int e1, e2;
        // Variables para almacenar los textos de los espacios
        String txtEspacio1, txtEspacio2;
        // Variable para almacenar el espacio
        EspacioParqueo espacio;
        // Lista para almacenar los números de los espacios
        List<Integer> listaNums = new ArrayList<>();
        try {
            // Verificar si se seleccionó un solo espacio
            if (vistaGestEspacios.getRdbUnEspacio().isSelected()) {
                // Obtener el número de espacio
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                // Validar el número de espacio
                if (!esNumeroValido(txtEspacio1)) {
                    // Mostrar el mensaje de error
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El número de espacio debe tener entre 1 y 5 dígitos y ser solo numérico.");
                    return;
                }
                // Buscar el espacio en el sistema
                if (sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1) == null) {
                    // Crear el espacio
                    espacio = new EspacioParqueo(txtEspacio1);
                    // Agregar el espacio al sistema
                    sistemaParqueo.getGestorEspacios().agregarEspacio(espacio);
                    // Cargar los espacios en la tabla
                    cargarEspacios();
                    // Mostrar el mensaje de éxito
                    JOptionPane.showMessageDialog(vistaGestEspacios, "Espacio agregado con éxito.");
                } else {
                    // Mostrar el mensaje de error
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " ya existe.");
                }

            // Verificar si se seleccionó varios espacios
            } else if (vistaGestEspacios.getRdbVariosEspacios().isSelected()) {
                // Obtener el número de espacio
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                // Obtener el límite de espacios
                txtEspacio2 = vistaGestEspacios.getTxtLimiteEspacios().getText();

                // Validar ambos números
                if (!esNumeroValido(txtEspacio1) || !esNumeroValido(txtEspacio2)) {
                    // Mostrar el mensaje de error
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

                // Iterar sobre el rango de números
                for (int i = e1; i <= e2; i++) {
                    listaNums.add(i);
                }
                
                // Iterar sobre la lista de números
                for (int num : listaNums) {
                    // Formatear el número
                    txtEspacio1 = String.format(formato, num);
                    // Buscar el espacio en el sistema
                    if (sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1) == null) {
                        // Crear el espacio
                        espacio = new EspacioParqueo(txtEspacio1);
                        // Agregar el espacio al sistema
                        sistemaParqueo.getGestorEspacios().agregarEspacio(espacio);
                    } else {
                        // Mostrar el mensaje de error
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " ya existe.");
                    }
                }
                // Cargar los espacios en la tabla
                cargarEspacios();
                // Mostrar el mensaje de éxito
                JOptionPane.showMessageDialog(vistaGestEspacios, "Espacios agregados con éxito.");
            } else {
                // Mostrar el mensaje de error
                JOptionPane.showMessageDialog(vistaGestEspacios, "Seleccione una opción antes de iniciar.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaGestEspacios, "Error al intentar añadir espacio");
        }
    }

    // Funcionalidad botón eliminar espacio
    private void eliminarEspacios() {
        // Variables para almacenar los números de espacio
        int e1, e2;
        String txtEspacio1, txtEspacio2;
        EspacioParqueo espacio;
        // Lista para almacenar los números de los espacios
        List<Integer> listaNums = new ArrayList<>();
        try {
            // Verificar si se seleccionó un solo espacio
            if (vistaGestEspacios.getRdbUnEspacio().isSelected()) {
                // Obtener el número de espacio
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                // Validar el número de espacio
                if (!esNumeroValido(txtEspacio1)) {
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El número de espacio debe tener entre 1 y 5 dígitos y ser solo numérico.");
                    return;
                }
                // Buscar el espacio en el sistema
                espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1);
                // Verificar si el espacio existe
                if (espacio != null) {
                    // Verificar si el espacio está ocupado
                    if (!espacio.estaOcupado()) {
                        // Eliminar el espacio del sistema
                        sistemaParqueo.getGestorEspacios().eliminarEspacio(txtEspacio1);
                        // Cargar los espacios en la tabla
                        cargarEspacios();
                        // Mostrar el mensaje de éxito
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio fue eliminado con éxito.");
                    } else {
                        // Mostrar el mensaje de error
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " está ocupado.");
                    }
                } else {
                    // Mostrar el mensaje de error
                    JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " no existe.");
                }

            // Verificar si se seleccionó varios espacios
            } else if (vistaGestEspacios.getRdbVariosEspacios().isSelected()) {
                // Obtener el número de espacio
                txtEspacio1 = vistaGestEspacios.getTxtNumeroParqueo().getText();
                // Obtener el límite de espacios
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

                // Iterar sobre el rango de números
                for (int i = e1; i <= e2; i++) {
                    listaNums.add(i);
                }

                // Iterar sobre la lista de números
                for (int num : listaNums) {
                    // Formatear el número
                    txtEspacio1 = String.format(formato, num);
                    espacio = sistemaParqueo.getGestorEspacios().buscarEspacio(txtEspacio1);
                    // Verificar si el espacio existe
                    if (espacio != null) {
                        // Verificar si el espacio está ocupado
                        if (!espacio.estaOcupado()) {
                            // Eliminar el espacio del sistema
                            sistemaParqueo.getGestorEspacios().eliminarEspacio(txtEspacio1);
                        } else {
                            // Mostrar el mensaje de error
                            JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " está ocupado.");
                        }
                    } else {
                        // Mostrar el mensaje de error
                        JOptionPane.showMessageDialog(vistaGestEspacios, "El espacio " + txtEspacio1 + " no existe.");
                    }
                }
                // Cargar los espacios en la tabla
                cargarEspacios();
                // Mostrar el mensaje de éxito
                JOptionPane.showMessageDialog(vistaGestEspacios, "Espacios eliminados con éxito.");
            } else {
                // Mostrar el mensaje de error
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
