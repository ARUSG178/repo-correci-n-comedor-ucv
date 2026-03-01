package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Administrador;
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
import java.awt.GridBagLayout;
import java.awt.Image;
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.comedor.vista.admin.PrincipalAdminUI;


  // Interfaz gráfica para el Menú del Comedor del sistema SAGC UCV.
public class MenuUserUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen

    private final Usuario usuario;
    private BufferedImage backgroundImage;
    
    // Datos del platillo
    private String nombreDesayuno, precioDesayuno, rutaImagenDesayuno;
    private String descDesayuno, infoNutricionalDesayuno;
    private String nombreAlmuerzo, precioAlmuerzo, rutaImagenAlmuerzo;
    private String descAlmuerzo, infoNutricionalAlmuerzo;

    private double ccbActual = 0.0;
    private double precioCalculadoDesayuno = 0.0;
    private double precioCalculadoAlmuerzo = 0.0;

    // Constructor por defecto para pruebas
    public MenuUserUI() {
        this(new Estudiante("00000000", "1234", "General", "UCV"));
    }

    // Constructor principal que recibe el usuario autenticado
    public MenuUserUI(Usuario usuario) {
        this.usuario = usuario;
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
            nombreDesayuno = props.getProperty("desayuno_nombre", "Desayuno no disponible");
            precioDesayuno = props.getProperty("desayuno_precio", "$ 0.00");
            rutaImagenDesayuno = props.getProperty("desayuno_imagen", "/com/comedor/resources/images/menu/base.jpg");
            descDesayuno = props.getProperty("desayuno_descripcion", "Descripción no disponible.");
            infoNutricionalDesayuno = props.getProperty("desayuno_nutricion", "Información nutricional pendiente.");

            nombreAlmuerzo = props.getProperty("almuerzo_nombre", "Almuerzo no disponible");
            precioAlmuerzo = props.getProperty("almuerzo_precio", "$ 0.00");
            rutaImagenAlmuerzo = props.getProperty("almuerzo_imagen", "/com/comedor/resources/images/menu/base.jpg");
            descAlmuerzo = props.getProperty("almuerzo_descripcion", "Descripción no disponible.");
            infoNutricionalAlmuerzo = props.getProperty("almuerzo_nutricion", "Información nutricional pendiente.");
            
            // Cargar CCB si existe
            String ccbStr = props.getProperty("ccb_actual", "0.0");
            ccbActual = Double.parseDouble(ccbStr);
            
            // Calcular precios para ambos
            precioCalculadoDesayuno = calcularPrecioParaUsuario(precioDesayuno);
            precioCalculadoAlmuerzo = calcularPrecioParaUsuario(precioAlmuerzo);
        } catch (Exception e) {}
    }

    private double calcularPrecioParaUsuario(String precioConfigurado) {
        // Lógica de negocio basada en el enunciado:
        // Estudiantes: 20% del CCB
        // Empleados: 50% del CCB (ejemplo, enunciado dice 90-110%, ajustamos a lógica simple o lo que definas)
        // Profesores/Admin: 100% CCB
        
        double base = 5.00; // Valor por defecto

        // 1. Intentar usar el precio configurado en el menú como base
        try {
            String pLimpio = precioConfigurado.replace("$", "").replace(" ", "").replace(",", ".").trim();
            base = Double.parseDouble(pLimpio);
        } catch (Exception e) {
            // Si falla el parseo, se mantiene 5.00 o el valor anterior
        }

        // 2. Si existe un CCB calculado, este tiene prioridad como base del costo
        if (ccbActual > 0) {
            base = ccbActual;
        }
        
        double precioFinal;
        if (usuario instanceof Estudiante) {
            precioFinal = base * 0.20; // 20%
        } else if (usuario instanceof Empleado) {
            precioFinal = base * 0.50; // 50%
        } else {
            precioFinal = base; // 100%
        }
        return precioFinal;
    }

    // Configura las propiedades de la ventana del menú
    private void configurarVentana() {
        setTitle("Menú del Comedor - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia en pantalla completa
    }

    // Crea un panel individual para mostrar un platillo del menú
    private JPanel crearPlatilloPanel(String titulo, String rutaImagen, String nombre, double precioCalculado, String descripcion, String infoNutricional) {
        JPanel platilloPanel = new JPanel();
        // Cambiamos a BoxLayout vertical para apilar elementos (Título, Imagen, Info, Botón)
        platilloPanel.setLayout(new BoxLayout(platilloPanel, BoxLayout.Y_AXIS));
        platilloPanel.setOpaque(false);
        
        // Título del platillo (Desayuno/Almuerzo)
        JLabel lblTituloPlatillo = new JLabel(titulo, SwingConstants.CENTER);
        lblTituloPlatillo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTituloPlatillo.setForeground(Color.WHITE);
        lblTituloPlatillo.setBorder(new EmptyBorder(0, 0, 10, 0));
        lblTituloPlatillo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel para la imagen (250x300)
        JPanel imagenPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(new Color(0, 51, 102, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                g2d.setColor(new Color(100, 100, 100, 200));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                String numero = nombre;
                int x = (getWidth() - fm.stringWidth(numero)) / 2;
                int y = 30;
                g2d.drawString(numero, x, y);
                
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
                String icono = "🍽️";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(icono)) / 2;
                y = (getHeight() + fm.getAscent()) / 2 - 10;
                g2d.drawString(icono, x, y);
                
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                g2d.setColor(new Color(120, 120, 120, 180));
                String texto = String.format("$ %.2f", precioCalculado);
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(texto)) / 2;
                y = getHeight() - 20;
                g2d.drawString(texto, x, y);
            }
        };
        
        // Tamaño aumentado para mejor visualización (380x480)
        imagenPanel.setPreferredSize(new Dimension(380, 480));
        imagenPanel.setMinimumSize(new Dimension(380, 480));
        imagenPanel.setMaximumSize(new Dimension(380, 480));
        imagenPanel.setBackground(new Color(245, 245, 245));
        imagenPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Aquí iría la carga de la imagen real cuando esté implementada
        try {
            Image img = null;
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                // Prioridad a archivo local (cargado por admin)
                File f = new File(rutaImagen);
                if (f.exists()) {
                    img = ImageIO.read(f);
                } else {
                    // Fallback a recursos
                    URL url = getClass().getResource(rutaImagen);
                    if (url != null) img = ImageIO.read(url);
                }
            }
            if (img != null) {
                Image scaled = img.getScaledInstance(376, 476, Image.SCALE_SMOOTH);
                JLabel imagenLabel = new JLabel(new ImageIcon(scaled));
                imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imagenPanel.add(imagenLabel);
            }
        } catch (Exception e) {
        }

        // --- NUEVA SECCIÓN: Descripción e Información Nutricional ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><div style='text-align: center; width: 260px;'>" + descripcion + "</div></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblDesc.setForeground(Color.WHITE);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNutri = new JLabel("<html><div style='text-align: center; width: 260px; color: #dddddd;'><br><b>Información Nutricional:</b><br>" + infoNutricional + "</div></html>");
        lblNutri.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNutri.setForeground(new Color(220, 220, 220));
        lblNutri.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(lblDesc);
        infoPanel.add(lblNutri);
        
        // Botón para seleccionar
        JButton btnSeleccionar = new JButton("SELECCIONAR");
        btnSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        Color colorBase = new Color(0, 60, 120);
        Color colorHover = new Color(0, 90, 180); // Azul más claro para el hover
        
        btnSeleccionar.setBackground(colorBase);
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnSeleccionar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSeleccionar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSeleccionar.setBackground(colorHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnSeleccionar.setBackground(colorBase);
            }
        });

        // Acción: Ir a Reconocimiento Facial
        btnSeleccionar.addActionListener(e -> {
            // Redirigir a selección de turno
            new SeleccionarTurnoUI(usuario, precioCalculado, titulo).setVisible(true);
            MenuUserUI.this.dispose();
        });
        
        // Panel envolvente para forzar el centrado del botón
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonPanel.setOpaque(false);
        botonPanel.add(btnSeleccionar);
        botonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        platilloPanel.add(lblTituloPlatillo);
        platilloPanel.add(imagenPanel);
        platilloPanel.add(infoPanel);
        platilloPanel.add(Box.createVerticalStrut(5));
        platilloPanel.add(botonPanel);
        
        return platilloPanel;
    }


    // Inicializa y construye la interfaz gráfica del menú
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
                g2d.setColor(COLOR_AZUL_INST);
                int barHeight = 135;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // --- LOGO ESTILIZADO (< SAGC)
        JLabel brandLabel = new JLabel("< SAGC") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString(getText(), 3, 43);
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
                if (usuario instanceof Administrador) {
                    new PrincipalAdminUI(usuario).setVisible(true);
                } else {
                    new PrincipalUserUI(usuario).setVisible(true);
                }
                MenuUserUI.this.dispose();
            }
        });

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (usuario instanceof Administrador) {
                        new PrincipalAdminUI(usuario).setVisible(true);
                    } else {
                        new PrincipalUserUI(usuario).setVisible(true);
                    }
                    MenuUserUI.this.dispose();
                }
            }
        });

        JPanel topBarContainer = new JPanel();
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 135));
        topBarContainer.setLayout(new BoxLayout(topBarContainer, BoxLayout.X_AXIS));

        topBarContainer.add(Box.createRigidArea(new Dimension(20, 0)));

        JPanel verticalCenterPanel = new JPanel();
        verticalCenterPanel.setOpaque(false);
        verticalCenterPanel.setLayout(new BoxLayout(verticalCenterPanel, BoxLayout.Y_AXIS));

        verticalCenterPanel.add(Box.createVerticalGlue());

        verticalCenterPanel.add(brandLabel);

        verticalCenterPanel.add(Box.createVerticalGlue());

        topBarContainer.add(verticalCenterPanel);

        topBarContainer.add(Box.createHorizontalGlue());
        
        // Título principal movido a la franja azul
        JLabel menuTitle = new JLabel("Seleccione su platillo", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        menuTitle.setForeground(Color.WHITE);
        topBarContainer.add(menuTitle);
        
        topBarContainer.add(Box.createHorizontalGlue());

        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL CON LOS 4 PLATILLOS EN HORIZONTAL ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        centerPanel.add(Box.createVerticalGlue());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel filaPlatillos = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        filaPlatillos.setOpaque(false);
        filaPlatillos.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel panelDesayuno = crearPlatilloPanel("Desayuno", rutaImagenDesayuno, nombreDesayuno, precioCalculadoDesayuno, descDesayuno, infoNutricionalDesayuno);
        JPanel panelAlmuerzo = crearPlatilloPanel("Almuerzo", rutaImagenAlmuerzo, nombreAlmuerzo, precioCalculadoAlmuerzo, descAlmuerzo, infoNutricionalAlmuerzo);
        
        filaPlatillos.add(panelDesayuno);
        filaPlatillos.add(panelAlmuerzo);
        
        contentPanel.add(filaPlatillos);
        
        centerPanel.add(contentPanel);
        
        centerPanel.add(Box.createVerticalGlue());
        
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuUserUI().setVisible(true));
    }
}