package com.comedor.vista.admin;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.persistencia.RepoSecretaria;
import com.comedor.vista.components.SideBarNavigation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PrincipalAdminUI extends JFrame {

    private final Usuario usuario;
    private BufferedImage backgroundImage;

    // Colores ORIGINALES AZULES
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102); // Azul institucional ORIGINAL
    public PrincipalAdminUI(Usuario usuario) {
        this.usuario = usuario;

        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null)
                backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }

        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Panel de Administración");
        setSize(1400, 950);
        setMinimumSize(new Dimension(1100, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g2d.setColor(new Color(0, 51, 102, 140)); // Overlay
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Barras azules consistentes
                int topBarHeight = 60;
                int bottomBarHeight = 30;
                g2d.setColor(COLOR_AZUL_INST);
                g2d.fillRect(0, 0, getWidth(), topBarHeight);
                g2d.fillRect(0, getHeight() - bottomBarHeight, getWidth(), bottomBarHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        // Mensaje personalizado con nombre del admin
        JLabel title = new JLabel("Panel Principal", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        headerPanel.add(title, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // --- SIDEBAR ---
        SideBarNavigation sideBar = new SideBarNavigation(usuario, () -> {
            // No hacer nada, ya estamos en el panel principal.
            // Esto evita que la ventana se cierre al hacer clic en su propio botón.
        });
        backgroundPanel.add(sideBar, BorderLayout.WEST);

        // --- CONTENIDO DERECHO ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        // --- CONTENIDO (BIENVENIDA) ---
        JPanel welcomePanel = crearPanelBienvenida();

        rightPanel.add(welcomePanel, BorderLayout.CENTER);
        backgroundPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 50, 40, 50);

        // Panel de bienvenida con foto
        JPanel welcomeContainer = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        welcomeContainer.setOpaque(false);
        welcomeContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Mensaje personalizado con nombre del administrador
        String nombreCompleto = obtenerNombreDesdeRepositorio(usuario.obtCedula());
        JLabel welcomeTitle = new JLabel("<html><div style='text-align: center; color: white;'>!Bienvenido, " + nombreCompleto + "!</div></html>");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel welcomeMessage = new JLabel("<html><div style='text-align: center; color: #e0e0e0;'>" +
            "Como administrador del sistema, puedes gestionar todas las funciones.<br>" +
            "Utiliza el menu de la izquierda para acceder a todas las opciones.</div></html>");
        welcomeMessage.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Panel para la foto del administrador
        JPanel fotoPanel = new JPanel(new BorderLayout());
        fotoPanel.setOpaque(false);
        fotoPanel.setPreferredSize(new Dimension(120, 120));
        
        JLabel lblFoto = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);
                
                try {
                    File fotoFile = new File("imagenes_bd_secretaria/" + usuario.obtCedula() + ".jpg");
                    if (fotoFile.exists() && fotoFile.canRead()) {
                        BufferedImage foto = ImageIO.read(fotoFile);
                        Image scaled = foto.getScaledInstance(getWidth()-20, getHeight()-20, Image.SCALE_SMOOTH);
                        g2.drawImage(scaled, 10, 10, getWidth()-20, getHeight()-20, this);
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                        String iniciales = obtenerIniciales(usuario.obtNombre());
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(iniciales)) / 2;
                        int y = (getHeight() + fm.getAscent()) / 2;
                        g2.drawString(iniciales, x, y);
                    }
                } catch (Exception e) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    String iniciales = obtenerIniciales(usuario.obtNombre());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(iniciales)) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2.drawString(iniciales, x, y);
                }
                g2.dispose();
            }
        };
        fotoPanel.add(lblFoto, BorderLayout.CENTER);

        GridBagConstraints gbcInner = new GridBagConstraints();
        
        // Mensaje de bienvenida
        gbcInner.gridx = 0;
        gbcInner.gridy = 0;
        gbcInner.insets = new Insets(0, 0, 20, 20);
        gbcInner.anchor = GridBagConstraints.WEST;
        welcomeContainer.add(welcomeTitle, gbcInner);
        
        gbcInner.gridy = 1;
        gbcInner.insets = new Insets(0, 0, 0, 20);
        welcomeContainer.add(welcomeMessage, gbcInner);
        
        // Foto del administrador
        gbcInner.gridx = 1;
        gbcInner.gridy = 0;
        gbcInner.gridheight = 2;
        gbcInner.insets = new Insets(0, 0, 0, 0);
        gbcInner.anchor = GridBagConstraints.EAST;
        welcomeContainer.add(fotoPanel, gbcInner);

        panel.add(welcomeContainer, gbc);

        return panel;
    }

    private String obtenerNombreDesdeRepositorio(String cedula) {
        // Primero usar el nombre ya guardado en el usuario
        if (usuario.obtNombre() != null && !usuario.obtNombre().trim().isEmpty()) {
            return usuario.obtNombre();
        }
        // Solo si no hay nombre guardado, buscar en secretaria
        try {
            RepoSecretaria repo = new RepoSecretaria();
            Usuario usuarioRepo = repo.buscarRegistroUCV(cedula);
            if (usuarioRepo != null && usuarioRepo.obtNombre() != null && !usuarioRepo.obtNombre().trim().isEmpty()) {
                return usuarioRepo.obtNombre();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener nombre desde repositorio: " + e.getMessage());
        }
        // Último recurso
        return "Administrador";
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "A";
        }
        String[] partes = nombre.trim().split(" ");
        StringBuilder iniciales = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isEmpty()) {
                iniciales.append(parte.charAt(0));
                if (iniciales.length() >= 2) break;
            }
        }
        return iniciales.toString().toUpperCase();
    }

    public static void main(String[] args) {
        com.comedor.modelo.entidades.Usuario adminDummy = new com.comedor.modelo.entidades.Administrador("0", "admin", "0");
        SwingUtilities.invokeLater(() -> new PrincipalAdminUI(adminDummy).setVisible(true));
    }
}
