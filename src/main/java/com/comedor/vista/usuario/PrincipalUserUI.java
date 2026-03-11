package com.comedor.vista.usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.persistencia.RepoSecretaria;
import com.comedor.vista.components.SideBarNavigation;

public class PrincipalUserUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final int TOP_BAR_HEIGHT = 60;
    private static final int BOTTOM_BAR_HEIGHT = 30;

    private Usuario usuario;
    private BufferedImage backgroundImage;
    private SideBarNavigation sideBarNavigation;

    // Constructor por defecto para pruebas
    public PrincipalUserUI() {
        // Constructor por defecto para pruebas (usa un usuario dummy)
        this(new Estudiante("00000000", "1234", "General", "UCV"));
    }

    // Constructor principal que recibe el usuario autenticado
    public PrincipalUserUI(Usuario usuario) {
        this.usuario = usuario;
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }

    // Configura las propiedades de la ventana principal
    private void configurarVentana() {
        setTitle("Usuario - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana al abrirse
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }

    // Inicializa y construye todos los componentes de la interfaz
    private void initUI() {
        
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

                // Barras delgadas consistentes con el resto de vistas
                g2d.setColor(COLOR_AZUL_INST);
                g2d.fillRect(0, 0, getWidth(), TOP_BAR_HEIGHT);
                g2d.fillRect(0, getHeight() - BOTTOM_BAR_HEIGHT, getWidth(), BOTTOM_BAR_HEIGHT);

                // Separación sutil
                g2d.setColor(new Color(0, 40, 80, 80));
                g2d.fillRect(0, TOP_BAR_HEIGHT - 5, getWidth(), 5);
                g2d.fillRect(0, getHeight() - BOTTOM_BAR_HEIGHT, getWidth(), 5);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // --- HEADER (misma estructura que MenuUserUI) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_AZUL_INST);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel headerTitle = new JLabel("Panel Principal", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // AÑADIR LA BARRA LATERAL MEJORADA
        sideBarNavigation = new SideBarNavigation(usuario, () -> {
            // Callback para navegación al panel principal
            backgroundPanel.removeAll();
            backgroundPanel.add(sideBarNavigation, BorderLayout.WEST);
            backgroundPanel.add(headerPanel, BorderLayout.NORTH);
            backgroundPanel.add(crearPanelBienvenida(), BorderLayout.CENTER);
            backgroundPanel.revalidate();
            backgroundPanel.repaint();
        });
        backgroundPanel.add(sideBarNavigation, BorderLayout.WEST);

        // --- CONTENIDO DERECHO (Para evitar superposición) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        
        rightPanel.add(crearPanelBienvenida(), BorderLayout.CENTER);
        backgroundPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel crearPanelBienvenida() {
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setOpaque(false);
        
        JPanel welcomeContainer = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        welcomeContainer.setOpaque(false);
        welcomeContainer.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        // Mensaje personalizado con nombre del usuario
        String nombreCompleto = obtenerNombreDesdeRepositorio(usuario.obtCedula());
        JLabel welcomeTitle = new JLabel("<html><div style='text-align: center;'>!Bienvenido, " + nombreCompleto + "!</div></html>");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(Color.WHITE);
        
        JLabel welcomeSub = new JLabel("<html><div style='text-align: center;'>Utiliza el menu de la izquierda para acceder a todas las funciones.</div></html>");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeSub.setForeground(new Color(220, 220, 220));

        // Panel para la foto del usuario
        JPanel fotoPanel = new JPanel(new BorderLayout());
        fotoPanel.setOpaque(false);
        fotoPanel.setPreferredSize(new Dimension(120, 120));
        
        JLabel lblFoto = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo negro para la foto
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);
                
                // Intentar cargar foto del usuario desde imagenes_bd_secretaria
                try {
                    // Primero intentar cargar desde imagenes_bd_secretaria
                    File fotoFile = new File("imagenes_bd_secretaria/" + usuario.obtCedula() + ".jpg");
                    if (fotoFile.exists() && fotoFile.canRead()) {
                        BufferedImage foto = ImageIO.read(fotoFile);
                        // Escalar foto para que quepa en el círculo
                        Image scaled = foto.getScaledInstance(getWidth()-20, getHeight()-20, Image.SCALE_SMOOTH);
                        g2.drawImage(scaled, 10, 10, getWidth()-20, getHeight()-20, this);
                    } else {
                        // Si no existe en imagenes_bd_secretaria, intentar desde resources
                        URL imageUrl = getClass().getResource("/com/comedor/resources/images/usuarios/" + usuario.obtCedula() + ".jpg");
                        if (imageUrl != null) {
                            BufferedImage foto = ImageIO.read(imageUrl);
                            // Escalar foto para que quepa en el círculo
                            Image scaled = foto.getScaledInstance(getWidth()-20, getHeight()-20, Image.SCALE_SMOOTH);
                            g2.drawImage(scaled, 10, 10, getWidth()-20, getHeight()-20, this);
                        } else {
                            // Si no hay foto, mostrar iniciales
                            g2.setColor(Color.WHITE);
                            g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                            String iniciales = obtenerIniciales(usuario.obtNombre());
                            FontMetrics fm = g2.getFontMetrics();
                            int x = (getWidth() - fm.stringWidth(iniciales)) / 2;
                            int y = (getHeight() + fm.getAscent()) / 2;
                            g2.drawString(iniciales, x, y);
                        }
                    }
                } catch (Exception e) {
                    // Si hay error, mostrar iniciales
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

        GridBagConstraints gbc = new GridBagConstraints();
        
        // Mensaje de bienvenida
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 20);
        gbc.anchor = GridBagConstraints.WEST;
        welcomeContainer.add(welcomeTitle, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 20);
        welcomeContainer.add(welcomeSub, gbc);
        
        // Foto del usuario
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.EAST;
        welcomeContainer.add(fotoPanel, gbc);
        
        welcomePanel.add(welcomeContainer);
        return welcomePanel;
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
        return "Usuario";
    }
    
    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "U";
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
        SwingUtilities.invokeLater(() -> new PrincipalUserUI().setVisible(true));
    }
}
