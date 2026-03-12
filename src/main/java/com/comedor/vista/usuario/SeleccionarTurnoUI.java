package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.vista.components.FondoSemitransparentePanel;
import com.comedor.vista.components.TurnoToggleButton;
import com.comedor.vista.components.SideBarNavigation;
import com.comedor.vista.listeners.SeleccionarTurnoListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.net.URL;

public class SeleccionarTurnoUI extends JFrame {

    private final Usuario usuario;
    private final double costoPlatillo;
    private final String tipoComida;
    private ButtonGroup turnosGroup;

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);
    private BufferedImage backgroundImage;

    public SeleccionarTurnoUI(Usuario usuario, double costoPlatillo, String tipoComida) {
        this.usuario = usuario;
        this.costoPlatillo = costoPlatillo;
        this.tipoComida = tipoComida;

        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }

        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Seleccionar Turno - SAGC");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        // Panel principal con fondo - IGUAL que MenuUserUI
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g2d.setColor(COLOR_OVERLAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Barras azules - IGUAL que MenuUserUI
                int topBarHeight = 60;
                int bottomBarHeight = 30;
                g2d.setColor(new Color(0, 51, 102));
                g2d.fillRect(0, 0, getWidth(), topBarHeight);
                g2d.fillRect(0, getHeight() - bottomBarHeight, getWidth(), bottomBarHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // BARRA LATERAL - IGUAL estructura que HistorialReservasUI
        SideBarNavigation sideBar = new SideBarNavigation(usuario, () -> {
            new MenuUserUI(usuario).setVisible(true);
            dispose();
        });
        backgroundPanel.add(sideBar, BorderLayout.WEST);

        // HEADER - IGUAL que MenuUserUI
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel menuTitle = new JLabel("Seleccionar Turno de " + tipoComida, SwingConstants.CENTER);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        menuTitle.setForeground(Color.WHITE);
        headerPanel.add(menuTitle, BorderLayout.CENTER);

        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // --- CONTENIDO DERECHO (Para evitar superposición) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        // --- CONTENIDO CENTRAL CON TURNOS ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(Box.createGlue(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel de turnos
        JPanel turnosContainer = new FondoSemitransparentePanel();
        turnosContainer.setLayout(new BoxLayout(turnosContainer, BoxLayout.Y_AXIS));
        turnosContainer.setOpaque(false);
        turnosContainer.setBorder(new EmptyBorder(40, 50, 40, 50));
        turnosContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnosContainer.setMaximumSize(new Dimension(400, 450));

        JLabel turnosTitle = new JLabel("Turnos Disponibles");
        turnosTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        turnosTitle.setForeground(Color.WHITE);
        turnosTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnosTitle.setBorder(new EmptyBorder(0, 0, 25, 0));
        turnosContainer.add(turnosTitle);

        // Turnos - Cargar desde configuración
        turnosGroup = new ButtonGroup();
        String[] turnos = cargarTurnosDesdeConfiguracion();

        for (String turno : turnos) {
            JToggleButton turnoButton = createTurnoButton(turno);
            turnoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            turnosGroup.add(turnoButton);
            turnosContainer.add(turnoButton);
            turnosContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        contentPanel.add(turnosContainer);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Botón Continuar
        JButton btnContinuar = new JButton("Continuar a Verificación");
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnContinuar.setBackground(COLOR_AZUL_INST);
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFocusPainted(false);
        btnContinuar.setPreferredSize(new Dimension(250, 50));
        btnContinuar.setMaximumSize(new Dimension(250, 50));
        btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(new SeleccionarTurnoListener(usuario, costoPlatillo, tipoComida, turnosGroup, this));
        contentPanel.add(btnContinuar);

        centerPanel.add(contentPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(Box.createGlue(), gbc);

        rightPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private TurnoToggleButton createTurnoButton(String text) {
        TurnoToggleButton button = new TurnoToggleButton(text);
        button.setActionCommand(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private String[] cargarTurnosDesdeConfiguracion() {
        Properties props = new Properties();
        File configFile = new File("turnos_config.properties");

        // Valores por defecto
        String[] turnosDefault;
        if ("Desayuno".equalsIgnoreCase(tipoComida)) {
            turnosDefault = new String[]{"07:00 - 08:00", "08:00 - 09:00", "09:00 - 10:00"};
        } else {
            turnosDefault = new String[]{"12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00"};
        }

        if (!configFile.exists()) {
            return turnosDefault;
        }

        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in);

            if ("Desayuno".equalsIgnoreCase(tipoComida)) {
                return new String[]{
                    props.getProperty("desayuno.turno1", turnosDefault[0]),
                    props.getProperty("desayuno.turno2", turnosDefault[1]),
                    props.getProperty("desayuno.turno3", turnosDefault[2])
                };
            } else {
                return new String[]{
                    props.getProperty("almuerzo.turno1", turnosDefault[0]),
                    props.getProperty("almuerzo.turno2", turnosDefault[1]),
                    props.getProperty("almuerzo.turno3", turnosDefault[2])
                };
            }
        } catch (IOException e) {
            System.err.println("Error cargando configuración de turnos: " + e.getMessage());
            return turnosDefault;
        }
    }
}