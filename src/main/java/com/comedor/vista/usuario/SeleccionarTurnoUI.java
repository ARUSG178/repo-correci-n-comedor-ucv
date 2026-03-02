package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.vista.components.FondoSemitransparentePanel;
import com.comedor.vista.components.TurnoToggleButton;
import com.comedor.vista.components.SideBarNavigation;
import com.comedor.vista.listeners.VolverMenuListener;
import com.comedor.vista.listeners.SeleccionarTurnoListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SeleccionarTurnoUI extends JFrame {

    private final Usuario usuario;
    private final double costoPlatillo;
    private final String tipoComida;
    private ButtonGroup turnosGroup;

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);
    private static final int TOP_BAR_HEIGHT = 60;
    private static final int BOTTOM_BAR_HEIGHT = 30;
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
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g2d.setColor(COLOR_OVERLAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(COLOR_AZUL_INST);
                g2d.fillRect(0, 0, getWidth(), TOP_BAR_HEIGHT);
                g2d.fillRect(0, getHeight() - BOTTOM_BAR_HEIGHT, getWidth(), BOTTOM_BAR_HEIGHT);

                g2d.setColor(new Color(0, 40, 80, 80));
                g2d.fillRect(0, TOP_BAR_HEIGHT - 5, getWidth(), 5);
                g2d.fillRect(0, getHeight() - BOTTOM_BAR_HEIGHT, getWidth(), 5);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // --- SIDEBAR ---
        SideBarNavigation sideBar = new SideBarNavigation(usuario, () -> {
            dispose();
        });
        mainPanel.add(sideBar, BorderLayout.WEST);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), TOP_BAR_HEIGHT));

        JLabel lblTitulo = new JLabel("Seleccionar Turno de " + tipoComida, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);

        JLabel btnVolver = new JLabel("  < Volver al Menú");
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addMouseListener(new VolverMenuListener(usuario, this));
        headerPanel.add(btnVolver, BorderLayout.WEST);

        // --- CONTENIDO ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // Panel que contendrá los turnos, con un fondo semitransparente y redondeado
        JPanel turnosContainer = new FondoSemitransparentePanel();
        turnosContainer.setLayout(new BoxLayout(turnosContainer, BoxLayout.Y_AXIS));
        turnosContainer.setOpaque(false);
        turnosContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel turnosTitle = new JLabel("Turnos Disponibles");
        turnosTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        turnosTitle.setForeground(Color.WHITE);
        turnosTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnosTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        turnosContainer.add(turnosTitle);

        turnosGroup = new ButtonGroup();
        String[] turnos;
        if ("Desayuno".equalsIgnoreCase(tipoComida)) {
            turnos = new String[]{"07:00 - 08:00", "08:00 - 09:00", "09:00 - 10:00"};
        } else { // Almuerzo
            turnos = new String[]{"12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00"};
        }

        for (String turno : turnos) {
            JToggleButton turnoButton = createTurnoButton(turno);
            turnosGroup.add(turnoButton);
            turnosContainer.add(turnoButton);
            turnosContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        centerPanel.add(turnosContainer);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        footerPanel.setOpaque(false);

        JButton btnContinuar = new JButton("Continuar a Verificación");
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnContinuar.setBackground(COLOR_AZUL_INST);
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFocusPainted(false);
        btnContinuar.setPreferredSize(new Dimension(250, 50));
        btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnContinuar.addActionListener(new SeleccionarTurnoListener(usuario, costoPlatillo, tipoComida, turnosGroup, this));
        
        footerPanel.add(btnContinuar);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private TurnoToggleButton createTurnoButton(String text) {
        TurnoToggleButton button = new TurnoToggleButton(text);
        button.setActionCommand(text);
        return button;
    }
}