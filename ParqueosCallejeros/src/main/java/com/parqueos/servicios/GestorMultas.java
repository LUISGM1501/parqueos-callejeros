package com.parqueos.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.parqueos.modelo.multa.Multa;
import com.parqueos.modelo.usuario.UsuarioParqueo;
import com.parqueos.util.GestorArchivos;

// Clase para gestionar las multas
public class GestorMultas {
    // Archivo donde se guardan las multas
    private static final String ARCHIVO_MULTAS = "multas.json";
    private List<Multa> multas;

    // Metodo para cargar las multas
    public void cargarMultas() {
        multas = GestorArchivos.cargarTodosLosElementos(ARCHIVO_MULTAS, Multa.class);
    }

    // Metodo para agregar una multa
    public void agregarMulta(Multa multa) {
        // Agregar la multa a la lista
        multas.add(multa);
        // Guardar las multas
        guardarMultas();
    }

    // Metodo para actualizar una multa
    public void actualizarMulta(Multa multa) {
        // Obtener el indice de la multa
        int index = multas.indexOf(multa);

        // Validar si el indice no es -1
        if (index != -1) {
            // Actualizar la multa
            multas.set(index, multa);
            // Guardar las multas
            guardarMultas();
        }
    }

    // Metodo para eliminar una multa
    public void eliminarMulta(String idMulta) {
        // Eliminar la multa de la lista
        multas.removeIf(m -> m.getIdMulta().equals(idMulta));
        // Guardar las multas
        guardarMultas();
    }

    // Metodo para obtener las multas de un usuario
    public List<Multa> obtenerMultasUsuario(UsuarioParqueo usuario) {
        return multas.stream()
                     // Filtrar las multas que tienen el vehiculo igual al usuario
                     .filter(m -> m.getVehiculo().getPropietario().equals(usuario))
                     // Ordenar las multas por la fecha y hora de la multa
                     .sorted((m1, m2) -> m2.getFechaHora().compareTo(m1.getFechaHora()))
                     // Convertir las multas a una lista
                     .collect(Collectors.toList());
    }

    // Metodo para buscar una multa por su id
    public Multa buscarMulta(String idMulta) {
        return multas.stream()
                     // Filtrar la multa que tiene el id igual al id de la multa
                     .filter(m -> m.getIdMulta().equals(idMulta))
                     .findFirst()
                     .orElse(null);
    }

    // Metodo para guardar las multas
    private void guardarMultas() {
        // Guardar las multas en el archivo json
        GestorArchivos.guardarTodo(multas, ARCHIVO_MULTAS);
    }

    // Getter para obtener las multas
    public List<Multa> getMultas() {
        return multas;
    }

    // Metodo para obtener las multas entre dos fechas
    public List<Multa> obtenerMultasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return multas.stream()
                     // Filtrar las multas que tienen la fecha y hora de la multa entre las fechas
                     .filter(m -> m.getFechaHora().toLocalDate().isAfter(fechaInicio.minusDays(1)) && 
                                    m.getFechaHora().toLocalDate().isBefore(fechaFin.plusDays(1)))
                     .collect(Collectors.toList());
    }
}