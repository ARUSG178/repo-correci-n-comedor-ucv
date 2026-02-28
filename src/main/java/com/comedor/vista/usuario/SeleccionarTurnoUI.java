package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
                int barHeight = 80;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitulo = new JLabel("Seleccionar Turno de " + tipoComida, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);

        JLabel btnVolver = new JLabel("  < Volver al Menú");
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MenuUserUI(usuario).setVisible(true);
                dispose();
            }
        });
        headerPanel.add(btnVolver, BorderLayout.WEST);

        // --- CONTENIDO ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // Panel que contendrá los turnos, con un fondo semitransparente y redondeado
        JPanel turnosContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 80)); // Fondo negro semitransparente
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
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
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        footerPanel.setOpaque(false);

        JButton btnContinuar = new JButton("Continuar a Verificación");
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnContinuar.setBackground(Color.WHITE);
        btnContinuar.setForeground(COLOR_AZUL_INST);
        btnContinuar.setFocusPainted(false);
        btnContinuar.setPreferredSize(new Dimension(250, 50));
        btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnContinuar.addActionListener(e -> irAReconocimiento());
        
        footerPanel.add(btnContinuar);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JToggleButton createTurnoButton(String text) {
        JToggleButton button = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Determinar colores basados en el estado
                Color bgColor = new Color(255, 255, 255, 40); // Fondo por defecto (transparente)
                Color borderColor = new Color(255, 255, 255, 100); // Borde por defecto
                
                if (isSelected()) {
                    bgColor = new Color(255, 255, 255, 230); // Fondo seleccionado (blanco sólido)
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
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(300, 60));
        button.setMinimumSize(new Dimension(300, 60));
        button.setMaximumSize(new Dimension(300, 60));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setActionCommand(text);

        button.addChangeListener(e -> button.setForeground(button.isSelected() ? COLOR_AZUL_INST : Color.WHITE));
        return button;
    }

    private void irAReconocimiento() {
        if (turnosGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un turno.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
            
            // Pasamos al siguiente paso: Reconocimiento Facial
            new ReconocimientoFacialUI(usuario, costoPlatillo, fechaReserva).setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al procesar el turno: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}