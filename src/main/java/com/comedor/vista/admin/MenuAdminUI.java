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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import com.comedor.vista.admin.PrincipalAdminUI;
import com.comedor.vista.InicioSesionUI;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Interfaz gráfica para la administración del Menú del Comedor.
 */
public class MenuAdminUI extends JFrame {

    // --- PALETA DE COLORES ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);

    private BufferedImage backgroundImage;
    
    // Rutas de imágenes
    private String[] rutasImagenes = new String[2];
    private String[] nombresPlatillos = new String[2];
    private String[] preciosPlatillos = new String[2];

    // Componentes para los platillos
    private JLabel[] labelsImagen = new JLabel[2];
    private JTextField[] fieldsNombre = new JTextField[2];
    private JTextField[] fieldsPrecio = new JTextField[2];

    // Inicializa la interfaz de administración del menú.
    public MenuAdminUI() {
        // Inicializar datos de ejemplo
        cargarConfiguracion();
        
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }
    
    //Inicializa datos de ejemplo para los platillos.
    
    private void inicializarDatosEjemplo() {
        rutasImagenes[0] = "/com/comedor/resources/images/menu/base.jpg";
        nombresPlatillos[0] = "Desayuno";
        preciosPlatillos[0] = "$ 0.00";
        rutasImagenes[1] = "/com/comedor/resources/images/menu/base.jpg";
        nombresPlatillos[1] = "Almuerzo";
        preciosPlatillos[1] = "$ 0.00";
    }
    
    private void cargarConfiguracion() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
            nombresPlatillos[0] = props.getProperty("desayuno_nombre", "Desayuno");
            preciosPlatillos[0] = props.getProperty("desayuno_precio", "$ 0.00");
            rutasImagenes[0] = props.getProperty("desayuno_imagen", "/com/comedor/resources/images/menu/base.jpg");

            nombresPlatillos[1] = props.getProperty("almuerzo_nombre", "Almuerzo");
            preciosPlatillos[1] = props.getProperty("almuerzo_precio", "$ 0.00");
            rutasImagenes[1] = props.getProperty("almuerzo_imagen", "/com/comedor/resources/images/menu/base.jpg");
        } catch (IOException e) {
            inicializarDatosEjemplo();
        }
    }

    private void configurarVentana() {
        setTitle("Administración de Menú - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private ImageIcon cargarImagen(String ruta, int ancho, int alto) {
        try {
            Image img = null;
            // 1. Intentar cargar desde archivo local (prioridad para imágenes subidas)
            File f = new File(ruta);
            if (f.exists()) {
                img = ImageIO.read(f);
            } else {
                // 2. Si no es archivo local, intentar como recurso del JAR/Classpath
                URL url = getClass().getResource(ruta);
                if (url != null) img = ImageIO.read(url);
            }
            
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
        return crearIconoPlaceholder(ancho, alto);
    }
    
    private ImageIcon crearIconoPlaceholder(int ancho, int alto) {
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagen.createGraphics();
        
        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ancho, alto);
        
        // Borde
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(0, 0, ancho - 1, alto - 1);
        
        // Emoji de comida
        g2d.setColor(new Color(180, 180, 180));
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String icono = "🍽️";
        int x = (ancho - fm.stringWidth(icono)) / 2;
        int y = (alto - fm.getHeight()) / 2 + fm.getAscent() - 10;
        g2d.drawString(icono, x, y);
        
        // Texto
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.setColor(new Color(120, 120, 120));
        String texto = "Haz clic para cambiar";
        fm = g2d.getFontMetrics();
        x = (ancho - fm.stringWidth(texto)) / 2;
        y = alto - 20;
        g2d.drawString(texto, x, y);
        
        g2d.dispose();
        return new ImageIcon(imagen);
    }
    
    private JLabel createTabLabel(String text) {
        JLabel tab = new JLabel(text);
        tab.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tab.setForeground(Color.WHITE);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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

    private JPanel crearPanelPlatillo(int indice, String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel para la imagen (Reducido de 400x500 a 280x350)
        JPanel panelImagen = new JPanel(new GridBagLayout());
        panelImagen.setBackground(Color.WHITE);
        panelImagen.setBorder(BorderFactory.createLineBorder(COLOR_AZUL_INST, 2));
        panelImagen.setPreferredSize(new Dimension(280, 350));
        panelImagen.setMinimumSize(new Dimension(280, 350));
        
        // Crear imagenes
        labelsImagen[indice] = new JLabel();
        labelsImagen[indice].setHorizontalAlignment(SwingConstants.CENTER);
        labelsImagen[indice].setVerticalAlignment(SwingConstants.CENTER);
        
        // Cargar imagenes
        // Ajustamos la escala de la imagen cargada
        ImageIcon icono = cargarImagen(rutasImagenes[indice], 276, 346);
        labelsImagen[indice].setIcon(icono);
        
        // Cambiar imagen
        labelsImagen[indice].setCursor(new Cursor(Cursor.HAND_CURSOR));
        labelsImagen[indice].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarImagen(indice);
            }
        });
        
        // Panel para el nombre y precio
        JPanel panelInfo = new JPanel(new BorderLayout(5, 5));
        panelInfo.setOpaque(false);
        panelInfo.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        // Nombre
        JPanel panelNombre = new JPanel(new BorderLayout(2, 2));
        panelNombre.setOpaque(false);
        
        JLabel labelNombre = new JLabel("Nombre " + titulo + ":");
        labelNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelNombre.setForeground(Color.WHITE);
        
        fieldsNombre[indice] = new JTextField(nombresPlatillos[indice], 15);
        fieldsNombre[indice].setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsNombre[indice].setBackground(new Color(255, 255, 255, 230));
        fieldsNombre[indice].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_AZUL_INST, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        fieldsNombre[indice].setPreferredSize(new Dimension(280, 30));
        
        panelNombre.add(labelNombre, BorderLayout.NORTH);
        panelNombre.add(fieldsNombre[indice], BorderLayout.CENTER);
        
        // Precio 
        JPanel panelPrecio = new JPanel(new BorderLayout(2, 2));
        panelPrecio.setOpaque(false);
        panelPrecio.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JLabel labelPrecio = new JLabel("Precio " + titulo + ":");
        labelPrecio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelPrecio.setForeground(Color.WHITE);
        
        fieldsPrecio[indice] = new JTextField(preciosPlatillos[indice], 10);
        fieldsPrecio[indice].setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPrecio[indice].setBackground(new Color(255, 255, 255, 230));
        fieldsPrecio[indice].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 60, 120), 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        fieldsPrecio[indice].setPreferredSize(new Dimension(280, 30));
        
        fieldsPrecio[indice].setToolTipText("Formato: $ 0.00");
        
        panelPrecio.add(labelPrecio, BorderLayout.NORTH);
        panelPrecio.add(fieldsPrecio[indice], BorderLayout.CENTER);
        
        panelInfo.add(panelNombre, BorderLayout.NORTH);
        panelInfo.add(panelPrecio, BorderLayout.CENTER);
        
        // Botón explícito para cargar imagen
        JButton btnCargar = new JButton("CAMBIAR FOTO");
        btnCargar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCargar.setBackground(COLOR_AZUL_INST);
        btnCargar.setForeground(Color.WHITE);
        btnCargar.setFocusPainted(false);
        btnCargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCargar.addActionListener(e -> seleccionarImagen(indice));

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(btnCargar);

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setOpaque(false);
        panelControles.add(panelBoton);
        panelControles.add(panelInfo);

        panel.add(panelImagen, BorderLayout.CENTER);
        panel.add(panelControles, BorderLayout.SOUTH);
        
        return panel;
    }
    

    private void seleccionarImagen(int indice) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar imagen para Platillo " + (indice + 1));
        
        // Filtro para imágenes
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            
            @Override
            public String getDescription() {
                return "Imágenes (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });
        
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoOrigen = fileChooser.getSelectedFile();
            
            try {
                // Crear directorio local para guardar las imágenes
                File directorioDestino = new File("imagenes_menu");
                if (!directorioDestino.exists()) {
                    directorioDestino.mkdirs();
                }
                
                // Copiar la imagen seleccionada a la carpeta del sistema
                // Usamos un nombre fijo por índice para reemplazar la anterior fácilmente
                // Usamos timestamp para evitar bloqueos de archivo en Windows y problemas de caché
                String nombreArchivo = "platillo_" + indice + "_" + System.currentTimeMillis() + ".jpg"; 
                File archivoDestino = new File(directorioDestino, nombreArchivo);
                
                Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                rutasImagenes[indice] = archivoDestino.getAbsolutePath();
                
                // Cargar la imagen copiada
                labelsImagen[indice].setIcon(cargarImagen(rutasImagenes[indice], 276, 346));
                labelsImagen[indice].revalidate();
                labelsImagen[indice].repaint();
                
                JOptionPane.showMessageDialog(this, "Imagen cargada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Guarda cambios para platillos
    private void guardarCambios() {
        // Validar y actualizar nombres
        for (int i = 0; i < 2; i++) {
            nombresPlatillos[i] = fieldsNombre[i].getText().trim();
            if (nombresPlatillos[i].isEmpty()) {
                nombresPlatillos[i] = (i == 0) ? "Desayuno" : "Almuerzo";
                fieldsNombre[i].setText(nombresPlatillos[i]);
            }
            
            // Validar y actualizar precios
            preciosPlatillos[i] = fieldsPrecio[i].getText().trim();
            
            // Validación numérica estricta antes de guardar
            try {
                String precioLimpio = preciosPlatillos[i].replace("$", "").replace(" ", "").replace(",", ".");
                if (!precioLimpio.isEmpty()) {
                    double valor = Double.parseDouble(precioLimpio);
                    if (valor < 0) throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El precio ingresado para " + nombresPlatillos[i] + " no es válido (no use letras ni negativos).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (preciosPlatillos[i].isEmpty()) {
                preciosPlatillos[i] = "$ 0.00";
                fieldsPrecio[i].setText(preciosPlatillos[i]);
            } else {
                // Formatear precio si es necesario
                if (!preciosPlatillos[i].startsWith("$")) {
                    preciosPlatillos[i] = "$ " + preciosPlatillos[i];
                    fieldsPrecio[i].setText(preciosPlatillos[i]);
                }
            }
        }
        
        // Guardar en archivo de propiedades
        Properties props = new Properties();
        // Cargar propiedades existentes para no perder el CCB
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
        } catch (IOException e) {
            // Si no existe, se creará nuevo
        }

        try (FileOutputStream out = new FileOutputStream("menu_config.properties")) {
            props.setProperty("desayuno_nombre", nombresPlatillos[0]);
            props.setProperty("desayuno_precio", preciosPlatillos[0]);
            props.setProperty("desayuno_imagen", rutasImagenes[0]);
            props.setProperty("almuerzo_nombre", nombresPlatillos[1]);
            props.setProperty("almuerzo_precio", preciosPlatillos[1]);
            props.setProperty("almuerzo_imagen", rutasImagenes[1]);
            props.store(out, "Configuracion del Menu");
            JOptionPane.showMessageDialog(this, "✅ Configuración guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Construye interfaz
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
        
        // Logo SAGC | Admin
        JLabel brandLabel = new JLabel("< SAGC | Admin ") {
            @Override
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
                MenuAdminUI.this.dispose();
            }
        });

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new PrincipalAdminUI().setVisible(true);
                    MenuAdminUI.this.dispose();
                }
            }
        });

        topBarContainer.add(Box.createRigidArea(new Dimension(20, 0)));

        JPanel verticalCenterPanel = new JPanel();
        verticalCenterPanel.setOpaque(false);
        verticalCenterPanel.setLayout(new BoxLayout(verticalCenterPanel, BoxLayout.Y_AXIS));

        verticalCenterPanel.add(Box.createVerticalGlue());

        verticalCenterPanel.add(brandLabel);

        verticalCenterPanel.add(Box.createVerticalGlue());

        topBarContainer.add(verticalCenterPanel);

        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);
        
        // --- PESTAÑAS DE FUNCIONALIDADES ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        tabsPanel.setOpaque(false);
        
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
        
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(500, 160));

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.weighty = 1.0;
        gbcLogo.anchor = GridBagConstraints.WEST;
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
        
        // --- BARRA INFERIOR (solo espacio) ---
        JPanel bottomBarContainer = new JPanel();
        bottomBarContainer.setOpaque(false);
        bottomBarContainer.setPreferredSize(new Dimension(getWidth(), 160));
        backgroundPanel.add(bottomBarContainer, BorderLayout.SOUTH);

        // --- CONTENIDO PRINCIPAL CON SCROLL ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        JPanel scrollableContent = new JPanel();
        scrollableContent.setLayout(new BoxLayout(scrollableContent, BoxLayout.Y_AXIS));
        scrollableContent.setOpaque(false);
        
        JLabel adminTitle = new JLabel("ADMINISTRACIÓN DE MENÚ", SwingConstants.CENTER);
        adminTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        adminTitle.setForeground(Color.WHITE);
        adminTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminTitle.setBorder(new EmptyBorder(40, 0, 20, 0));
        
        JPanel instruccionesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        instruccionesPanel.setOpaque(false);
        instruccionesPanel.setMaximumSize(new Dimension(800, 60));
        instruccionesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel para los 4 platillos
        JPanel platillosContainer = new JPanel();
        platillosContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        platillosContainer.setOpaque(false);
        platillosContainer.setMaximumSize(new Dimension(1000, 750));
        platillosContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Crear el panel de configuración
        JPanel panelDesayuno = crearPanelPlatillo(0, "Desayuno");
        JPanel panelAlmuerzo = crearPanelPlatillo(1, "Almuerzo");
        platillosContainer.add(panelDesayuno);
        platillosContainer.add(panelAlmuerzo);
        
        // Panel de botones
        JPanel botonesContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        botonesContainer.setOpaque(false);
        botonesContainer.setMaximumSize(new Dimension(600, 100));
        botonesContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botón Guardar
        JButton btnGuardar = new JButton("GUARDAR CAMBIOS");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnGuardar.setBackground(new Color(0, 60, 120));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 40, 80), 2),
            new EmptyBorder(15, 40, 15, 40)
        ));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarCambios());
        
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("CERRAR");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCerrar.setBackground(new Color(80, 80, 80)); // Gris oscuro para cerrar
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
            new EmptyBorder(15, 40, 15, 40)
        ));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new PrincipalAdminUI().setVisible(true);
                MenuAdminUI.this.dispose();
            });
        });
        
        botonesContainer.add(btnGuardar);
        botonesContainer.add(btnCerrar);
        
        scrollableContent.add(adminTitle);
        scrollableContent.add(instruccionesPanel);
        scrollableContent.add(Box.createVerticalStrut(40));
        scrollableContent.add(platillosContainer);
        scrollableContent.add(Box.createVerticalStrut(40));
        scrollableContent.add(botonesContainer);
        scrollableContent.add(Box.createVerticalStrut(80));
        
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(0, 20, 0, 20));
        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        
        backgroundPanel.add(contentWrapper, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAdminUI().setVisible(true));
    }
}