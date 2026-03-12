package com.comedor.vista.admin;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.vista.components.SideBarNavigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.LocalTime;
import java.util.Properties;
import javax.imageio.ImageIO;

/**
 * UI para que el administrador configure los horarios de turnos
 * Solo permite editar hasta las 12:00 para desayuno y 17:00 para almuerzo
 */
public class ConfigurarTurnosUI extends JFrame {

    private static final String CONFIG_FILE = "turnos_config.properties";
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);

    private final Usuario usuario;
    private BufferedImage backgroundImage;

    private JTextField[] desayunoFields = new JTextField[3];
    private JTextField[] almuerzoFields = new JTextField[3];

    public ConfigurarTurnosUI(Usuario usuario) {
        this.usuario = usuario;

        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }

        configurarVentana();
        initUI();
        cargarConfiguracion();
    }

    private void configurarVentana() {
        setTitle("Configurar Turnos - SAGC UCV");
        setSize(1000, 800);
        setMinimumSize(new Dimension(800, 600));
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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        JLabel headerTitle = new JLabel("Configuración de Turnos", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Sidebar
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

        // Contenido principal
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Panel de Desayuno
        JPanel desayunoPanel = crearPanelTurnos("Desayuno", desayunoFields, "12:00");
        contentPanel.add(desayunoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Panel de Almuerzo
        JPanel almuerzoPanel = crearPanelTurnos("Almuerzo", almuerzoFields, "17:00");
        contentPanel.add(almuerzoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Botón Guardar
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setBackground(new Color(0, 100, 200));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(200, 45));
        btnGuardar.setMaximumSize(new Dimension(200, 45));
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        contentPanel.add(btnGuardar);

        centerPanel.add(contentPanel);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel crearPanelTurnos(String titulo, JTextField[] fields, String horaLimite) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        panel.setBackground(new Color(0, 0, 0, 120));
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        panel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        // Límite máximo de horarios
        String mensajeLimite = "Límite máximo: " + horaLimite;
        JLabel lblLimite = new JLabel(mensajeLimite, SwingConstants.CENTER);
        lblLimite.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLimite.setForeground(new Color(150, 255, 150));
        lblLimite.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblLimite);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Campos de turnos
        for (int i = 0; i < 3; i++) {
            JPanel turnoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            turnoPanel.setOpaque(false);
            turnoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblTurno = new JLabel("Turno " + (i + 1) + ":");
            lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblTurno.setForeground(Color.WHITE);
            lblTurno.setPreferredSize(new Dimension(80, 30));

            fields[i] = new JTextField(15);
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fields[i].setToolTipText("Formato: HH:mm - HH:mm (ej: 07:00 - 08:00)");

            turnoPanel.add(lblTurno);
            turnoPanel.add(fields[i]);
            panel.add(turnoPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Mensaje informativo
        JLabel lblInfo = new JLabel("⚠ Ningún turno puede exceder el límite máximo", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(255, 200, 100));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblInfo);

        return panel;
    }

    private void cargarConfiguracion() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        // Si el archivo no existe, usar valores por defecto
        if (!configFile.exists()) {
            desayunoFields[0].setText("07:00 - 08:00");
            desayunoFields[1].setText("08:00 - 09:00");
            desayunoFields[2].setText("09:00 - 10:00");
            almuerzoFields[0].setText("12:00 - 13:00");
            almuerzoFields[1].setText("13:00 - 14:00");
            almuerzoFields[2].setText("14:00 - 15:00");
            return;
        }

        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in);

            desayunoFields[0].setText(props.getProperty("desayuno.turno1", "07:00 - 08:00"));
            desayunoFields[1].setText(props.getProperty("desayuno.turno2", "08:00 - 09:00"));
            desayunoFields[2].setText(props.getProperty("desayuno.turno3", "09:00 - 10:00"));

            almuerzoFields[0].setText(props.getProperty("almuerzo.turno1", "12:00 - 13:00"));
            almuerzoFields[1].setText(props.getProperty("almuerzo.turno2", "13:00 - 14:00"));
            almuerzoFields[2].setText(props.getProperty("almuerzo.turno3", "14:00 - 15:00"));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error cargando configuración: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarConfiguracion() {
        // Validar formato de horarios
        for (int i = 0; i < 3; i++) {
            if (!validarFormatoHorario(desayunoFields[i].getText())) {
                JOptionPane.showMessageDialog(this,
                    "Formato inválido en Desayuno Turno " + (i + 1) + "\nUse: HH:mm - HH:mm",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!validarFormatoHorario(almuerzoFields[i].getText())) {
                JOptionPane.showMessageDialog(this,
                    "Formato inválido en Almuerzo Turno " + (i + 1) + "\nUse: HH:mm - HH:mm",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Validar límites máximos de hora
        if (!validarLimitesMaximos()) {
            return;
        }

        // Validar que no se repitan horas
        if (!validarHorasUnicas()) {
            return;
        }

        // Guardar configuración
        Properties props = new Properties();
        props.setProperty("desayuno.turno1", desayunoFields[0].getText());
        props.setProperty("desayuno.turno2", desayunoFields[1].getText());
        props.setProperty("desayuno.turno3", desayunoFields[2].getText());
        props.setProperty("almuerzo.turno1", almuerzoFields[0].getText());
        props.setProperty("almuerzo.turno2", almuerzoFields[1].getText());
        props.setProperty("almuerzo.turno3", almuerzoFields[2].getText());
        props.setProperty("limite.configuracion.desayuno", "12:00");
        props.setProperty("limite.configuracion.almuerzo", "17:00");

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Configuración de Turnos del Comedor - Actualizado: " + java.time.LocalDateTime.now());
            JOptionPane.showMessageDialog(this,
                "Configuración guardada exitosamente.\nLos cambios se aplicarán inmediatamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error guardando configuración: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarLimitesMaximos() {
        // Límite máximo: 12:00 para desayuno, 17:00 para almuerzo
        LocalTime limiteDesayuno = LocalTime.parse("12:00");
        LocalTime limiteAlmuerzo = LocalTime.parse("17:00");

        for (int i = 0; i < 3; i++) {
            // Validar límite para desayuno
            String[] partesDesayuno = desayunoFields[i].getText().split(" - ");
            if (partesDesayuno.length == 2) {
                LocalTime horaInicioDesayuno = LocalTime.parse(partesDesayuno[0].trim());
                LocalTime horaFinDesayuno = LocalTime.parse(partesDesayuno[1].trim());
                
                if (horaInicioDesayuno.isAfter(limiteDesayuno) || horaFinDesayuno.isAfter(limiteDesayuno)) {
                    JOptionPane.showMessageDialog(this,
                        "Error en Desayuno Turno " + (i + 1) + ":\nLas horas no pueden ser después de las 12:00",
                        "Límite Máximo", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Validar límite para almuerzo
            String[] partesAlmuerzo = almuerzoFields[i].getText().split(" - ");
            if (partesAlmuerzo.length == 2) {
                LocalTime horaInicioAlmuerzo = LocalTime.parse(partesAlmuerzo[0].trim());
                LocalTime horaFinAlmuerzo = LocalTime.parse(partesAlmuerzo[1].trim());
                
                if (horaInicioAlmuerzo.isAfter(limiteAlmuerzo) || horaFinAlmuerzo.isAfter(limiteAlmuerzo)) {
                    JOptionPane.showMessageDialog(this,
                        "Error en Almuerzo Turno " + (i + 1) + ":\nLas horas no pueden ser después de las 17:00",
                        "Límite Máximo", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validarHorasUnicas() {
        // Recolectar todas las horas de inicio y fin
        java.util.Set<String> horasUsadas = new java.util.HashSet<>();
        
        // Validar desayuno
        for (int i = 0; i < 3; i++) {
            String[] partes = desayunoFields[i].getText().split(" - ");
            if (partes.length == 2) {
                String horaInicio = partes[0].trim();
                String horaFin = partes[1].trim();
                
                if (horasUsadas.contains(horaInicio)) {
                    JOptionPane.showMessageDialog(this,
                        "Error en Desayuno Turno " + (i + 1) + ":\nLa hora de inicio '" + horaInicio + "' ya está siendo usada en otro turno",
                        "Horas Repetidas", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (horasUsadas.contains(horaFin)) {
                    JOptionPane.showMessageDialog(this,
                        "Error en Desayuno Turno " + (i + 1) + ":\nLa hora de fin '" + horaFin + "' ya está siendo usada en otro turno",
                        "Horas Repetidas", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                horasUsadas.add(horaInicio);
                horasUsadas.add(horaFin);
            }
        }
        
        // Validar almuerzo
        for (int i = 0; i < 3; i++) {
            String[] partes = almuerzoFields[i].getText().split(" - ");
            if (partes.length == 2) {
                String horaInicio = partes[0].trim();
                String horaFin = partes[1].trim();
                
                if (horasUsadas.contains(horaInicio)) {
                    JOptionPane.showMessageDialog(this,
                        "Error en Almuerzo Turno " + (i + 1) + ":\nLa hora de inicio '" + horaInicio + "' ya está siendo usada en otro turno",
                        "Horas Repetidas", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (horasUsadas.contains(horaFin)) {
                    JOptionPane.showMessageDialog(this,
                        "Error en Almuerzo Turno " + (i + 1) + ":\nLa hora de fin '" + horaFin + "' ya está siendo usada en otro turno",
                        "Horas Repetidas", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                horasUsadas.add(horaInicio);
                horasUsadas.add(horaFin);
            }
        }
        
        return true;
    }

    private boolean validarFormatoHorario(String horario) {
        if (horario == null || horario.trim().isEmpty()) {
            return false;
        }
        // Validar formato HH:mm - HH:mm
        return horario.matches("\\d{2}:\\d{2}\\s+-\\s+\\d{2}:\\d{2}");
    }
}
