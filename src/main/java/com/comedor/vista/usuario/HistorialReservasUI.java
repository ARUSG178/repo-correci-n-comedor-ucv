package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Reserva;
import com.comedor.modelo.persistencia.RepoReservas;
import com.comedor.vista.components.SideBarNavigation;
import com.comedor.vista.utils.UIConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialReservasUI extends JFrame {

    private final Usuario usuario;
    private BufferedImage backgroundImage;

    // Colores institucionales
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);
    private static final Color CARD_BG = new Color(0, 0, 0, 140);

    public HistorialReservasUI(Usuario usuario) {
        this.usuario = usuario;
        
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Historial de Reservas - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        JLabel headerTitle = new JLabel("Historial de Reservas", SwingConstants.CENTER);
        headerTitle.setFont(UIConstants.FONT_TITLE_BOLD);
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        SideBarNavigation sideBar = new SideBarNavigation(usuario, () -> {
            new MenuUserUI(usuario).setVisible(true);
            dispose();
        });
        backgroundPanel.add(sideBar, BorderLayout.WEST);

        // Contenido principal
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        scrollPanel.setOpaque(false);
        scrollPanel.setBorder(UIConstants.spacing(UIConstants.SPACING_LG));

        // Obtener reservas reales desde el repositorio
        List<Reserva> reservas = RepoReservas.obtenerReservasPorUsuario(usuario);

        if (reservas.isEmpty()) {
            // Mostrar mensaje cuando no hay reservas
            JLabel lblEmpty = new JLabel("No tienes reservas registradas", SwingConstants.CENTER);
            lblEmpty.setFont(UIConstants.FONT_BODY_NORMAL);
            lblEmpty.setForeground(new Color(200, 200, 200));
            scrollPanel.add(Box.createVerticalGlue());
            scrollPanel.add(lblEmpty);
            scrollPanel.add(Box.createVerticalGlue());
        } else {
            for (Reserva r : reservas) {
                scrollPanel.add(crearTarjetaReserva(r));
                scrollPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
            }
        }

        JScrollPane scroll = new JScrollPane(scrollPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        rightPanel.add(scroll, BorderLayout.CENTER);
        backgroundPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel crearTarjetaReserva(Reserva r) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra sutil
                g2d.setColor(UIConstants.SHADOW_COLOR);
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
                
                // Fondo de la tarjeta
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(UIConstants.CARD_PADDING);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel lblFecha = new JLabel(r.obtHorarioReservado().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), SwingConstants.LEFT);
        lblFecha.setFont(UIConstants.FONT_CARD_SUBTITLE);
        lblFecha.setForeground(Color.WHITE);

        JLabel lblPlatillo = new JLabel("Reserva #" + r.obtClaveAcceso(), SwingConstants.LEFT);
        lblPlatillo.setFont(UIConstants.FONT_BODY_NORMAL);
        lblPlatillo.setForeground(new Color(200, 200, 255));

        JLabel lblMonto = new JLabel("Fecha: " + r.obtHorarioReservado().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), SwingConstants.LEFT);
        lblMonto.setFont(UIConstants.FONT_CARD_SUBTITLE);
        lblMonto.setForeground(new Color(255, 215, 0));

        JLabel lblEstado = new JLabel("Estado: " + r.obtEstado(), SwingConstants.LEFT);
        lblEstado.setFont(UIConstants.FONT_BODY_SMALL);
        lblEstado.setForeground(r.obtEstado().equalsIgnoreCase("Completado") ? new Color(100, 255, 100) : new Color(255, 150, 150));

        card.add(lblFecha);
        card.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        card.add(lblPlatillo);
        card.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        card.add(lblMonto);
        card.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        card.add(lblEstado);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Usuario dummy = new com.comedor.modelo.entidades.Estudiante("123", "123", "Ingeniería", "Ciencias");
            new HistorialReservasUI(dummy).setVisible(true);
        });
    }
}
