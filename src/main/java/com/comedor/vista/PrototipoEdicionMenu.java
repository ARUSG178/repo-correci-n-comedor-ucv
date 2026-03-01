package com.comedor.vista;

import com.comedor.controlador.ServicioMenu;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Menu;
import com.comedor.modelo.entidades.Platillo;
import com.comedor.modelo.entidades.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PrototipoEdicionMenu extends JFrame {

    private final Usuario usuarioAdmin;
    private final ServicioMenu servicioMenu;
    
    // Colores
    private static final Color COLOR_PRIMARY = new Color(0, 51, 102);
    private static final Color COLOR_BG = new Color(245, 245, 250);
    private static final Color COLOR_ACCENT = new Color(0, 120, 215);

    public PrototipoEdicionMenu(Usuario usuario) {
        this.usuarioAdmin = usuario;
        this.servicioMenu = new ServicioMenu();
        
        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Editor de Menú Profesional - SAGC");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(COLOR_BG);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));
        
        JLabel title = new JLabel("Gestión de Menú y Nutrición");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);
        
        mainPanel.add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabs.setBackground(Color.WHITE);
        
        tabs.addTab("Desayuno", crearEditorPlatillo("Desayuno"));
        tabs.addTab("Almuerzo", crearEditorPlatillo("Almuerzo"));
        
        mainPanel.add(tabs, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }

    private JPanel crearEditorPlatillo(String tipoMenu) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Cargar datos actuales
        Menu menuActual = servicioMenu.obtenerMenu(tipoMenu);
        Platillo platillo = (menuActual != null && !menuActual.obtPlatillos().isEmpty()) 
                            ? menuActual.obtPlatillos().get(0) 
                            : new Platillo("", 0.0);

        // Variables para componentes
        JTextField txtNombre = new JTextField(platillo.obtNombre());
        JTextField txtPrecio = new JTextField(String.valueOf(platillo.obtPrecio()));
        JTextArea txtDesc = new JTextArea(platillo.obtDescripcion());
        JTextArea txtNutri = new JTextArea(platillo.obtInfoNutricional());
        JLabel lblPreview = new JLabel();
        final String[] rutaImagen = {platillo.obtImagen()}; // Array para ser mutable en lambda

        // Configurar componentes
        styleTextField(txtNombre);
        styleTextField(txtPrecio);
        styleTextArea(txtDesc);
        styleTextArea(txtNutri);
        
        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // --- COLUMNA IZQUIERDA: IMAGEN ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 5; gbc.weightx = 0.4;
        
        JPanel imagePanel = new JPanel(new BorderLayout(0, 10));
        imagePanel.setOpaque(false);
        imagePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Fotografía del Platillo", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRIMARY));
        
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(350, 350));
        lblPreview.setBorder(new EmptyBorder(10, 10, 10, 10));
        actualizarImagen(lblPreview, rutaImagen[0]);
        
        JButton btnSubir = new JButton("Seleccionar Imagen...");
        styleButton(btnSubir, COLOR_ACCENT);
        btnSubir.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaImagen[0] = fc.getSelectedFile().getAbsolutePath();
                actualizarImagen(lblPreview, rutaImagen[0]);
            }
        });
        
        imagePanel.add(lblPreview, BorderLayout.CENTER);
        imagePanel.add(btnSubir, BorderLayout.SOUTH);
        
        panel.add(imagePanel, gbc);
        
        // --- COLUMNA DERECHA: DATOS ---
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.weightx = 0.6; gbc.weighty = 0;
        
        panel.add(crearLabel("Nombre del Platillo:"), gbc);
        
        gbc.gridy = 1;
        panel.add(txtNombre, gbc);
        
        gbc.gridy = 2;
        panel.add(crearLabel("Precio ($):"), gbc);
        
        gbc.gridy = 3;
        panel.add(txtPrecio, gbc);
        
        gbc.gridy = 4; gbc.weighty = 0.3;
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setOpaque(false);
        descPanel.add(crearLabel("Descripción Detallada:"), BorderLayout.NORTH);
        descPanel.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
        panel.add(descPanel, gbc);
        
        gbc.gridy = 5; gbc.weighty = 0.3;
        JPanel nutriPanel = new JPanel(new BorderLayout());
        nutriPanel.setOpaque(false);
        nutriPanel.add(crearLabel("Información Nutricional:"), BorderLayout.NORTH);
        nutriPanel.add(new JScrollPane(txtNutri), BorderLayout.CENTER);
        panel.add(nutriPanel, gbc);
        
        // --- BOTÓN GUARDAR ---
        gbc.gridy = 6; gbc.weighty = 0; gbc.gridwidth = 2; gbc.gridx = 0;
        JButton btnGuardar = new JButton("Guardar Cambios en " + tipoMenu);
        styleButton(btnGuardar, new Color(0, 100, 0));
        btnGuardar.setPreferredSize(new Dimension(200, 50));
        
        btnGuardar.addActionListener(e -> {
            try {
                double precio = Double.parseDouble(txtPrecio.getText().replace(",", "."));
                Platillo p = new Platillo(
                    txtNombre.getText(), 
                    txtDesc.getText(), 
                    precio, 
                    rutaImagen[0], 
                    txtNutri.getText()
                );
                
                Menu m = new Menu(tipoMenu);
                m.agregarPlatillo(p);
                
                servicioMenu.configurarMenu(usuarioAdmin, m);
                JOptionPane.showMessageDialog(this, "Menú de " + tipoMenu + " actualizado correctamente.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(btnGuardar, gbc);
        
        return panel;
    }

    private void actualizarImagen(JLabel lbl, String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            lbl.setText("Sin imagen seleccionada");
            lbl.setIcon(null);
            return;
        }
        try {
            BufferedImage img = null;
            File f = new File(ruta);
            if (f.exists()) img = ImageIO.read(f);
            else {
                java.net.URL url = getClass().getResource(ruta);
                if (url != null) img = ImageIO.read(url);
            }
            
            if (img != null) {
                Image scaled = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(scaled));
                lbl.setText("");
            } else {
                lbl.setText("No se pudo cargar la imagen");
            }
        } catch (IOException e) {
            lbl.setText("Error de lectura");
        }
    }

    private JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(COLOR_PRIMARY);
        return l;
    }

    private void styleTextField(JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleTextArea(JTextArea txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        // Usuario dummy para pruebas
        Administrador admin = new Administrador("00000000", "admin", "ADMIN123");
        SwingUtilities.invokeLater(() -> new PrototipoEdicionMenu(admin).setVisible(true));
    }
}