package com.parqueos.ui.vistas;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;


import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import com.parqueos.modelo.parqueo.ConfiguracionParqueo;
import com.parqueos.ui.componentes.BotonPersonalizado;
import com.parqueos.ui.componentes.PanelPersonalizado;
import java.time.ZoneId;

public class VistaConfiguracionParqueo extends JDialog {
    private JSpinner spnHorarioInicio;
    private JSpinner spnHorarioFin;
    private JSpinner spnPrecioHora;
    private JSpinner spnTiempoMinimo;
    private JSpinner spnCostoMulta;
    private BotonPersonalizado btnGuardar;
    private BotonPersonalizado btnCancelar;

    public VistaConfiguracionParqueo(JFrame parent, ConfiguracionParqueo configuracionActual) {
        super(parent, "Configuración del Parqueo", true);
        inicializarComponentes(configuracionActual);
    }
    
    //Conversión de LocalTime a Date
    private Date localTimeToDate(LocalTime localTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, localTime.getHour());
        calendar.set(Calendar.MINUTE, localTime.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void inicializarComponentes(ConfiguracionParqueo configuracionActual) {
        PanelPersonalizado panel = new PanelPersonalizado();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Horario de inicio
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Horario de inicio:"), gbc);
        spnHorarioInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deInicio = new JSpinner.DateEditor(spnHorarioInicio, "HH:mm");
        spnHorarioInicio.setEditor(deInicio);
        spnHorarioInicio.setValue(calendarFromLocalTime(configuracionActual.getHorarioInicio()).getTime());

        gbc.gridx = 1;
        panel.add(spnHorarioInicio, gbc);

        // Horario de fin
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Horario de fin:"), gbc);
        spnHorarioFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deFin = new JSpinner.DateEditor(spnHorarioFin, "HH:mm");
        spnHorarioFin.setEditor(deFin);
        spnHorarioFin.setValue(calendarFromLocalTime(configuracionActual.getHorarioFin()).getTime());
        gbc.gridx = 1;
        panel.add(spnHorarioFin, gbc);

        // Precio por hora
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Precio por hora:"), gbc);
        int precioHora = Math.max(0, Math.min(configuracionActual.getPrecioHora(), 10000));
        spnPrecioHora = new JSpinner(new SpinnerNumberModel(precioHora > 0 ? precioHora : 100, 0, 10000, 100)); // Valor predeterminado: 100
        gbc.gridx = 1;
        panel.add(spnPrecioHora, gbc);

        // Tiempo mínimo
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Tiempo mínimo (minutos):"), gbc);
        int tiempoMinimo = Math.max(30, Math.min(configuracionActual.getTiempoMinimo(), 120)); // Mínimo 30
        spnTiempoMinimo = new JSpinner(new SpinnerNumberModel(tiempoMinimo > 0 ? tiempoMinimo : 30, 30, 300, 1));
        gbc.gridx = 1;
        panel.add(spnTiempoMinimo, gbc);


        // Costo de multa
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Costo de multa:"), gbc);
        int costoMulta = Math.max(0, Math.min(configuracionActual.getCostoMulta(), 50000));
        spnCostoMulta = new JSpinner(new SpinnerNumberModel(costoMulta > 0 ? costoMulta : 500, 0, 50000, 500)); // Valor predeterminado: 500
        gbc.gridx = 1;
        panel.add(spnCostoMulta, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new BotonPersonalizado("Guardar");
        btnCancelar = new BotonPersonalizado("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(panelBotones, gbc);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }


    private int validarValor(int valor, int min, int max) {
        return Math.max(min, Math.min(valor, max));
    }
    
    public ConfiguracionParqueo getConfiguracion() {
    Date inicioDate = (Date) spnHorarioInicio.getValue();
    Date finDate = (Date) spnHorarioFin.getValue();
    LocalTime horarioInicio = localTimeFromCalendar(calendarFromLocalTime(inicioDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()));
    LocalTime horarioFin = localTimeFromCalendar(calendarFromLocalTime(finDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()));

    int precioHora = validarValor((Integer) spnPrecioHora.getValue(), 0, 10000);
    int tiempoMinimo = validarValor((Integer) spnTiempoMinimo.getValue(), 30, 300);
    int costoMulta = validarValor((Integer) spnCostoMulta.getValue(), 0, 50000);

    ConfiguracionParqueo configuracion = ConfiguracionParqueo.obtenerInstancia();
    configuracion.setHorarioInicio(horarioInicio);
    configuracion.setHorarioFin(horarioFin);
    configuracion.setPrecioHora(precioHora);
    configuracion.setTiempoMinimo(tiempoMinimo);
    configuracion.setCostoMulta(costoMulta);

    return configuracion;
}


    private Calendar calendarFromLocalTime(LocalTime time) {
        Calendar cal = Calendar.getInstance();
        if (time == null) {
            // Valor predeterminado: 8:00 AM
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, time.getHour());
            cal.set(Calendar.MINUTE, time.getMinute());
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private LocalTime localTimeFromCalendar(Calendar cal) {
        return LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    public BotonPersonalizado getBtnGuardar() {
        return btnGuardar;
    }

    public BotonPersonalizado getBtnCancelar() {
        return btnCancelar;
    }
}
