package com.comedor.vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.comedor.controlador.ServicioRegistro;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.DuplicateUserException;
import com.comedor.modelo.excepciones.InvalidCredentialsException;

// Interfaz gráfica para el registro de usuarios del sistema SAGC UCV
public class RegistroUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen
    private static final Color COLOR_FORM_BG = new Color(255, 255, 255);            // Fondo blanco
    private static final Color COLOR_INPUT_BG = new Color(0, 85, 170);            // Fondo azul claro inputs
    private static final Color COLOR_RADIO_BLUE = new Color(0, 102, 204);          // Azul para los Radios
    private static final Color COLOR_BTN_AZUL = new Color(0, 60, 120);            // Botón Registrar
    private static final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160); // Texto fantasma

    private BufferedImage backgroundImage;
    private CardLayout cardLayout;
    private JPanel specificFieldsPanel;

    private ModernTextField txtCedula;
    private ModernTextField txtContrasena;

    private ModernTextField txtFacultad;
    private ModernTextField txtCarrera;

    private JRadioButton studentRadio;
    private JRadioButton employeeRadio;
    private JRadioButton adminRadio;

    private ModernTextField txtCargo;
    private ModernTextField txtDepartamento;
    private ModernTextField txtAdminCodigo;

    // Inicializa la ventana de registro y carga los recursos
    public RegistroUI() {
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }

    // Configura las propiedades básicas de la ventana
    private void configurarVentana() {
        setTitle("Registro de Usuario - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Centra la ventana al abrirse 
        setLocationRelativeTo(null); 
        // Inicia en pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }

    // Construye la interfaz gráfica completa
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

                // Barras sólidas superior e inferior
                g2d.setColor(COLOR_AZUL_INST);
                int barHeight = 135;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        backgroundPanel.add(crearBarraSuperior(), BorderLayout.NORTH);

        JPanel contentHost = new JPanel(new GridBagLayout());
        contentHost.setOpaque(false);
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridy = 1; gbcMain.weighty = 1.0; gbcMain.fill = GridBagConstraints.BOTH;
        contentHost.add(crearFormularioCentral(), gbcMain);

        JScrollPane scroll = new JScrollPane(contentHost);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        backgroundPanel.add(scroll, BorderLayout.CENTER);
    }

    // Crea la barra superior con el logo y navegación
    private JPanel crearBarraSuperior() {
        JPanel contentHost = new JPanel(new GridBagLayout());
        contentHost.setOpaque(false);
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
        brandLabel.setFocusable(true);

        brandLabel.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new InicioSesionUI().setVisible(true);
                RegistroUI.this.dispose();
            }
        });

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new InicioSesionUI().setVisible(true);
                    RegistroUI.this.dispose();
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

        return topBarContainer;
    }

    // Crea el panel central que contiene el formulario de registro
    private JPanel crearFormularioCentral() {
        JPanel centeringSpace = new JPanel(new GridBagLayout());
        centeringSpace.setOpaque(false);
        
        JPanel formCard = new ShadowRoundedPanel(new GridBagLayout());
        formCard.setBackground(COLOR_FORM_BG);
        formCard.setBorder(new EmptyBorder(40, 60, 45, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.insets = new Insets(8, 0, 8, 0);

        JLabel titleLabel = new JLabel("Registrarse");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(COLOR_AZUL_INST);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 25, 0);
        formCard.add(titleLabel, gbc);

        txtCedula = new ModernTextField("Ej. 12345678");
        // Validación: Solo permitir números
        txtCedula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        addLabelAndField(formCard, "Cédula:", txtCedula, gbc, 1);

        txtContrasena = new ModernTextField("Mín. 6 caracteres");
        addLabelAndField(formCard, "Contraseña:", txtContrasena, gbc, 2);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        radioPanel.setOpaque(false);
        studentRadio = createCustomRadio("Estudiante");
        employeeRadio = createCustomRadio("Empleado");
        adminRadio = createCustomRadio("Administrador");
        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio); group.add(employeeRadio); group.add(adminRadio);
        studentRadio.setSelected(true);
        radioPanel.add(studentRadio); radioPanel.add(employeeRadio); radioPanel.add(adminRadio);
        gbc.gridy = 3; gbc.insets = new Insets(15, 0, 15, 0);
        formCard.add(radioPanel, gbc);

        cardLayout = new CardLayout();
        specificFieldsPanel = new JPanel(cardLayout);
        specificFieldsPanel.setOpaque(false);

        JPanel pEst = new JPanel(new GridBagLayout()); pEst.setOpaque(false);
        GridBagConstraints gbcSub = new GridBagConstraints();
        gbcSub.fill = GridBagConstraints.HORIZONTAL; gbcSub.weightx = 1.0; gbcSub.gridx = 0;
        txtFacultad = new ModernTextField("Ej. Ciencias");
        addLabelAndField(pEst, "Facultad:", txtFacultad, gbcSub, 0);
        txtCarrera = new ModernTextField("Ej. Computación");
        addLabelAndField(pEst, "Carrera:", txtCarrera, gbcSub, 1);

        JPanel pEmp = new JPanel(new GridBagLayout()); pEmp.setOpaque(false);
        txtCargo = new ModernTextField("Ej. Profesor");
        addLabelAndField(pEmp, "Cargo:", txtCargo, gbcSub, 0);
        txtDepartamento = new ModernTextField("Ej. Docencia");
        addLabelAndField(pEmp, "Departamento:", txtDepartamento, gbcSub, 1);

        JPanel pAdm = new JPanel(new GridBagLayout()); pAdm.setOpaque(false);
        txtAdminCodigo = new ModernTextField("8 caracteres alfanum.");
        addLabelAndField(pAdm, "Código Admin:", txtAdminCodigo, gbcSub, 0);

        specificFieldsPanel.add(pEst, "Estudiante");
        specificFieldsPanel.add(pEmp, "Empleado");
        specificFieldsPanel.add(pAdm, "Administrador");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 25, 0);
        formCard.add(specificFieldsPanel, gbc);

        JButton btnReg = new JButton("REGISTRAR");
        styleButton(btnReg);
        btnReg.addActionListener(e -> ejecutarRegistro());
        gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        formCard.add(btnReg, gbc);

        centeringSpace.add(formCard, new GridBagConstraints());

        studentRadio.addActionListener(e -> cardLayout.show(specificFieldsPanel, "Estudiante"));
        employeeRadio.addActionListener(e -> cardLayout.show(specificFieldsPanel, "Empleado"));
        adminRadio.addActionListener(e -> cardLayout.show(specificFieldsPanel, "Administrador"));

        return centeringSpace;
    }

    // Ejecuta la lógica de validación y registro del usuario
    private void ejecutarRegistro() {
        String cedula = txtCedula.getText().trim();
        String contr = txtContrasena.getText();

        if (cedula.isEmpty() || contr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los campos Cédula y Contraseña son obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario nuevo = null;
        if (studentRadio.isSelected()) {
            String carrera = txtCarrera.getText().trim();
            String facultad = txtFacultad.getText().trim();
            if (facultad.isEmpty() || carrera.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Para registrarse como Estudiante, los campos Facultad y Carrera son obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevo = new Estudiante(cedula, contr, carrera, facultad);
        } else if (employeeRadio.isSelected()) {
            String cargo = txtCargo.getText().trim();
            String departamento = txtDepartamento.getText().trim();
            if (cargo.isEmpty() || departamento.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Para registrarse como Empleado, los campos Cargo y Departamento son obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevo = new Empleado(cedula, contr, cargo, departamento, "0000");
        } else { 
            String codigo = txtAdminCodigo.getText().trim();
            if (codigo.isEmpty() || !codigo.matches("[A-Za-z0-9]{8}")) {
                JOptionPane.showMessageDialog(this, "El Código de Administrador es obligatorio y debe tener 8 caracteres alfanuméricos.", "Código Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevo = new Administrador(cedula, contr, codigo);
        }

        ServicioRegistro servicio = new ServicioRegistro();
        try {
            servicio.registrarUsuario(nuevo);
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                new InicioSesionUI().setVisible(true);
                dispose();
            });
        } catch (DuplicateUserException | InvalidCredentialsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar en disco: " + ex.getMessage(), "Error IO", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clase interna para campos de texto con estilo moderno
    private class ModernTextField extends JTextField {
        private String hint;
        public ModernTextField(String h) { 
            this.hint = h; setOpaque(false); setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            addKeyListener(new KeyAdapter() { @Override public void keyReleased(KeyEvent e) { repaint(); } });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            if (getText().isEmpty()) { // El texto fantasma solo se pinta si no hay texto
                g2.setColor(COLOR_PLACEHOLDER);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(hint, 15, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
            }
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // Crea un botón de radio personalizado
    private JRadioButton createCustomRadio(String texto) {
        JRadioButton radio = new JRadioButton(texto);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radio.setForeground(COLOR_AZUL_INST);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        radio.setIcon(new CustomRadioIcon(false));
        radio.setSelectedIcon(new CustomRadioIcon(true));
        return radio;
    }

    // Icono personalizado para los botones de radio
    private static class CustomRadioIcon implements Icon {
        private boolean sel;
        public CustomRadioIcon(boolean sel) { this.sel = sel; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_RADIO_BLUE);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(x, y + 2, 16, 16); // Círculo exterior
            if (sel) g2.fillOval(x + 4, y + 6, 9, 9); // Punto interior
            g2.dispose();
        }
        @Override public int getIconWidth() { return 25; }
        @Override public int getIconHeight() { return 20; }
    }

    // Panel con bordes redondeados y sombra
    private class ShadowRoundedPanel extends JPanel {
        public ShadowRoundedPanel(LayoutManager lm) { super(lm); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 60)); // Sombra
            g2.fillRoundRect(6, 6, getWidth()-6, getHeight()-6, 30, 30);
            g2.setColor(getBackground()); // Fondo crema
            g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 30, 30);
            g2.dispose();
        }
    }

    // Agrega una etiqueta y un campo de texto al panel
    private void addLabelAndField(JPanel p, String t, JComponent f, GridBagConstraints g, int y) {
        g.gridy = y;
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(COLOR_AZUL_INST);
        JPanel c = new JPanel(new BorderLayout(0, 5)); c.setOpaque(false);
        c.add(l, BorderLayout.NORTH); c.add(f, BorderLayout.CENTER);
        p.add(c, g);
    }

    // Aplica estilo visual a los botones
    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(COLOR_BTN_AZUL); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(10, 30, 10, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Punto de entrada para pruebas de la interfaz
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistroUI().setVisible(true));
    }
}