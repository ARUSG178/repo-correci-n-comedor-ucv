package com.comedor.vista.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import com.comedor.utilidades.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.comedor.vista.components.SideBarNavigation;

/**
 * Interfaz gráfica para el Menú del Comedor del sistema SAGC UCV.
 */
public class VerMenuAdminUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen
    private static final Color CARD_BG = new Color(0, 0, 0, 140);

    private BufferedImage backgroundImage;
    private final com.comedor.modelo.entidades.Usuario usuario;
    
    // Datos del platillo
    private String nombreDesayuno = "Desayuno";
    private String precioDesayuno = "$ 0.00";
    private String rutaImagenDesayuno = "/com/comedor/resources/images/menu/base.jpg";

    private String nombreAlmuerzo = "Almuerzo";
    private String precioAlmuerzo = "$ 0.00";
    private String rutaImagenAlmuerzo = "/com/comedor/resources/images/menu/base.jpg";

    private String descDesayuno = "";
    private String nutDesayuno = "";
    private String descAlmuerzo = "";
    private String nutAlmuerzo = "";
    
    /**
     * Inicializa la interfaz del menú del comedor y carga recursos.
     */
    public VerMenuAdminUI(com.comedor.modelo.entidades.Usuario usuario) {
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
            nombreDesayuno = props.getProperty("desayuno_nombre", "Desayuno");
            precioDesayuno = props.getProperty("desayuno_precio", "$ 0.00");
            rutaImagenDesayuno = props.getProperty("desayuno_imagen", "/com/comedor/resources/images/menu/base.jpg");
            descDesayuno = props.getProperty("desayuno_descripcion", "");
            nutDesayuno = props.getProperty("desayuno_nutricion", "");

            nombreAlmuerzo = props.getProperty("almuerzo_nombre", "Almuerzo");
            precioAlmuerzo = props.getProperty("almuerzo_precio", "$ 0.00");
            rutaImagenAlmuerzo = props.getProperty("almuerzo_imagen", "/com/comedor/resources/images/menu/base.jpg");
            descAlmuerzo = props.getProperty("almuerzo_descripcion", "");
            nutAlmuerzo = props.getProperty("almuerzo_nutricion", "");
        } catch (Exception e) {
            Logger.error("Error al cargar datos del menú", e);
        }
    }

    private void configurarVentana() {
        setTitle("Vista del Menú del Comedor - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
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
                g2d.setColor(COLOR_OVERLAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(COLOR_AZUL_INST);
                int topBarHeight = 60;
                int bottomBarHeight = 30;
                g2d.fillRect(0, 0, getWidth(), topBarHeight);
                g2d.fillRect(0, getHeight() - bottomBarHeight, getWidth(), bottomBarHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        JLabel headerTitle = new JLabel("Editar Menú", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        SideBarNavigation sideBar = new SideBarNavigation(usuario, () -> {
            try {
                new PrincipalAdminUI(usuario).setVisible(true);
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al volver al panel principal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backgroundPanel.add(sideBar, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JPanel filaPlatillos = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        filaPlatillos.setOpaque(false);

        filaPlatillos.add(crearPlatilloPanel("Desayuno", rutaImagenDesayuno, nombreDesayuno, precioDesayuno));
        filaPlatillos.add(crearPlatilloPanel("Almuerzo", rutaImagenAlmuerzo, nombreAlmuerzo, precioAlmuerzo));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(filaPlatillos);

        rightPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel crearPlatilloPanel(String titulo, String rutaImagen, String nombre, String precio) {
        JPanel platilloPanel = new JPanel(new BorderLayout());
        platilloPanel.setOpaque(false);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblTituloPlatillo = new JLabel(titulo, SwingConstants.CENTER);
        lblTituloPlatillo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloPlatillo.setForeground(Color.WHITE);
        lblTituloPlatillo.setBorder(new EmptyBorder(0, 0, 10, 0));
        lblTituloPlatillo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombreLabel = new JLabel("Nombre:");
        lblNombreLabel.setForeground(Color.WHITE);
        lblNombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtNombre = new JTextField(nombre != null ? nombre : "");
        txtNombre.setMaximumSize(new Dimension(260, 30));
        txtNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtNombre.setBackground(new Color(0, 0, 0, 110));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setCaretColor(Color.WHITE);

        JPanel imagenPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 110));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        imagenPanel.setPreferredSize(new Dimension(280, 350));
        imagenPanel.setBackground(new Color(0, 0, 0, 0));

        try {
            Image img = null;
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                File f = new File(rutaImagen);
                if (f.exists()) {
                    img = ImageIO.read(f);
                } else {
                    URL url = getClass().getResource(rutaImagen);
                    if (url != null) img = ImageIO.read(url);
                }
            }
            if (img != null) {
                Image scaled = img.getScaledInstance(276, 346, Image.SCALE_SMOOTH);
                JLabel imagenLabel = new JLabel(new ImageIcon(scaled));
                imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imagenPanel.add(imagenLabel);
            }
        } catch (Exception e) {
            // fallback
        }

        JPanel editor = new JPanel();
        editor.setOpaque(false);
        editor.setLayout(new BoxLayout(editor, BoxLayout.Y_AXIS));
        editor.setBorder(new EmptyBorder(8, 10, 0, 10));

        JLabel lblPrecio = new JLabel("Precio ($):");
        lblPrecio.setForeground(Color.WHITE);
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField txtPrecio = new JTextField(precio != null ? precio.replace("$", "").trim() : "");
        txtPrecio.setMaximumSize(new Dimension(260, 30));
        txtPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPrecio.setBackground(new Color(0, 0, 0, 110));
        txtPrecio.setForeground(Color.WHITE);
        txtPrecio.setCaretColor(Color.WHITE);

        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setForeground(Color.WHITE);
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextArea txtDesc = new JTextArea(obtenerDesc(titulo));
        txtDesc.setBackground(new Color(0, 0, 0, 110));
        txtDesc.setForeground(Color.WHITE);
        txtDesc.setCaretColor(Color.WHITE);

        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(txtDesc);
        spDesc.setPreferredSize(new Dimension(260, 70));
        spDesc.setMaximumSize(new Dimension(260, 70));
        spDesc.getViewport().setBackground(new Color(0, 0, 0, 0));
        spDesc.setOpaque(false);
        spDesc.getViewport().setOpaque(false);

        JLabel lblNut = new JLabel("Valor nutricional:");
        lblNut.setForeground(Color.WHITE);
        lblNut.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNut.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextArea txtNut = new JTextArea(obtenerNut(titulo));
        txtNut.setBackground(new Color(0, 0, 0, 110));
        txtNut.setForeground(Color.WHITE);
        txtNut.setCaretColor(Color.WHITE);

        txtNut.setLineWrap(true);
        txtNut.setWrapStyleWord(true);
        JScrollPane spNut = new JScrollPane(txtNut);
        spNut.setPreferredSize(new Dimension(260, 70));
        spNut.setMaximumSize(new Dimension(260, 70));
        spNut.getViewport().setBackground(new Color(0, 0, 0, 0));
        spNut.setOpaque(false);
        spNut.getViewport().setOpaque(false);

        JButton btnImagen = new JButton("Cargar Imagen");
        btnImagen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnImagen.setBackground(new Color(0, 60, 120));
        btnImagen.setForeground(Color.WHITE);
        btnImagen.setFocusPainted(false);
        btnImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnImagen.setOpaque(true);
        btnImagen.setContentAreaFilled(true);

        final String[] rutaImagenSeleccionada = new String[] { rutaImagen };
        btnImagen.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                rutaImagenSeleccionada[0] = f.getAbsolutePath();
            }
        });

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(0, 81, 132));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setOpaque(true);
        btnGuardar.setContentAreaFilled(true);
        btnGuardar.addActionListener(e -> {
            guardarPlatillo(titulo, txtNombre.getText(), txtPrecio.getText(), txtDesc.getText(), txtNut.getText(), rutaImagenSeleccionada[0]);
            SwingUtilities.invokeLater(() -> {
                dispose();
                new VerMenuAdminUI(usuario).setVisible(true);
            });
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        acciones.setOpaque(false);
        acciones.add(btnImagen);
        acciones.add(btnGuardar);

        editor.add(lblNombreLabel);
        editor.add(txtNombre);
        editor.add(Box.createRigidArea(new Dimension(0, 6)));
        editor.add(lblPrecio);
        editor.add(txtPrecio);
        editor.add(Box.createRigidArea(new Dimension(0, 6)));
        editor.add(lblDesc);
        editor.add(spDesc);
        editor.add(Box.createRigidArea(new Dimension(0, 6)));
        editor.add(lblNut);
        editor.add(spNut);
        editor.add(Box.createRigidArea(new Dimension(0, 10)));
        editor.add(acciones);

        card.add(lblTituloPlatillo);
        card.add(imagenPanel);
        card.add(editor);
        platilloPanel.add(card, BorderLayout.CENTER);

        return platilloPanel;
    }

    private String obtenerDesc(String titulo) {
        if ("Desayuno".equalsIgnoreCase(titulo)) return descDesayuno;
        if ("Almuerzo".equalsIgnoreCase(titulo)) return descAlmuerzo;
        return "";
    }

    private String obtenerNut(String titulo) {
        if ("Desayuno".equalsIgnoreCase(titulo)) return nutDesayuno;
        if ("Almuerzo".equalsIgnoreCase(titulo)) return nutAlmuerzo;
        return "";
    }

    private void guardarPlatillo(String titulo, String nombreTxt, String precioTxt, String desc, String nut, String rutaImagen) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
        } catch (Exception e) {
            // sin archivo
        }

        String precioNorm = (precioTxt == null) ? "0.00" : precioTxt.trim();
        if (!precioNorm.startsWith("$")) {
            precioNorm = "$ " + precioNorm;
        }

        if ("Desayuno".equalsIgnoreCase(titulo)) {
            props.setProperty("desayuno_nombre", nombreTxt != null ? nombreTxt : "Desayuno");
            props.setProperty("desayuno_precio", precioNorm);
            props.setProperty("desayuno_descripcion", desc != null ? desc : "");
            props.setProperty("desayuno_nutricion", nut != null ? nut : "");
            props.setProperty("desayuno_imagen", rutaImagen != null ? rutaImagen : "");
        } else if ("Almuerzo".equalsIgnoreCase(titulo)) {
            props.setProperty("almuerzo_nombre", nombreTxt != null ? nombreTxt : "Almuerzo");
            props.setProperty("almuerzo_precio", precioNorm);
            props.setProperty("almuerzo_descripcion", desc != null ? desc : "");
            props.setProperty("almuerzo_nutricion", nut != null ? nut : "");
            props.setProperty("almuerzo_imagen", rutaImagen != null ? rutaImagen : "");
        }

        try (FileOutputStream out = new FileOutputStream("menu_config.properties")) {
            props.store(out, "Configuracion del Menu");
        } catch (Exception e) {
            Logger.error("Error guardando menu_config.properties", e);
        }
    }

    public static void main(String[] args) {
        com.comedor.modelo.entidades.Usuario adminDummy = new com.comedor.modelo.entidades.Administrador("0", "admin", "0");
        SwingUtilities.invokeLater(() -> new VerMenuAdminUI(adminDummy).setVisible(true));
    }
}