package com.comedor.vista;

import com.comedor.controlador.ServicioMenu;
import com.comedor.modelo.entidades.Menu;
import com.comedor.modelo.entidades.Platillo;
import com.comedor.modelo.entidades.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PrincipalAdminUI extends JFrame {

    private final Usuario usuario;
    private final ServicioMenu servicioMenu;
    // --- Componentes Desayuno ---
    private JTextField txtNombreDes;
    private JTextField txtPrecioDes;
    private JTextArea txtDescDes;
    private JTextArea txtNutriDes;
    private JLabel lblImgDes;
    private String rutaImgDes = "";

    // --- Componentes Almuerzo ---
    private JTextField txtNombreAlm;
    private JTextField txtPrecioAlm;
    private JTextArea txtDescAlm;
    private JTextArea txtNutriAlm;
    private JLabel lblImgAlm;
    private String rutaImgAlm = "";

    // Colores
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_FONDO_PANEL = new Color(245, 245, 250);

    public PrincipalAdminUI(Usuario usuario) {
        this.usuario = usuario;
        this.servicioMenu = new ServicioMenu(); // Carga la config automáticamente

        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null)
                ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }

        configurarVentana();
        initUI();
        cargarDatosActuales();
    }

    private void configurarVentana() {
        setTitle("Panel de Administración - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(1100, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_AZUL_INST);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel title = new JLabel("Gestión del Menú Semanal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(200, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            new InicioSesionUI().setVisible(true);
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // --- CONTENIDO (PESTAÑAS) ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        tabs.addTab("Editor de Menú", crearPanelEditorMenu());
        tabs.addTab("Gestión de Costos", crearPanelCostos());

        mainPanel.add(tabs, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel crearPanelEditorMenu() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel Desayuno
        JPanel pDesayuno = crearPanelPlatillo("Desayuno");
        txtNombreDes = (JTextField) pDesayuno.getClientProperty("nombre");
        txtPrecioDes = (JTextField) pDesayuno.getClientProperty("precio");
        txtDescDes = (JTextArea) pDesayuno.getClientProperty("desc");
        txtNutriDes = (JTextArea) pDesayuno.getClientProperty("nutri");
        lblImgDes = (JLabel) pDesayuno.getClientProperty("img");
        JButton btnImgDes = (JButton) pDesayuno.getClientProperty("btnImg");
        
        btnImgDes.addActionListener(e -> seleccionarImagen(lblImgDes, true));

        // Panel Almuerzo
        JPanel pAlmuerzo = crearPanelPlatillo("Almuerzo");
        txtNombreAlm = (JTextField) pAlmuerzo.getClientProperty("nombre");
        txtPrecioAlm = (JTextField) pAlmuerzo.getClientProperty("precio");
        txtDescAlm = (JTextArea) pAlmuerzo.getClientProperty("desc");
        txtNutriAlm = (JTextArea) pAlmuerzo.getClientProperty("nutri");
        lblImgAlm = (JLabel) pAlmuerzo.getClientProperty("img");
        JButton btnImgAlm = (JButton) pAlmuerzo.getClientProperty("btnImg");

        btnImgAlm.addActionListener(e -> seleccionarImagen(lblImgAlm, false));

        panel.add(pDesayuno);
        panel.add(pAlmuerzo);

        // Botón Guardar Global
        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
        
        JButton btnGuardar = new JButton("GUARDAR CAMBIOS EN EL MENÚ");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnGuardar.setBackground(new Color(0, 100, 0));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(getWidth(), 60));
        btnGuardar.addActionListener(e -> guardarCambios());
        
        container.add(btnGuardar, BorderLayout.SOUTH);

        return container;
    }

    private JPanel crearPanelPlatillo(String titulo) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_AZUL_INST, 2), 
            " " + titulo + " ", 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 20), 
            COLOR_AZUL_INST
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Imagen Preview
        JLabel lblImg = new JLabel("Sin Imagen", SwingConstants.CENTER);
        lblImg.setPreferredSize(new Dimension(200, 200));
        lblImg.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridheight = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.BOTH;
        card.add(lblImg, gbc);

        // Botón subir imagen
        JButton btnSubir = new JButton("Cambiar Imagen");
        gbc.gridy = 4; gbc.gridheight = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(btnSubir, gbc);

        // Campos de texto (Columna derecha)
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        
        card.add(new JLabel("Nombre del Platillo:"), gbc);
        JTextField txtNombre = new JTextField();
        gbc.gridy = 1;
        card.add(txtNombre, gbc);

        gbc.gridy = 2;
        card.add(new JLabel("Precio Base ($):"), gbc);
        JTextField txtPrecio = new JTextField();
        gbc.gridy = 3;
        card.add(txtPrecio, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        card.add(new JLabel("Descripción del Plato:"), gbc);
        
        JTextArea txtDesc = new JTextArea(3, 20);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        gbc.gridy = 6; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        card.add(scrollDesc, gbc);

        // Info Nutricional
        gbc.gridy = 7; gbc.weighty = 0;
        card.add(new JLabel("Información Nutricional (Calorías, Proteínas, etc.):"), gbc);
        
        JTextArea txtNutri = new JTextArea(3, 20);
        txtNutri.setLineWrap(true);
        txtNutri.setWrapStyleWord(true);
        JScrollPane scrollNutri = new JScrollPane(txtNutri);
        gbc.gridy = 8; gbc.weighty = 1.0;
        card.add(scrollNutri, gbc);

        // Guardar referencias en el panel para acceder luego
        card.putClientProperty("nombre", txtNombre);
        card.putClientProperty("precio", txtPrecio);
        card.putClientProperty("desc", txtDesc);
        card.putClientProperty("nutri", txtNutri);
        card.putClientProperty("img", lblImg);
        card.putClientProperty("btnImg", btnSubir);

        return card;
    }

    private JPanel crearPanelCostos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO_PANEL);

        JButton btnCCB = new JButton("Calcular Costo por Bandeja (CCB)");
        btnCCB.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCCB.setPreferredSize(new Dimension(350, 60));
        btnCCB.addActionListener(e -> new DialogoCCB(this).setVisible(true));

        panel.add(btnCCB);
        return panel;
    }

    // --- LÓGICA ---

    private void cargarDatosActuales() {
        // Cargar Desayuno
        Menu mDes = servicioMenu.obtenerMenu("Desayuno");
        if (mDes != null && !mDes.obtPlatillos().isEmpty()) {
            Platillo p = mDes.obtPlatillos().get(0);
            txtNombreDes.setText(p.obtNombre());
            txtPrecioDes.setText(String.valueOf(p.obtPrecio()));
            txtDescDes.setText(p.obtDescripcion());
            txtNutriDes.setText(p.obtInfoNutricional());
            rutaImgDes = p.obtImagen();
            actualizarPreview(lblImgDes, rutaImgDes);
        }

        // Cargar Almuerzo
        Menu mAlm = servicioMenu.obtenerMenu("Almuerzo");
        if (mAlm != null && !mAlm.obtPlatillos().isEmpty()) {
            Platillo p = mAlm.obtPlatillos().get(0);
            txtNombreAlm.setText(p.obtNombre());
            txtPrecioAlm.setText(String.valueOf(p.obtPrecio()));
            txtDescAlm.setText(p.obtDescripcion());
            txtNutriAlm.setText(p.obtInfoNutricional());
            rutaImgAlm = p.obtImagen();
            actualizarPreview(lblImgAlm, rutaImgAlm);
        }
    }

    private void seleccionarImagen(JLabel lblPreview, boolean esDesayuno) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes JPG/PNG", "jpg", "png", "jpeg"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String rutaAbsoluta = archivo.getAbsolutePath();
            
            if (esDesayuno) {
                rutaImgDes = rutaAbsoluta;
            } else {
                rutaImgAlm = rutaAbsoluta;
            }
            
            actualizarPreview(lblPreview, rutaAbsoluta);
        }
    }

    private void actualizarPreview(JLabel label, String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            label.setText("Sin Imagen");
            label.setIcon(null);
            return;
        }
        
        try {
            BufferedImage img = null;
            File f = new File(ruta);
            if (f.exists()) {
                img = ImageIO.read(f);
            } else {
                // Intento cargar desde recursos si es ruta relativa
                URL url = getClass().getResource(ruta);
                if (url != null) img = ImageIO.read(url);
            }

            if (img != null) {
                // Escalar imagen para que quepa en el label (200x200)
                Image scaled = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
                label.setText("");
            } else {
                label.setText("No encontrada");
                label.setIcon(null);
            }
        } catch (Exception e) {
            label.setText("Error carga");
            e.printStackTrace();
        }
    }

    private void guardarCambios() {
        try {
            // 1. Guardar Desayuno
            String nomDes = txtNombreDes.getText();
            double preDes = Double.parseDouble(txtPrecioDes.getText().replace(",", "."));
            String descDes = txtDescDes.getText();
            String nutriDes = txtNutriDes.getText();
            
            Platillo pDes = new Platillo(nomDes, descDes, preDes, rutaImgDes, nutriDes);
            Menu mDes = new Menu("Desayuno");
            mDes.agregarPlatillo(pDes);
            servicioMenu.configurarMenu(usuario, mDes);

            // 2. Guardar Almuerzo
            String nomAlm = txtNombreAlm.getText();
            double preAlm = Double.parseDouble(txtPrecioAlm.getText().replace(",", "."));
            String descAlm = txtDescAlm.getText();
            String nutriAlm = txtNutriAlm.getText();

            Platillo pAlm = new Platillo(nomAlm, descAlm, preAlm, rutaImgAlm, nutriAlm);
            Menu mAlm = new Menu("Almuerzo");
            mAlm.agregarPlatillo(pAlm);
            servicioMenu.configurarMenu(usuario, mAlm);

            JOptionPane.showMessageDialog(this, "¡Menú actualizado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en el formato del precio. Use solo números y punto decimal.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}