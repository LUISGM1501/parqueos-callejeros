package com.parqueos.ui.vistas;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DialogoFecha {

    private JDateChooser dateChooserInicio;
    private JDateChooser dateChooserFin;

    public DialogoFecha() {
        // Inicializar los JDateChooser
        dateChooserInicio = new JDateChooser();
        dateChooserFin = new JDateChooser();

        // Establecer fechas por defecto
        dateChooserInicio.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        dateChooserFin.setDate(java.sql.Date.valueOf(LocalDate.now()));

        // Ajustar el tamaño de los JDateChooser
        dateChooserInicio.setPreferredSize(new Dimension(150, 20));
        dateChooserFin.setPreferredSize(new Dimension(150, 20));
    }

    public LocalDate[] mostrarDialogo() {
        // Crear un panel para los JDateChooser
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Fecha de inicio:"));
        panel.add(dateChooserInicio);
        panel.add(new JLabel("Fecha de fin:"));
        panel.add(dateChooserFin);

        // Mostrar el diálogo
        int result = JOptionPane.showConfirmDialog(null, panel, "Seleccione el rango de fechas", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Obtener las fechas seleccionadas
            Date fechaInicioDate = dateChooserInicio.getDate();
            Date fechaFinDate = dateChooserFin.getDate();

            // Validar que ambas fechas hayan sido seleccionadas
            if (fechaInicioDate == null || fechaFinDate == null) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar ambas fechas.");
                return null;
            }

            // Convertir Date a LocalDate
            LocalDate fechaInicio = fechaInicioDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFin = fechaFinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Validar que la fecha de fin no sea anterior a la fecha de inicio
            if (fechaFin.isBefore(fechaInicio)) {
                JOptionPane.showMessageDialog(null, "La fecha de fin no puede ser anterior a la fecha de inicio.");
                return null;
            }

            // Retornar las fechas seleccionadas
            return new LocalDate[]{fechaInicio, fechaFin};
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
            return null;
        }
    }
}
