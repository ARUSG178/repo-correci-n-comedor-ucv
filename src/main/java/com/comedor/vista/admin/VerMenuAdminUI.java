package com.comedor.vista.admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;
import com.comedor.vista.admin.PrincipalAdminUI;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Interfaz gráfica para el Menú del Comedor del sistema SAGC UCV.
 */
public class VerMenuAdminUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen

    private BufferedImage backgroundImage;
    
    // Datos del platillo
    private String nombreDesayuno = "Desayuno";
    private String precioDesayuno = "$ 0.00";
    private String rutaImagenDesayuno = "/com/comedor/resources/images/menu/base.jpg";

    private String nombreAlmuerzo = "Almuerzo";
    private String precioAlmuerzo = "$ 0.00";
    private String rutaImagenAlmuerzo = "/com/comedor/resources/images/menu/base.jpg";

    /**
     * Inicializa la interfaz del menú del comedor y carga recursos.
     */
    public VerMenuAdminUI() {
        cargarDatosPlatillo();
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }
    
    private void cargarDatosPlatillo() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
            nombreDesayuno = props.getProperty("desayuno_nombre", "Desayuno");
            precioDesayuno = props.getProperty("desayuno_precio", "$ 0.00");
            rutaImagenDesayuno = props.getProperty("desayuno_imagen", "/com/comedor/resources/images/menu/base.jpg");

            nombreAlmuerzo = props.getProperty("almuerzo_nombre", "Almuerzo");
            precioAlmuerzo = props.getProperty("almuerzo_precio", "$ 0.00");
            rutaImagenAlmuerzo = props.getProperty("almuerzo_imagen", "/com/comedor/resources/images/menu/base.jpg");
        } catch (Exception e) {}
    }

    /**
     * Configura propiedades básicas de la ventana.
     */
    private void configurarVentana() {
        setTitle("Vista del Menú del Comedor - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia en pantalla completa
    }

    /**
     * Crea un panel de platillo individual con solo imagen y botón.
     */
    private JPanel crearPlatilloPanel(String titulo, String rutaImagen, String nombre, String precio) {
        JPanel platilloPanel = new JPanel();
        platilloPanel.setLayout(new BorderLayout(0, 15));
        platilloPanel.setOpaque(false);
        
        // Título del platillo (Desayuno/Almuerzo)
        JLabel lblTituloPlatillo = new JLabel(titulo, SwingConstants.CENTER);
        lblTituloPlatillo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloPlatillo.setForeground(Color.WHITE);
        lblTituloPlatillo.setBorder(new EmptyBorder(0, 0, 10, 0));
        platilloPanel.add(lblTituloPlatillo, BorderLayout.NORTH);

        // Panel para la imagen (250x300)
        JPanel imagenPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Dibujar fondo con borde redondeado
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Dibujar borde decorativo
                g2d.setColor(new Color(0, 51, 102, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                // Dibujar número del platillo
                g2d.setColor(new Color(100, 100, 100, 200));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                String numero = nombre;
                int x = (getWidth() - fm.stringWidth(numero)) / 2;
                int y = 30;
                g2d.drawString(numero, x, y);
                
                // Dibujar icono de placeholder
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
                String icono = "🍽️";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(icono)) / 2;
                y = (getHeight() + fm.getAscent()) / 2 - 10;
                g2d.drawString(icono, x, y);
                
                // Texto indicativo
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                g2d.setColor(new Color(120, 120, 120, 180));
                String texto = precio;
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(texto)) / 2;
                y = getHeight() - 20;
                g2d.drawString(texto, x, y);
            }
        };
        
        // Tamaño fijo 280x350 (Reducido ~30%)
        imagenPanel.setPreferredSize(new Dimension(280, 350));
        imagenPanel.setMinimumSize(new Dimension(280, 350));
        imagenPanel.setMaximumSize(new Dimension(280, 350));
        imagenPanel.setBackground(new Color(245, 245, 245));
        
        // Aquí iría la carga de la imagen real cuando esté implementada
        try {
            Image img = null;
            URL url = getClass().getResource(rutaImagen);
            if (url != null) {
                img = ImageIO.read(url);
            } else {
                File f = new File(rutaImagen);
                if (f.exists()) img = ImageIO.read(f);
            }
            if (img != null) {
                Image scaled = img.getScaledInstance(276, 346, Image.SCALE_SMOOTH);
                JLabel imagenLabel = new JLabel(new ImageIcon(scaled));
                imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imagenPanel.add(imagenLabel);
            }
        } catch (Exception e) {
            // Fallback al mensaje dibujado
        }
        
        // Botón para seleccionar
        JButton btnSeleccionar = new JButton("VISTA PREVIA");
        btnSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSeleccionar.setBackground(new Color(0, 60, 120));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnSeleccionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Panel para centrar el botón
        JPanel botonPanel = new JPanel(new GridBagLayout());
        botonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        botonPanel.add(btnSeleccionar, gbc);
        
        // Agregar componentes al panel principal
        platilloPanel.add(imagenPanel, BorderLayout.CENTER);
        platilloPanel.add(botonPanel, BorderLayout.SOUTH);
        
        return platilloPanel;
    }

    private JLabel createTabLabel(String text) {
        JLabel tab = new JLabel(text);
        tab.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tab.setForeground(Color.WHITE);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tab.setForeground(new Color(255, 255, 255, 220));
                tab.setFont(new Font("Segoe UI", Font.BOLD, 19));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                tab.setForeground(Color.WHITE);
                tab.setFont(new Font("Segoe UI", Font.BOLD, 18));
            }
        });
        
        return tab;
    }

    /**
     * Construye y organiza los componentes del menú.
     */
    private void initUI() {
        
        // 1. PANEL DE FONDO: Dibuja la imagen, el filtro y las barras
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

                // Barras sólidas superior e inferior
                g2d.setColor(COLOR_AZUL_INST);
                int barHeight = 160;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // --- BARRA SUPERIOR CON LOGO Y PESTAÑAS ---
        JPanel topBarContainer = new JPanel(new BorderLayout());
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 160));
        
        JLabel brandLabel = new JLabel("< SAGC | Admin ") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Sombra
                g2.setFont(getFont());
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString(getText(), 3, 43);
                // Degradado metálico
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(220, 220, 220)));
                g2.drawString(getText(), 0, 40);
                g2.dispose();
            }
        };
        brandLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        brandLabel.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new PrincipalAdminUI().setVisible(true);
                VerMenuAdminUI.this.dispose();
            }
        });

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new PrincipalAdminUI().setVisible(true);
                    VerMenuAdminUI.this.dispose();
                }
            }
        });

        // --- PESTAÑAS DE FUNCIONALIDADES ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        tabsPanel.setOpaque(false);
        
        // Crear pestañas
        JLabel usuarioTab = createTabLabel("Usuario");
        JLabel menuTab = createTabLabel("AggPlatillo");
        
        tabsPanel.add(usuarioTab);
        tabsPanel.add(menuTab);

        usuarioTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Usuario
                // new UsuarioUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(VerMenuAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Usuario", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        menuTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new MenuAdminUI().setVisible(true); // This should probably go to PrincipalAdminUI and then to MenuAdminUI
                VerMenuAdminUI.this.dispose();
            }
        });


        // Panel para las pestañas (derecha)
        JPanel tabsContainer = new JPanel(new GridBagLayout());
        tabsContainer.setOpaque(false);
        
        GridBagConstraints gbcTabs = new GridBagConstraints();
        gbcTabs.gridx = 0;
        gbcTabs.gridy = 0;
        gbcTabs.weighty = 1.0;
        gbcTabs.anchor = GridBagConstraints.CENTER;
        
        JPanel tabsVerticalCenter = new JPanel(new BorderLayout());
        tabsVerticalCenter.setOpaque(false);
        tabsVerticalCenter.add(tabsPanel, BorderLayout.CENTER);
  
        tabsContainer.add(tabsVerticalCenter, gbcTabs);
        
        // Panel para el logo (izquierda)
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(500, 160));

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.weighty = 1.0;
        gbcLogo.insets = new Insets(0, -31, 0, 0);

        JPanel logoInnerContainer = new JPanel(new GridBagLayout());
        logoInnerContainer.setOpaque(false);

        GridBagConstraints gbcLogoInner = new GridBagConstraints();
        gbcLogoInner.gridx = 0;
        gbcLogoInner.gridy = 0;
        gbcLogoInner.anchor = GridBagConstraints.WEST;

        logoInnerContainer.add(brandLabel, gbcLogoInner);
        logoPanel.add(logoInnerContainer, gbcLogo);

        topBarContainer.add(logoPanel, BorderLayout.WEST);
        topBarContainer.add(tabsContainer, BorderLayout.EAST);
        
        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);
        
        JPanel bottomBarContainer = new JPanel();
        bottomBarContainer.setOpaque(false);
        bottomBarContainer.setPreferredSize(new Dimension(getWidth(), 160));
        backgroundPanel.add(bottomBarContainer, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel menuTitle = new JLabel("Seleccione su platillo", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(new EmptyBorder(0, 0, 50, 0));
        
        JPanel filaPlatillos = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        filaPlatillos.setOpaque(false);
        filaPlatillos.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Crear los platillos
        JPanel platillo1 = crearPlatilloPanel("Desayuno", rutaImagenDesayuno, nombreDesayuno, precioDesayuno);
        JPanel platillo2 = crearPlatilloPanel("Almuerzo", rutaImagenAlmuerzo, nombreAlmuerzo, precioAlmuerzo);
        
        // Añadir los platillos en horizontal
        filaPlatillos.add(platillo1);
        filaPlatillos.add(platillo2);
        
        contentPanel.add(menuTitle);
        contentPanel.add(filaPlatillos);
        
        centerPanel.add(contentPanel);
        
        centerPanel.add(Box.createVerticalGlue());
        
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerMenuAdminUI().setVisible(true));
    }
}