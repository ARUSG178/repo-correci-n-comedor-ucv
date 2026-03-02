package com.comedor.vista.listeners;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.utilidades.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;
import javax.swing.*;
import java.io.FileOutputStream;

/**
 * Listener para procesar la selección de turno y redirigir a verificación
 * Cumple con SRP: Responsabilidad única de manejar la lógica de selección de turno
 */
public class SeleccionarTurnoListener implements ActionListener {
    private final Usuario usuario;
    private final double costoPlatillo;
    private final String tipoComida;
    private final ButtonGroup turnosGroup;
    private final JFrame parentFrame;

    public SeleccionarTurnoListener(Usuario usuario, double costoPlatillo, String tipoComida, 
                                  ButtonGroup turnosGroup, JFrame parentFrame) {
        this.usuario = usuario;
        this.costoPlatillo = costoPlatillo;
        this.tipoComida = tipoComida;
        this.turnosGroup = turnosGroup;
        this.parentFrame = parentFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (turnosGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(parentFrame, "Debe seleccionar un turno.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String turnoSeleccionado = turnosGroup.getSelection().getActionCommand();
            // Extraer la hora de inicio del string "HH:mm - HH:mm"
            String horaInicioStr = turnoSeleccionado.split(" - ")[0].trim();
            
            // Asegurar formato HH:mm (agregar 0 si es necesario por seguridad)
            if (horaInicioStr.indexOf(':') == 1) {
                horaInicioStr = "0" + horaInicioStr;
            }

            LocalTime horaInicio = LocalTime.parse(horaInicioStr);
            
            // Combinar con la fecha de hoy
            LocalDateTime fechaReserva = LocalDateTime.of(LocalDate.now(), horaInicio);
            
            // Guardar datos para el módulo externo
            Properties props = new Properties();
            props.setProperty("cedula", usuario.obtCedula());
            props.setProperty("costo", String.valueOf(costoPlatillo));
            props.setProperty("fechaReserva", fechaReserva.toString());
            props.setProperty("tipoComida", tipoComida);

            try (FileOutputStream out = new FileOutputStream("verification_request.properties")) {
                props.store(out, "Solicitud de Verificacion Biometrica");
            }

            Logger.info("Turno seleccionado: " + turnoSeleccionado + " para usuario " + usuario.obtCedula());

            JOptionPane.showMessageDialog(parentFrame, 
                "Turno pre-seleccionado.\nPor favor, ejecute el módulo de verificación biométrica para completar el pago.", 
                "Paso Siguiente", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Volver a la pantalla principal del usuario
            new com.comedor.vista.usuario.PrincipalUserUI(usuario).setVisible(true);
            parentFrame.dispose();
        } catch (Exception ex) {
            Logger.error("Error al procesar el turno", ex);
            JOptionPane.showMessageDialog(parentFrame, "Error al procesar el turno: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
