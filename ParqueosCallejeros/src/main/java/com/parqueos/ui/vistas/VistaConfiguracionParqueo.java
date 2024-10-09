package com.parqueos.ui.vistas;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;

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
        spnHorarioInicio.setValue(configuracionActual.getHorarioInicio());
        gbc.gridx = 1;
        panel.add(spnHorarioInicio, gbc);

        // Horario de fin
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Horario de fin:"), gbc);
        spnHorarioFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deFin = new JSpinner.DateEditor(spnHorarioFin, "HH:mm");
        spnHorarioFin.setEditor(deFin);
        spnHorarioFin.setValue(configuracionActual.getHorarioFin());
        gbc.gridx = 1;
        panel.add(spnHorarioFin, gbc);

        // Precio por hora
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Precio por hora:"), gbc);
        spnPrecioHora = new JSpinner(new SpinnerNumberModel(configuracionActual.getPrecioHora(), 0, 10000, 100));
        gbc.gridx = 1;
        panel.add(spnPrecioHora, gbc);

        // Tiempo mínimo
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Tiempo mínimo (minutos):"), gbc);
        spnTiempoMinimo = new JSpinner(new SpinnerNumberModel(configuracionActual.getTiempoMinimo(), 1, 120, 1));
        gbc.gridx = 1;
        panel.add(spnTiempoMinimo, gbc);

        // Costo de multa
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Costo de multa:"), gbc);
        spnCostoMulta = new JSpinner(new SpinnerNumberModel(configuracionActual.getCostoMulta(), 0, 50000, 500));
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

    public ConfiguracionParqueo getConfiguracion() {
        LocalTime horarioInicio = LocalTime.parse(((JSpinner.DateEditor) spnHorarioInicio.getEditor()).getFormat().format(spnHorarioInicio.getValue()));
        LocalTime horarioFin = LocalTime.parse(((JSpinner.DateEditor) spnHorarioFin.getEditor()).getFormat().format(spnHorarioFin.getValue()));
        int precioHora = (Integer) spnPrecioHora.getValue();
        int tiempoMinimo = (Integer) spnTiempoMinimo.getValue();
        int costoMulta = (Integer) spnCostoMulta.getValue();

        return new ConfiguracionParqueo(horarioInicio, horarioFin, precioHora, tiempoMinimo, costoMulta);
    }

    public BotonPersonalizado getBtnGuardar() {
        return btnGuardar;
    }

    public BotonPersonalizado getBtnCancelar() {
        return btnCancelar;
    }
}