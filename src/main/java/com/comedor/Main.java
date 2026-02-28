package com.comedor;

import javax.swing.SwingUtilities;
import com.comedor.vista.InicioSesionUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InicioSesionUI().setVisible(true));
    }
}