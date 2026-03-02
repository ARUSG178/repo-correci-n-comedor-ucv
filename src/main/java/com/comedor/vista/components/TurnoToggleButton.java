package com.comedor.vista.components;

import javax.swing.*;
import java.awt.*;

/**
 * Botón de toggle con estilo personalizado para selección de turnos
 * Cumple con SRP: Responsabilidad única de renderizar botones de turno
 */
public class TurnoToggleButton extends JToggleButton {
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);

    public TurnoToggleButton(String text) {
        super(text);
        configurarEstilo();
    }

    private void configurarEstilo() {
        setFont(new Font("Segoe UI", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setPreferredSize(new Dimension(300, 60));
        setMinimumSize(new Dimension(300, 60));
        setMaximumSize(new Dimension(300, 60));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Determinar colores basados en el estado
            Color bgColor = new Color(255, 255, 255, 40); // Fondo por defecto (transparente)
            Color borderColor = new Color(255, 255, 255, 100); // Borde por defecto
            
            if (isSelected()) {
                bgColor = COLOR_AZUL_INST; // Fondo seleccionado (Azul)
                borderColor = Color.WHITE;
            } else if (getModel().isRollover()) {
                bgColor = new Color(255, 255, 255, 70); // Fondo hover
                borderColor = new Color(255, 255, 255, 180);
            }
            
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            
            super.paintComponent(g);
        } finally {
            g2.dispose();
        }
    }
}
