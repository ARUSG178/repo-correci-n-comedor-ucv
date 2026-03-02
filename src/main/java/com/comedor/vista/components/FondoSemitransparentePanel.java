package com.comedor.vista.components;

import javax.swing.*;
import java.awt.*;

/**
 * Panel con fondo semitransparente y bordes redondeados
 * Cumple con SRP: Responsabilidad única de renderizar el fondo
 */
public class FondoSemitransparentePanel extends JPanel {
    
    public FondoSemitransparentePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 80)); // Fondo negro semitransparente
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
        } finally {
            g2.dispose();
        }
    }
}
