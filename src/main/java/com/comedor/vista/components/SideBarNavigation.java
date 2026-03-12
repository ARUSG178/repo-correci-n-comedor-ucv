package com.comedor.vista.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.vista.InicioSesionUI;
import com.comedor.vista.usuario.MenuUserUI;
import com.comedor.vista.usuario.RecargaSaldoUI;
import com.comedor.vista.admin.VerMenuAdminUI;
import com.comedor.vista.admin.GestionUsuariosUI;
import com.comedor.vista.DialogoCCB;

/**
 * Componente de barra lateral moderno y estilizado para el sistema SAGC UCV
 */
public class SideBarNavigation extends JPanel {

    // --- PALETA DE COLORES ORIGINAL AZUL ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102); // Azul institucional ORIGINAL
    private static final Color COLOR_TEXTO_PRIMARIO = new Color(220, 220, 220); // Blanco grisáceo para texto principal

    private boolean isSidebarExpanded = true;
    private boolean isAdminUser = false;
    private JPanel sideBar;
    private JLabel brandLabel;
    private JButton toggleButton;
    private List<JLabel> textLabels = new ArrayList<>();
    private List<JLabel> iconLabels = new ArrayList<>();
    private List<NavItem> navItems = new ArrayList<>();
    private JPanel walletPanel;
    private JLabel walletTextLabel;
    private JButton walletRechargeButton;
    private Timer sidebarTimer;
    private Usuario usuario;
    private Runnable onNavigationCallback;

    private static final int SIDEBAR_WIDTH_EXPANDED = 320;
    private static final int SIDEBAR_WIDTH_COLLAPSED = 95;
    private static final int SIDEBAR_ANIMATION_STEP = 15;
    private static final int SIDEBAR_ANIMATION_DELAY_MS = 10;

    private static class NavItem {
        private final JPanel panel;
        private final JLabel icon;
        private final JLabel text;

        private NavItem(JPanel panel, JLabel icon, JLabel text) {
            this.panel = panel;
            this.icon = icon;
            this.text = text;
        }
    }

    public SideBarNavigation(Usuario usuario, Runnable onNavigationCallback) {
        this.usuario = usuario;
        this.onNavigationCallback = onNavigationCallback;
        this.isAdminUser = (usuario instanceof Administrador);
        
        setOpaque(false);
        setLayout(new BorderLayout());
        // No establecer tamaño preferido - dejar que BorderLayout.WEST maneje la altura
        add(crearSideBarIzquierda(), BorderLayout.CENTER);
    }

    private JPanel crearSideBarIzquierda() {
        sideBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo sólido: un solo color (sin degradado fuerte)
                // Sombra sutil (dibujar primero para no oscurecer el relleno azul)
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 20, 20);

                g2d.setColor(COLOR_AZUL_INST);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setOpaque(false);
        sideBar.setPreferredSize(new Dimension(SIDEBAR_WIDTH_EXPANDED, Integer.MAX_VALUE));
        sideBar.setMaximumSize(new Dimension(SIDEBAR_WIDTH_EXPANDED, Integer.MAX_VALUE));
        sideBar.setBorder(new EmptyBorder(15, 15, 15, 15)); // Más padding general

        // --- Título/Logo mejorado (SIN BOTÓN HAMBURGUESA) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        header.setBorder(new EmptyBorder(20, 10, 20, 10));

        brandLabel = new JLabel(isAdminUser ? "SAGC | Admin" : "SAGC | Usuario", SwingConstants.CENTER);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandLabel.setForeground(COLOR_TEXTO_PRIMARIO);

        toggleButton = new JButton("≡");
        toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setFocusPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> toggleSidebar());

        header.add(toggleButton, BorderLayout.WEST);
        header.add(brandLabel, BorderLayout.CENTER);

        sideBar.add(header);
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Separador Visual mejorado ---
        sideBar.add(crearSeparador());
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Botones de Navegación ---
        if (isAdminUser) {
            agregarBotonesAdmin();
        } else {
            agregarBotonesUsuario();
        }

        // Espacio flexible
        sideBar.add(Box.createVerticalGlue());

        if (!isAdminUser) {
            walletPanel = crearWalletPanel();
            sideBar.add(walletPanel);
            sideBar.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // --- Separador Inferior ---
        sideBar.add(crearSeparador());
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Botón de Cerrar Sesión ---
        sideBar.add(crearBotonNavegacion("\ud83d\udeaa", "Cerrar Sesión", () -> {
            int confirm = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "¿Está seguro de que desea cerrar la sesión?",
                    "Confirmar Cierre de Sesión",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                new InicioSesionUI().setVisible(true);
                SwingUtilities.getWindowAncestor(this).dispose();
            }
        }, true));
        
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        updateSidebarUIForState();
        return sideBar;
    }

    private JPanel crearWalletPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.setBorder(new EmptyBorder(10, 18, 10, 18));

        JLabel walletIcon = new JLabel("\uD83D\uDCB0", SwingConstants.CENTER);
        walletIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        walletIcon.setForeground(COLOR_TEXTO_PRIMARIO);
        walletIcon.setPreferredSize(new Dimension(28, 28));

        walletTextLabel = new JLabel("Saldo: $ 0.00");
        walletTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        walletTextLabel.setForeground(COLOR_TEXTO_PRIMARIO);

        walletRechargeButton = new JButton("Recargar");
        walletRechargeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        walletRechargeButton.setBackground(COLOR_AZUL_INST);
        walletRechargeButton.setForeground(Color.WHITE);
        walletRechargeButton.setOpaque(true);
        walletRechargeButton.setContentAreaFilled(true);
        walletRechargeButton.setFocusPainted(false);
        walletRechargeButton.setBorderPainted(false);
        walletRechargeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        walletRechargeButton.setBorder(new EmptyBorder(8, 14, 8, 14));
        walletRechargeButton.addActionListener(e -> abrirRecargaSaldo());

        walletRechargeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                walletRechargeButton.setBackground(new Color(0, 81, 132));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                walletRechargeButton.setBackground(COLOR_AZUL_INST);
            }
        });

        panel.add(walletIcon, BorderLayout.WEST);
        panel.add(walletTextLabel, BorderLayout.CENTER);
        panel.add(walletRechargeButton, BorderLayout.EAST);

        actualizarSaldoWallet();
        return panel;
    }

    private void abrirRecargaSaldo() {
        SwingUtilities.invokeLater(() -> {
            RecargaSaldoUI ui = new RecargaSaldoUI(usuario, this::actualizarSaldoWallet);
            ui.setVisible(true);
        });
    }

    private void actualizarSaldoWallet() {
        if (walletTextLabel == null || usuario == null) {
            return;
        }
        walletTextLabel.setText(String.format("Saldo: $ %.2f", usuario.obtSaldo()));
    }

    private void toggleSidebar() {
        if (sidebarTimer != null && sidebarTimer.isRunning()) {
            return;
        }

        boolean collapsing = isSidebarExpanded;
        if (collapsing) {
            setExpandedUIVisible(false);
            applyNavLayout(false);
        }

        int startWidth = sideBar.getPreferredSize().width;
        int targetWidth = isSidebarExpanded ? SIDEBAR_WIDTH_COLLAPSED : SIDEBAR_WIDTH_EXPANDED;

        sidebarTimer = new Timer(SIDEBAR_ANIMATION_DELAY_MS, null);
        sidebarTimer.addActionListener(e -> {
            int currentWidth = sideBar.getPreferredSize().width;
            int direction = currentWidth < targetWidth ? 1 : -1;
            int nextWidth = currentWidth + (direction * SIDEBAR_ANIMATION_STEP);

            boolean reached = (direction > 0 && nextWidth >= targetWidth) || (direction < 0 && nextWidth <= targetWidth);
            if (reached) {
                nextWidth = targetWidth;
            }

            sideBar.setPreferredSize(new Dimension(nextWidth, Integer.MAX_VALUE));
            sideBar.setMaximumSize(new Dimension(nextWidth, Integer.MAX_VALUE));
            sideBar.revalidate();
            sideBar.repaint();

            if (reached) {
                sidebarTimer.stop();
                isSidebarExpanded = !isSidebarExpanded;
                updateSidebarUIForState();
            }
        });

        sideBar.setPreferredSize(new Dimension(startWidth, Integer.MAX_VALUE));
        sideBar.setMaximumSize(new Dimension(startWidth, Integer.MAX_VALUE));
        sidebarTimer.start();
    }

    private void updateSidebarUIForState() {
        boolean showText = isSidebarExpanded;

        setExpandedUIVisible(showText);
        applyNavLayout(showText);

        if (walletPanel != null) {
            walletRechargeButton.setVisible(showText);
            walletTextLabel.setVisible(showText);
        }

        if (toggleButton != null) {
            toggleButton.setHorizontalAlignment(SwingConstants.CENTER);
        }

        int width = showText ? SIDEBAR_WIDTH_EXPANDED : SIDEBAR_WIDTH_COLLAPSED;
        sideBar.setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
        sideBar.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        sideBar.revalidate();
        sideBar.repaint();
    }

    private void setExpandedUIVisible(boolean visible) {
        for (JLabel label : textLabels) {
            label.setVisible(visible);
        }

        if (brandLabel != null) {
            brandLabel.setVisible(visible);
        }

        if (walletRechargeButton != null) {
            walletRechargeButton.setVisible(visible);
        }
    }

    private void applyNavLayout(boolean expanded) {
        for (NavItem item : navItems) {
            item.panel.removeAll();

            if (expanded) {
                item.panel.setLayout(new BorderLayout(30, 0));
                item.panel.setBorder(new EmptyBorder(15, 30, 15, 30));
                item.panel.add(item.icon, BorderLayout.WEST);
                item.panel.add(item.text, BorderLayout.CENTER);
            } else {
                item.panel.setLayout(new BorderLayout());
                item.panel.setBorder(new EmptyBorder(15, 0, 15, 0));
                item.icon.setHorizontalAlignment(SwingConstants.CENTER);
                item.panel.add(item.icon, BorderLayout.CENTER);
                // Mantener el texto oculto para evitar que reserve espacio
                item.text.setVisible(false);
            }

            item.panel.revalidate();
            item.panel.repaint();
        }
    }

    private void agregarBotonesAdmin() {
        sideBar.add(crearBotonNavegacion("\ud83c\udfe0", "Panel Principal", () -> {
            if (onNavigationCallback != null) onNavigationCallback.run();
        }));
        sideBar.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado entre botones
        
        sideBar.add(crearBotonNavegacion("\ud83d\udc64", "Gestionar Usuarios", () -> {
            try {
                new GestionUsuariosUI(usuario).setVisible(true);
                SwingUtilities.getWindowAncestor(this).dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al abrir Gestion de Usuarios:\n" + e.getMessage(), "Error de Navegación", JOptionPane.ERROR_MESSAGE);
            }
        }));
        sideBar.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado entre botones
        
        sideBar.add(crearBotonNavegacion("\ud83d\udcdd", "Editor de Menú", () -> {
            try {
                new VerMenuAdminUI(usuario).setVisible(true);
                SwingUtilities.getWindowAncestor(this).dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al abrir Editor de Menú:\n" + e.getMessage(), "Error de Navegación", JOptionPane.ERROR_MESSAGE);
            }
        }));
        sideBar.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado entre botones
        
        sideBar.add(crearBotonNavegacion("\ud83d\udcc5", "Administrar Turnos", () -> {
            try {
                new com.comedor.vista.admin.ConfigurarTurnosUI(usuario).setVisible(true);
                SwingUtilities.getWindowAncestor(this).dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al abrir Administrar Turnos:\n" + e.getMessage(), "Error de Navegación", JOptionPane.ERROR_MESSAGE);
            }
        }));
        sideBar.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado entre botones
        
        sideBar.add(crearBotonNavegacion("\ud83d\udcb2", "Costos (CCB)", () -> {
            new DialogoCCB((java.awt.Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true);
        }));
        sideBar.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado entre botones
        
        sideBar.add(crearBotonNavegacion("\ud83c\udf7d\ufe0f", "Vista Previa Menú", () -> {
            try {
                new MenuUserUI(usuario).setVisible(true);
                SwingUtilities.getWindowAncestor(this).dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al abrir Vista Previa de Menú:\n" + e.getMessage(), "Error de Navegación", JOptionPane.ERROR_MESSAGE);
            }
        }));
    }

    private void agregarBotonesUsuario() {
        sideBar.add(crearBotonNavegacion("\ud83c\udfe0", "Panel Principal", () -> {
            if (onNavigationCallback != null) onNavigationCallback.run();
        }));
        sideBar.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado entre botones
        
        sideBar.add(crearBotonNavegacion("\ud83c\udf7d\ufe0f", "Menú del Día", () -> {
            new MenuUserUI(usuario).setVisible(true);
            SwingUtilities.getWindowAncestor(this).dispose();
        }));
        sideBar.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado entre botones
        
        sideBar.add(crearBotonNavegacion("\ud83d\udcca", "Historial", () -> {
            SwingUtilities.invokeLater(() -> {
                com.comedor.vista.usuario.HistorialReservasUI ui = new com.comedor.vista.usuario.HistorialReservasUI(usuario);
                ui.setVisible(true);
            });
        }));
    }

    private JPanel crearSeparador() {
        JPanel separador = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 8)); // Más sutil y transparente
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
            }
        };
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setOpaque(false);
        separador.setBorder(new EmptyBorder(5, 20, 5, 20)); // Menos altura
        return separador;
    }

    private JPanel crearBotonNavegacion(String icono, String texto, Runnable accion) {
        return crearBotonNavegacion(icono, texto, accion, false);
    }
    
    private JPanel crearBotonNavegacion(String icono, String texto, Runnable accion, boolean isLogout) {
        JPanel botonPanel = new JPanel();
        botonPanel.setLayout(new BorderLayout(30, 0));
        botonPanel.setOpaque(false);
        botonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, isLogout ? 110 : 120));
        botonPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        botonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonPanel.setToolTipText(texto);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblIcono.setForeground(COLOR_TEXTO_PRIMARIO);
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setVerticalAlignment(SwingConstants.CENTER);
        iconLabels.add(lblIcono);

        JLabel lblTexto = new JLabel(texto, SwingConstants.CENTER);
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTexto.setForeground(COLOR_TEXTO_PRIMARIO);
        textLabels.add(lblTexto);

        navItems.add(new NavItem(botonPanel, lblIcono, lblTexto));

        botonPanel.add(lblIcono, BorderLayout.WEST);
        botonPanel.add(lblTexto, BorderLayout.CENTER);

        botonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    accion.run();
                });
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblIcono.setForeground(new Color(255, 255, 255, 240));
                lblTexto.setForeground(new Color(255, 255, 255, 240));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblIcono.setForeground(COLOR_TEXTO_PRIMARIO);
                lblTexto.setForeground(COLOR_TEXTO_PRIMARIO);
            }
        });

        return botonPanel;
    }
}
