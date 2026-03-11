package com.comedor.vista.usuario;

import com.comedor.controlador.ServicioMenu;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.vista.components.SideBarNavigation;
import com.comedor.vista.utils.UIConstants;
import com.comedor.utilidades.Logger;
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
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MenuUserUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen

    private Usuario usuario;
    private BufferedImage backgroundImage;
    private SideBarNavigation sideBarNavigation;
    private JLabel lblPrecioDesayuno;
    private JLabel lblPrecioAlmuerzo;
    private JLabel lblDescDesayuno;
    private JLabel lblDescAlmuerzo;
    private JLabel lblNutriDesayuno;
    private JLabel lblNutriAlmuerzo;
    private JLabel lblImagenDesayuno;
    private JLabel lblImagenAlmuerzo;

    private static final Color CARD_BG = new Color(0, 0, 0, 140);
    private static final Color CARD_TEXT = Color.WHITE;
    
    // Datos del platillo
    private String nombreDesayuno, precioDesayuno, rutaImagenDesayuno;
    private String descDesayuno, infoNutricionalDesayuno;
    private String nombreAlmuerzo, precioAlmuerzo, rutaImagenAlmuerzo;
    private String descAlmuerzo, infoNutricionalAlmuerzo;

    private double ccbActual = 0.0;

    // Constructor por defecto para pruebas
    public MenuUserUI() {
        this(new Estudiante("00000000", "1234", "General", "UCV"));
    }

    private static class PrecioBreakdown {
        private final double original;
        private final double base;
        private final double factor;
        private final double finalCobro;

        private PrecioBreakdown(double original, double base, double factor, double finalCobro) {
            this.original = original;
            this.base = base;
            this.factor = factor;
            this.finalCobro = finalCobro;
        }
    }

    private PrecioBreakdown calcularBreakdown(String precioConfigurado) {
        double original = 5.00;
        try {
            String pLimpio = precioConfigurado.replace("$", "").replace(" ", "").replace(",", ".").trim();
            original = Double.parseDouble(pLimpio);
        } catch (Exception e) {
            // fallback
        }

        double base = (ccbActual > 0) ? ccbActual : original;

        double factor = new ServicioMenu().factorParaUsuario(usuario);
        double finalCobro = base * factor;
        return new PrecioBreakdown(original, base, factor, finalCobro);
    }

    private String toHtmlPrecioBreakdown(PrecioBreakdown b) {
        double subsidio = Math.max(0.0, b.base - b.finalCobro);
        String tipo = (usuario instanceof Estudiante) ? "estudiante" : (usuario instanceof Empleado) ? "empleado" : (usuario instanceof com.comedor.modelo.entidades.Profesor) ? "profesor" : "usuario";
        String pct = String.format("%.0f", b.factor * 100.0);

        return "<html>"
                + "<div style='text-align:center; width: 390px;'>"
                + "<div style='font-size:10px; color:#cfcfcf;'>Precio original: $ " + String.format("%.2f", b.original) + "</div>"
                + "<div style='font-size:10px; color:#cfcfcf;'>Base (CCB): $ " + String.format("%.2f", b.base) + "</div>"
                + "<div style='font-size:10px; color:#cfcfcf;'>Tarifa aplicada (" + tipo + "): " + pct + "%</div>"
                + "<div style='font-size:10px; color:#cfcfcf;'>Subsidio: $ " + String.format("%.2f", subsidio) + "</div>"
                + "<div style='margin-top:6px; font-size:18px; color:#ffffff;'><b>Final: $ " + String.format("%.2f", b.finalCobro) + "</b></div>"
                + "</div>"
                + "</html>";
    }

    private static class RoundedTranslucentPanel extends JPanel {
        private final int arc;
        private final Color fill;

        private RoundedTranslucentPanel(int arc, Color fill) {
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(fill);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }
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

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // Reflejar cambios del administrador (precios/imagen/desc/nutrición/CCB/tarifas)
                SwingUtilities.invokeLater(() -> refrescarDesdeConfig());
            }
        });
    }

    private void refrescarDesdeConfig() {
        cargarDatosPlatillo();

        if (lblPrecioDesayuno != null) {
            PrecioBreakdown b = calcularBreakdown(precioDesayuno);
            lblPrecioDesayuno.setText(toHtmlPrecioBreakdown(b));
        }
        if (lblPrecioAlmuerzo != null) {
            PrecioBreakdown b = calcularBreakdown(precioAlmuerzo);
            lblPrecioAlmuerzo.setText(toHtmlPrecioBreakdown(b));
        }

        if (lblDescDesayuno != null) lblDescDesayuno.setText(toHtmlBlock(descDesayuno));
        if (lblDescAlmuerzo != null) lblDescAlmuerzo.setText(toHtmlBlock(descAlmuerzo));

        if (lblNutriDesayuno != null) lblNutriDesayuno.setText(toHtmlNutri(infoNutricionalDesayuno));
        if (lblNutriAlmuerzo != null) lblNutriAlmuerzo.setText(toHtmlNutri(infoNutricionalAlmuerzo));

        if (lblImagenDesayuno != null) setImagenEnLabel(lblImagenDesayuno, rutaImagenDesayuno);
        if (lblImagenAlmuerzo != null) setImagenEnLabel(lblImagenAlmuerzo, rutaImagenAlmuerzo);
    }

    private String toHtmlBlock(String texto) {
        String t = (texto != null && !texto.trim().isEmpty()) ? texto : "Descripción no disponible.";
        return "<html><div style='text-align: center; width: 390px;'>" + t + "</div></html>";
    }

    private String toHtmlNutri(String texto) {
        String t = (texto != null && !texto.trim().isEmpty()) ? texto : "Información nutricional pendiente.";
        return "<html><div style='text-align: center; width: 390px;'><br><b>Información Nutricional:</b><br>" + t + "</div></html>";
    }

    private void setImagenEnLabel(JLabel label, String rutaImagen) {
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
                int w = label.getWidth();
                int h = label.getHeight();
                if (w <= 0 || h <= 0) {
                    w = 360;
                    h = 360;
                }
                Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
                label.setText("");
            } else {
                label.setIcon(null);
                label.setText("Sin imagen");
            }
        } catch (Exception e) {
            label.setIcon(null);
            label.setText("Sin imagen");
        }
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

        } catch (Exception e) {
            Logger.error("Error al cargar datos del platillo", e);
        }
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

                // --- BARRAS AZULES SÓLIDAS ---
                int topBarHeight = 60;
                int bottomBarHeight = 30;
                g2d.setColor(new Color(0, 51, 102)); // Azul institucional
                g2d.fillRect(0, 0, getWidth(), topBarHeight);
                g2d.fillRect(0, getHeight() - bottomBarHeight, getWidth(), bottomBarHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // AÑADIR LA BARRA LATERAL MEJORADA
        sideBarNavigation = new SideBarNavigation(usuario, () -> {
            // Ir al panel principal según el tipo de usuario
            SwingUtilities.invokeLater(() -> {
                if (usuario instanceof com.comedor.modelo.entidades.Administrador) {
                    new com.comedor.vista.admin.PrincipalAdminUI(usuario).setVisible(true);
                } else {
                    new PrincipalUserUI(usuario).setVisible(true);
                }
                MenuUserUI.this.dispose();
            });
        });
        backgroundPanel.add(sideBarNavigation, BorderLayout.WEST);

        // --- HEADER (misma estructura/altura que SeleccionarTurnoUI) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel menuTitle = new JLabel("Seleccione su platillo", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        menuTitle.setForeground(Color.WHITE);
        headerPanel.add(menuTitle, BorderLayout.CENTER);

        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // --- CONTENIDO DERECHO (Para evitar superposición) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        // --- CONTENIDO CENTRAL CON LOS PLATILLOS ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        centerPanel.add(Box.createVerticalGlue());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel filaPlatillos = new JPanel(new GridBagLayout());
        filaPlatillos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panelDesayuno = crearPlatilloPanel("Desayuno", rutaImagenDesayuno, nombreDesayuno, descDesayuno, infoNutricionalDesayuno);
        JPanel panelAlmuerzo = crearPlatilloPanel("Almuerzo", rutaImagenAlmuerzo, nombreAlmuerzo, descAlmuerzo, infoNutricionalAlmuerzo);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 100); // gap de 100px a la derecha
        filaPlatillos.add(panelDesayuno, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        filaPlatillos.add(panelAlmuerzo, gbc);
        
        contentPanel.add(filaPlatillos);
        
        centerPanel.add(contentPanel);
        
        centerPanel.add(Box.createVerticalGlue());
        
        rightPanel.add(centerPanel, BorderLayout.CENTER);
        
        backgroundPanel.add(rightPanel, BorderLayout.CENTER);
    }

    // Crea un panel individual para mostrar un platillo del menú
    private JPanel crearPlatilloPanel(String titulo, String rutaImagen, String nombre, String descripcion, String infoNutricional) {
        JPanel platilloPanel = new JPanel(new BorderLayout());
        platilloPanel.setOpaque(false);

        final int contentWidth = UIConstants.CONTENT_WIDTH_NORMAL;

        RoundedTranslucentPanel card = new RoundedTranslucentPanel(26, CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(UIConstants.CARD_PADDING);
        card.setPreferredSize(new Dimension(UIConstants.CARD_MAX_WIDTH, 740));
        card.setMaximumSize(new Dimension(UIConstants.CARD_MAX_WIDTH, 740));
        card.setMinimumSize(new Dimension(UIConstants.CARD_MAX_WIDTH, 740));

        PrecioBreakdown breakdown = calcularBreakdown("Desayuno".equalsIgnoreCase(titulo) ? precioDesayuno : precioAlmuerzo);

        JLabel lblTituloPlatillo = new JLabel(titulo, SwingConstants.CENTER);
        lblTituloPlatillo.setFont(UIConstants.FONT_CARD_TITLE);
        lblTituloPlatillo.setForeground(new Color(160, 200, 255));
        lblTituloPlatillo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloPlatillo.setMaximumSize(new Dimension(contentWidth, 24));

        JLabel lblNombre = new JLabel(nombre != null ? nombre : "", SwingConstants.CENTER);
        lblNombre.setFont(UIConstants.FONT_CARD_SUBTITLE);
        lblNombre.setForeground(CARD_TEXT);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setMaximumSize(new Dimension(contentWidth, 28));

        // Panel para la imagen
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
        imagenPanel.setPreferredSize(new Dimension(390, 390));
        imagenPanel.setBackground(new Color(0, 0, 0, 0));

        JLabel imagenLabel = new JLabel();
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagenLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagenLabel.setMaximumSize(new Dimension(390, 390));
        setImagenEnLabel(imagenLabel, rutaImagen);
        imagenPanel.add(imagenLabel);
        
        // Asignar a variables de instancia para poder actualizarlas luego
        if ("Desayuno".equalsIgnoreCase(titulo)) {
            lblImagenDesayuno = imagenLabel;
        } else if ("Almuerzo".equalsIgnoreCase(titulo)) {
            lblImagenAlmuerzo = imagenLabel;
        }

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(UIConstants.spacingVertical(UIConstants.SPACING_SM));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Wrapper para centrar descripción
        JPanel wrapDesc = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapDesc.setOpaque(false);
        JLabel lblDesc = new JLabel(toHtmlBlock(descripcion));
        lblDesc.setFont(UIConstants.FONT_BODY_NORMAL);
        lblDesc.setForeground(CARD_TEXT);
        wrapDesc.add(lblDesc);
        
        // Asignar a variables de instancia para poder actualizarlas luego
        if ("Desayuno".equalsIgnoreCase(titulo)) {
            lblDescDesayuno = lblDesc;
        } else if ("Almuerzo".equalsIgnoreCase(titulo)) {
            lblDescAlmuerzo = lblDesc;
        }

        // Wrapper para centrar nutrición
        JPanel wrapNutri = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapNutri.setOpaque(false);
        JLabel lblNutri = new JLabel(toHtmlNutri(infoNutricional));
        lblNutri.setFont(UIConstants.FONT_BODY_NORMAL);
        lblNutri.setForeground(CARD_TEXT);
        wrapNutri.add(lblNutri);
        
        // Asignar a variables de instancia para poder actualizarlas luego
        if ("Desayuno".equalsIgnoreCase(titulo)) {
            lblNutriDesayuno = lblNutri;
        } else if ("Almuerzo".equalsIgnoreCase(titulo)) {
            lblNutriAlmuerzo = lblNutri;
        }

        // Wrapper para centrar precio
        JPanel wrapPrecio = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapPrecio.setOpaque(false);
        JLabel lblPrecio = new JLabel(toHtmlPrecioBreakdown(breakdown));
        lblPrecio.setFont(UIConstants.FONT_CARD_SUBTITLE);
        lblPrecio.setForeground(new Color(255, 215, 0));
        wrapPrecio.add(lblPrecio);

        // Wrapper para centrar botón
        JPanel wrapBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapBoton.setOpaque(false);
        JButton btnSeleccionar = new JButton("Seleccionar");
        btnSeleccionar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnSeleccionar.setFont(UIConstants.FONT_BUTTON);
        btnSeleccionar.setBackground(new Color(0, 40, 90));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeleccionar.setOpaque(true);
        btnSeleccionar.setContentAreaFilled(true);
        btnSeleccionar.addActionListener(e -> {
            if ("Desayuno".equalsIgnoreCase(titulo)) {
                new SeleccionarTurnoUI(usuario, breakdown.finalCobro, "Desayuno").setVisible(true);
            } else if ("Almuerzo".equalsIgnoreCase(titulo)) {
                new SeleccionarTurnoUI(usuario, breakdown.finalCobro, "Almuerzo").setVisible(true);
            }
            MenuUserUI.this.dispose();
        });
        wrapBoton.add(btnSeleccionar);

        infoPanel.add(wrapDesc);
        infoPanel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        infoPanel.add(wrapNutri);
        infoPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        infoPanel.add(wrapPrecio);
        infoPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        infoPanel.add(wrapBoton);

        card.add(lblTituloPlatillo);
        card.add(Box.createVerticalStrut(4));
        card.add(lblNombre);
        card.add(Box.createVerticalStrut(8));
        card.add(imagenPanel);
        card.add(infoPanel);

        platilloPanel.add(card, BorderLayout.CENTER);
        return platilloPanel;
    }

    // ... (rest of the code remains the same)
}
