package com.comedor.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import com.comedor.controlador.ServicioIS;
import com.comedor.controlador.ServicioRegistro;
import com.comedor.controlador.ServicioFactory;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.DuplicateUserException;
import com.comedor.modelo.excepciones.InvalidCredentialsException;
import com.comedor.vista.admin.PrincipalAdminUI;
import com.comedor.vista.usuario.PrincipalUserUI;

// Interfaz gráfica principal para el inicio de sesión del sistema SAGC UCV
public class InicioSesionUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102); // Azul Institucional
    private static final Color COLOR_FORM_BG = new Color(255, 255, 255); // Blanco
    private static final Color COLOR_INPUT_BG = new Color(0, 85, 170); // Azul más claro para inputs
    private static final Color COLOR_TEXTO = new Color(0, 51, 102); // Texto azul oscuro

    private BufferedImage backgroundImage;
    private ModernTextField txtCedula;
    private ModernPasswordField txtClave;

    private ModernTextField txtCedulaRegistro;
    private ModernTextField txtClaveRegistro;
    private ModernTextField txtCodigoAdminRegistro;

    private final CardLayout authCards = new CardLayout();
    private JPanel authCardsPanel;
    private SegmentedAuthToggle segmentedAuthToggle;
    private ShadowRoundedPanel card;

    // Inicializa la ventana y carga los recursos necesarios
    public InicioSesionUI() {
        this(false);
    }

    public InicioSesionUI(boolean mostrarRegistro) {
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_is_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        configurarVentana();
        initUI(mostrarRegistro);
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

    private void initUI(boolean mostrarRegistro) {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(COLOR_AZUL_INST);

        mainPanel.add(crearPanelIzquierdo());
        mainPanel.add(crearPanelDerecho());

        setContentPane(mainPanel);

        if (mostrarRegistro) {
            mostrarRegistro();
        } else {
            mostrarLogin();
        }
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 51, 102, 180));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g.setColor(COLOR_AZUL_INST);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(new GridBagLayout());

        JLabel lbLogo = new JLabel("SAGC", SwingConstants.CENTER);
        lbLogo.setFont(new Font("Segoe UI", Font.BOLD, 110));
        lbLogo.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Sistema de Asignación y Gestión del Comedor", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 220));
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        lbLogo.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        logoPanel.add(lbLogo);
        logoPanel.add(subtitle);

        panel.add(logoPanel);
        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_AZUL_INST);

        card = new ShadowRoundedPanel(new BorderLayout());
        card.setBackground(COLOR_FORM_BG);
        card.setBorder(new EmptyBorder(18, 22, 22, 22));
        card.setPreferredSize(new Dimension(520, 480));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        segmentedAuthToggle = new SegmentedAuthToggle();
        segmentedAuthToggle.setAlignmentX(CENTER_ALIGNMENT);
        body.add(segmentedAuthToggle);
        body.add(Box.createRigidArea(new Dimension(0, 18)));

        authCardsPanel = new JPanel(authCards);
        authCardsPanel.setOpaque(false);
        authCardsPanel.add(crearFormularioLogin(), "LOGIN");
        authCardsPanel.add(crearFormularioRegistro(), "REGISTRO");
        body.add(authCardsPanel);

        card.add(body, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(card, gbc);

        return panel;
    }

    private JPanel crearFormularioLogin() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtCedula = new ModernTextField("Ej. 12345678");
        txtCedula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        form.add(crearCampo("Cédula:", txtCedula));
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        txtClave = new ModernPasswordField("•••••••••");
        txtClave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        form.add(crearCampo("Contraseña:", txtClave));
        form.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnEntrar = new JButton("Entrar");
        styleButton(btnEntrar);
        btnEntrar.setAlignmentX(CENTER_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(360, 40));
        btnEntrar.addActionListener(e -> ejecutarLogin());
        form.add(btnEntrar);

        return form;
    }

    private JPanel crearFormularioRegistro() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtCedulaRegistro = new ModernTextField("Ej. 12345678");
        form.add(crearCampo("Cédula:", txtCedulaRegistro));
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        txtClaveRegistro = new ModernTextField("Mín. 6 caracteres");
        form.add(crearCampo("Contraseña:", txtClaveRegistro));
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        txtCodigoAdminRegistro = new ModernTextField("Opcional (Solo Admin)");
        form.add(crearCampo("Código Admin:", txtCodigoAdminRegistro));
        form.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnRegistrar = new JButton("Registrar");
        styleButton(btnRegistrar);
        btnRegistrar.setAlignmentX(CENTER_ALIGNMENT);
        btnRegistrar.setMaximumSize(new Dimension(360, 40));
        btnRegistrar.addActionListener(e -> ejecutarRegistro());
        form.add(btnRegistrar);

        return form;
    }

    private JPanel crearCampo(String label, JComponent field) {
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);
        container.setAlignmentX(CENTER_ALIGNMENT);
        container.setMaximumSize(new Dimension(360, 80));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(COLOR_TEXTO);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(l, BorderLayout.NORTH);
        container.add(field, BorderLayout.CENTER);
        return container;
    }

    private void mostrarLogin() {
        authCards.show(authCardsPanel, "LOGIN");
        segmentedAuthToggle.setSelected(Mode.LOGIN);
        actualizarCardPorModo(Mode.LOGIN);
    }

    private void mostrarRegistro() {
        authCards.show(authCardsPanel, "REGISTRO");
        segmentedAuthToggle.setSelected(Mode.REGISTRO);
        actualizarCardPorModo(Mode.REGISTRO);
    }

    private void actualizarCardPorModo(Mode mode) {
        if (card == null) return;
        if (mode == Mode.LOGIN) {
            card.setPreferredSize(new Dimension(520, 360));
        } else {
            card.setPreferredSize(new Dimension(520, 480));
        }
        card.revalidate();
        card.repaint();
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

        ServicioIS servicio = ServicioFactory.getInstance().crearServicioIS();
        
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
                    try {
                        new PrincipalAdminUI(usuarioReal).setVisible(true);
                        this.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al iniciar el panel de Administrador:\n" + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    try {
                        new PrincipalUserUI(usuarioReal).setVisible(true);
                        this.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al iniciar el panel de Usuario:\n" + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            
        } catch (InvalidCredentialsException ex) {
            // Este error ahora incluirá mensajes específicos sobre tipo de usuario
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error accediendo a la base de datos", 
                "Error del Sistema", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarRegistro() {
        String cedula = txtCedulaRegistro.getText().trim();
        String clave = txtClaveRegistro.getText();
        String codigo = txtCodigoAdminRegistro.getText().trim();

        if (cedula.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Los campos Cédula y Contraseña son obligatorios.",
                "Campos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ServicioRegistro servicio = ServicioFactory.getInstance().crearServicioRegistro();
        try {
            servicio.registrarUsuario(cedula, clave, codigo);
            JOptionPane.showMessageDialog(this,
                "Usuario registrado correctamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            mostrarLogin();
        } catch (DuplicateUserException | InvalidCredentialsException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al registrar: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private enum Mode { LOGIN, REGISTRO }

    private class SegmentedAuthToggle extends JPanel {
        private final JLabel left = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        private final JLabel right = new JLabel("Registro", SwingConstants.CENTER);
        private Mode selected = Mode.LOGIN;

        SegmentedAuthToggle() {
            setOpaque(false);
            setLayout(new GridLayout(1, 2));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            configurarLabel(left, Mode.LOGIN);
            configurarLabel(right, Mode.REGISTRO);

            add(left);
            add(right);
            setSelected(Mode.LOGIN);
        }

        private void configurarLabel(JLabel label, Mode mode) {
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.setBorder(new EmptyBorder(14, 10, 14, 10));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (mode == Mode.LOGIN) {
                        mostrarLogin();
                    } else {
                        mostrarRegistro();
                    }
                }
            });
        }

        void setSelected(Mode mode) {
            this.selected = mode;

            Color activeBg = new Color(255, 255, 255);
            Color activeFg = COLOR_AZUL_INST;
            Color inactiveBg = new Color(230, 235, 245);
            Color inactiveFg = new Color(0, 51, 102, 160);

            boolean loginActive = selected == Mode.LOGIN;
            left.setBackground(loginActive ? activeBg : inactiveBg);
            left.setForeground(loginActive ? activeFg : inactiveFg);

            right.setBackground(!loginActive ? activeBg : inactiveBg);
            right.setForeground(!loginActive ? activeFg : inactiveFg);
        }
    }

    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(new Color(0, 123, 255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(15, 0, 15, 0));

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(0, 86, 179));
                b.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(0, 123, 255));
                b.repaint();
            }
        });

        // Override paintComponent para hacer el botón redondeado
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton button = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dibujar fondo redondeado
                g2.setColor(button.getBackground());
                g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 30, 30);
                
                // Dibujar texto
                FontMetrics fm = g2.getFontMetrics();
                int textX = (button.getWidth() - fm.stringWidth(button.getText())) / 2;
                int textY = (button.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(button.getForeground());
                g2.setFont(button.getFont());
                g2.drawString(button.getText(), textX, textY);
                
                g2.dispose();
            }
        });
    }

    private class ModernTextField extends JTextField {
        private final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160);
        private final String hint;

        public ModernTextField(String h) {
            this.hint = h;
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

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
        private final String hint;

        public ModernPasswordField(String h) {
            this.hint = h;
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

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
        public ShadowRoundedPanel(LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillRoundRect(10, 10, getWidth() - 10, getHeight() - 10, 30, 30);
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fillRoundRect(7, 7, getWidth() - 7, getHeight() - 7, 30, 30);
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 30, 30);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 30, 30);
            g2.dispose();
        }
    }
}
