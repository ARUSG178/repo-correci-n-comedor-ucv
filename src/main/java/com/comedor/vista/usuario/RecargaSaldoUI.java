package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.swing.border.EmptyBorder;

public class RecargaSaldoUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);

    private final Usuario usuario;
    private final Runnable onRecargaExitosa;
    private BufferedImage backgroundImage;

    public RecargaSaldoUI(Usuario usuario, Runnable onRecargaExitosa) {
        this.usuario = usuario;
        this.onRecargaExitosa = onRecargaExitosa;

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
        setTitle("Recargar Saldo - SAGC UCV");
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initUI() {
        // Panel de fondo con overlay
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                // Overlay difuminado
                g2d.setColor(COLOR_OVERLAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Header azul
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_AZUL_INST);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        JLabel headerTitle = new JLabel("Recargar Saldo", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Bottom bar azul
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(COLOR_AZUL_INST);
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 30));
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Panel central blanco con bordes redondeados
        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        centerPanel.setLayout(new BorderLayout());

        // Panel de recarga (reutilizamos PRecarga)
        PRecarga panelRecarga = new PRecarga(usuario, () -> {
            if (onRecargaExitosa != null) onRecargaExitosa.run();
            dispose();
        });
        centerPanel.add(panelRecarga, BorderLayout.CENTER);

        // Contenedor para centrar el panel blanco
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(centerPanel, new GridBagConstraints());

        backgroundPanel.add(wrapper, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Usuario dummy = new com.comedor.modelo.entidades.Estudiante("123", "123", "Ingeniería", "Ciencias");
            new RecargaSaldoUI(dummy, () -> System.out.println("Recarga exitosa")).setVisible(true);
        });
    }
}
