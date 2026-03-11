package com.comedor.vista.listeners;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.vista.usuario.MenuUserUI;
import com.comedor.utilidades.Logger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Listener para el botón de volver al menú principal
 */
public class VolverMenuListener extends MouseAdapter {
    private final Usuario usuario;
    private final java.awt.Window windowToClose;

    public VolverMenuListener(Usuario usuario, java.awt.Window windowToClose) {
        this.usuario = usuario;
        this.windowToClose = windowToClose;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Logger.info("Usuario " + usuario.obtCedula() + " volviendo al menú principal");
        new MenuUserUI(usuario).setVisible(true);
        windowToClose.dispose();
    }
}
