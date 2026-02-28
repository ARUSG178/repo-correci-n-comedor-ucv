package com.comedor.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.comedor.controlador.ServicioIS;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.InvalidCredentialsException;
import com.comedor.vista.admin.PrincipalAdminUI;
import com.comedor.vista.usuario.PrincipalUserUI;

// Interfaz gráfica principal para el inicio de sesión del sistema SAGC UCV
public class InicioSesionUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102); // Azul Institucional
    private static final Color COLOR_FORM_BG = new Color(255, 255, 255); // Blanco
    private static final Color COLOR_INPUT_BG = new Color(0, 85, 170); // Azul más claro para inputs
    private static final Color COLOR_BTN_AZUL = new Color(0, 60, 120); // Azul botón
    private static final Color COLOR_TEXTO = new Color(0, 51, 102); // Texto azul oscuro

    private BufferedImage backgroundImage;
    private ModernTextField txtCedula;
    private ModernPasswordField txtClave;

    // Inicializa la ventana y carga los recursos necesarios
    public InicioSesionUI() {
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_is_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }

        configurarVentana();
        initUI();
    }

    // Configura las propiedades básicas de la ventana principal
    private void configurarVentana() {
        setTitle("Iniciar Sesión - SAGC UCV"); 
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);    
    }

    // Construye la interfaz gráfica dividida en dos paneles principales
    private void initUI() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(COLOR_AZUL_INST);

        mainPanel.add(crearPanelIzquierdo());
        mainPanel.add(crearPanelDerecho());
        setContentPane(mainPanel);
    }

    // Crea el panel izquierdo con la imagen de fondo y el logo institucional
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 51, 102, 180)); // Filtro azul
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(new GridBagLayout());
        
        JLabel lbLogo = new JLabel("SAGC", SwingConstants.CENTER);
        lbLogo.setFont(new Font("Segoe UI", Font.BOLD, 100));
        lbLogo.setForeground(Color.WHITE);
        lbLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbLogo.setFocusable(true);
        lbLogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                new RegistroUI().setVisible(true);
                InicioSesionUI.this.dispose();
            }
        });

        JLabel subtitle = new JLabel("Sistema de Asignación y Gestión del Comedor", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(lbLogo, BorderLayout.CENTER);
        logoPanel.add(subtitle, BorderLayout.SOUTH);
        panel.add(logoPanel);
        return panel;
    }

    // Crea el panel derecho con el formulario de inicio de sesión
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_AZUL_INST);

        ShadowRoundedPanel card = new ShadowRoundedPanel(new GridBagLayout());
        card.setBackground(COLOR_FORM_BG);
        card.setBorder(new EmptyBorder(50, 50, 50, 50));
        card.setPreferredSize(new Dimension(450, 550)); // Ajustado

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Iniciar Sesión");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(COLOR_AZUL_INST);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 30, 0);
        card.add(title, gbc);

        txtCedula = new ModernTextField("Ej. 12345678");
        addLabelAndField(card, "Cédula:", txtCedula, gbc, 1);

        txtClave = new ModernPasswordField("••••••••");
        addLabelAndField(card, "Contraseña:", txtClave, gbc, 2);

        JButton btnEntrar = new JButton("Entrar");
        styleButton(btnEntrar);
        btnEntrar.addActionListener(e -> ejecutarLogin());
        
        gbc.gridy = 3; gbc.insets = new Insets(40, 0, 10, 0);
        card.add(btnEntrar, gbc);

        JButton btnIrRegistro = new JButton("¿No tienes cuenta? Regístrate");
        btnIrRegistro.setContentAreaFilled(false);
        btnIrRegistro.setBorderPainted(false);
        btnIrRegistro.setForeground(COLOR_AZUL_INST);
        btnIrRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIrRegistro.addActionListener(e -> {
            new RegistroUI().setVisible(true);
            this.dispose();
        });
        gbc.gridy = 4;
        card.add(btnIrRegistro, gbc);

        panel.add(card);
        return panel;
    }

    // Ejecuta la lógica de autenticación al presionar el botón Entrar
    private void ejecutarLogin() {
        String cedula = txtCedula.getText().trim();
        String clave = new String(txtClave.getPassword());
        
        if (cedula.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese correo y contraseña", 
                "Datos incompletos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ServicioIS servicio = new ServicioIS();
        
        try {
            // Creamos un usuario genérico (Estudiante) solo para transportar las credenciales
            Usuario usuarioIngresado = new Estudiante(cedula, clave, "", "");
            
            Usuario usuarioReal = servicio.IniciarSesion(usuarioIngresado);
            
            JOptionPane.showMessageDialog(this, 
                "¡Bienvenido al sistema!", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            if (usuarioReal instanceof Administrador) {
                SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    new PrincipalAdminUI(usuarioReal).setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    new PrincipalUserUI(usuarioReal).setVisible(true);
                });
            }
            
        } catch (InvalidCredentialsException ex) {
            // Este error ahora incluirá mensajes específicos sobre tipo de usuario
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        } catch (java.io.IOException ioEx) {
            JOptionPane.showMessageDialog(this, 
                "Error accediendo a la base de datos", 
                "Error del Sistema", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Agrega una etiqueta y un campo de texto al panel especificado
    private void addLabelAndField(JPanel p, String t, JComponent f, GridBagConstraints g, int y) {
        g.gridy = y;
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(COLOR_TEXTO);
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);
        container.add(l, BorderLayout.NORTH);
        container.add(f, BorderLayout.CENTER);
        p.add(container, g);
    }

    // Aplica el estilo visual estándar a los botones de acción
    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setBackground(COLOR_BTN_AZUL);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 0, 12, 0));
    }

    private class ModernTextField extends JTextField {
        private final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160);
        private String hint;
        
        public ModernTextField(String h) { 
            this.hint = h; 
            setOpaque(false); 
            setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); 
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            
            // Texto placeholder si está vacío
            if (getText().isEmpty()) {
                g2.setColor(COLOR_PLACEHOLDER);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint, 15, y);
            }
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private class ModernPasswordField extends JPasswordField {
        private final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160);
        private String hint;
        
        public ModernPasswordField(String h) { 
            this.hint = h;
            setOpaque(false); 
            setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); 
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            
            // Texto placeholder si está vacío
            if (getPassword().length == 0) {
                g2.setColor(COLOR_PLACEHOLDER);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint, 15, y);
            }
            
            super.paintComponent(g);
            g2.dispose();
        }
    }


    private class ShadowRoundedPanel extends JPanel {
        public ShadowRoundedPanel(LayoutManager lm) { super(lm); setOpaque(false); }
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, 30, 30);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 30, 30);
            g2.dispose();
        }
    }
}// fastidio los commits git se bugea