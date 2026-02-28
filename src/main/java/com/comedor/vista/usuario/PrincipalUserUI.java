package com.comedor.vista.usuario;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.comedor.vista.InicioSesionUI;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;

public class PrincipalUserUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen

    private Usuario usuario;
    private BufferedImage backgroundImage;
    // private double saldoActual = 0.0;

    // Constructor por defecto para pruebas
    public PrincipalUserUI() {
        // Constructor por defecto para pruebas (usa un usuario dummy)
        this(new Estudiante("00000000", "1234", "General", "UCV"));
    }

    // Constructor principal que recibe el usuario autenticado
    public PrincipalUserUI(Usuario usuario) {
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

    // Configura las propiedades de la ventana principal
    private void configurarVentana() {
        setTitle("Usuario - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana al abrirse
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }

    // Inicializa y construye todos los componentes de la interfaz
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
                int barHeight = 135;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel contentHost = new JPanel(new GridBagLayout());
        contentHost.setOpaque(false);
        
        JLabel welcomeTitle = new JLabel("Bienvenido al Comedor Universitario", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeTitle.setForeground(Color.WHITE);
        
        JLabel welcomeSub = new JLabel("<html><center>Gestione su saldo y acceda al menú del día<br>desde la barra superior.</center></html>", SwingConstants.CENTER);
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        welcomeSub.setForeground(new Color(230, 230, 230));

        GridBagConstraints gbcHost = new GridBagConstraints();
        gbcHost.gridx = 0; gbcHost.gridy = 0;
        gbcHost.insets = new Insets(0, 0, 20, 0);
        contentHost.add(welcomeTitle, gbcHost);
        
        gbcHost.gridy = 1;
        contentHost.add(welcomeSub, gbcHost);

        JLabel brandLabel = new JLabel("SAGC") {
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
        brandLabel.setFocusable(true);

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new InicioSesionUI().setVisible(true);
                    PrincipalUserUI.this.dispose();
                }
            }
        });

        // --- PESTAÑAS DE FUNCIONALIDADES ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        tabsPanel.setOpaque(false);

        JLabel menuTab = createTabLabel("Menú");
        JLabel historialTab = createTabLabel("Historial");
        JLabel cerrarSesionTab = createTabLabel("Cerrar Sesión");

        menuTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new MenuUserUI(usuario).setVisible(true);
                PrincipalUserUI.this.dispose();
            }
        });

        historialTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Historial
                // new HistorialUI().setVisible(true);
                // MainUserUI.this.dispose();
                JOptionPane.showMessageDialog(PrincipalUserUI.this, 
                    "Funcionalidad no implementada.", 
                    "Historial", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        cerrarSesionTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(PrincipalUserUI.this,
                        "¿Está seguro de que desea cerrar la sesión?",
                        "Confirmar Cierre de Sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    new InicioSesionUI().setVisible(true);
                    PrincipalUserUI.this.dispose();
                }
            }
        });

        tabsPanel.add(menuTab);
        tabsPanel.add(historialTab);
        tabsPanel.add(cerrarSesionTab);

        JPanel topBarContainer = new JPanel(new BorderLayout());
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 135));

        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(300, 135));

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.anchor = GridBagConstraints.WEST;
        gbcLogo.insets = new Insets(0, -28, 0, 0);

        JPanel logoVerticalCenter = new JPanel(new GridBagLayout());
        logoVerticalCenter.setOpaque(false);
        GridBagConstraints gbcLogoCenter = new GridBagConstraints();
        gbcLogoCenter.gridx = 0;
        gbcLogoCenter.gridy = 0;
        gbcLogoCenter.weighty = 1.0;
        gbcLogoCenter.anchor = GridBagConstraints.CENTER;
        logoVerticalCenter.add(brandLabel, gbcLogoCenter);

        logoPanel.add(logoVerticalCenter, gbcLogo);

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

        topBarContainer.add(logoPanel, BorderLayout.WEST);
        topBarContainer.add(tabsContainer, BorderLayout.EAST);

        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);

        JPanel bottomBarContainer = new JPanel(new BorderLayout());
        bottomBarContainer.setOpaque(false);
        bottomBarContainer.setPreferredSize(new Dimension(getWidth(), 135));

        JPanel saldoPanel = new JPanel(new GridBagLayout());
        saldoPanel.setOpaque(false);
        saldoPanel.setPreferredSize(new Dimension(300, 135));

        JLabel iconoMonedero = new JLabel("💰") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("💰")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("💰", x, y);
                g2.dispose();
            }
        };
        iconoMonedero.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
        iconoMonedero.setForeground(Color.WHITE);

        JPanel infoSaldoPanel = new JPanel(new GridBagLayout());
        infoSaldoPanel.setOpaque(false);

        JLabel saldoLabel = new JLabel("Saldo:");
        saldoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saldoLabel.setForeground(new Color(255, 255, 255, 200));

        JLabel montoLabel = new JLabel(String.format("$ %.2f", usuario.obtSaldo()));
        montoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        montoLabel.setForeground(Color.WHITE);

        JLabel recargarLabel = new JLabel("[Click para recargar]");
        recargarLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        recargarLabel.setForeground(new Color(255, 255, 255, 180));
        recargarLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        recargarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Abrir el panel de recarga en una ventana emergente (Pop-up)
                PRecarga panelRecarga = new PRecarga(usuario, () -> {
                    montoLabel.setText(String.format("$ %.2f", usuario.obtSaldo()));
                });

                Object[] options = {"Cerrar"};
                JOptionPane.showOptionDialog(
                    PrincipalUserUI.this,
                    panelRecarga,
                    "Recargar Monedero",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
                );
            }
        });
                //     JOptionPane.QUESTION_MESSAGE
                // );
                
        //         if (input != null && !input.trim().isEmpty()) {
        //             try {
        //                 // Reemplazar coma por punto para parseo
        //                 String inputNormalizado = input.trim().replace(",", ".");
        //                 double montoRecarga = Double.parseDouble(inputNormalizado);
                        
        //                 if (montoRecarga > 0 && montoRecarga <= 10000) { // Límite de 10,000 Bs.
        //                     // Actualizar la variable de saldo
        //                     saldoActual += montoRecarga;
                            
        //                     // Actualizar el label visual
        //                     actualizarDisplaySaldo();
                            
        //                     // Mostrar confirmación
        //                     String montoFormateado = formatSaldo(montoRecarga);
        //                     String nuevoSaldoFormateado = formatSaldo(saldoActual);
                            
        //                     JOptionPane.showMessageDialog(
        //                         MainUserUI.this,
        //                         "¡Recarga exitosa!\n\n" +
        //                         "Monto recargado: " + montoFormateado + "\n" +
        //                         "Nuevo saldo: " + nuevoSaldoFormateado,
        //                         "Recarga Exitosa",
        //                         JOptionPane.INFORMATION_MESSAGE
        //                     );
                            
        //                 } else if (montoRecarga <= 0) {
        //                     mostrarError("Ingrese un monto mayor a cero.");
        //                 } else {
        //                     mostrarError("El monto máximo de recarga es Bs. 10.000,00");
        //                 }
        //             } catch (NumberFormatException ex) {
        //                 mostrarError("Formato inválido. Use números.\nEj: 100.50 o 100,50");
        //             }
        //         }
        //     }
            
        //     private void actualizarDisplaySaldo() {
        //         montoLabel.setText(formatSaldo(saldoActual));
        //     }
            
        //     private String formatSaldo(double monto) {
        //         // Formato: "Bs. 1.250,00"
        //         return String.format("Bs. %,.2f", monto)
        //             .replace(",", "X")
        //             .replace(".", ",")
        //             .replace("X", ".");
        //     }
            
        //     private void mostrarError(String mensaje) {
        //         JOptionPane.showMessageDialog(
        //             MainUserUI.this,
        //             mensaje,
        //             "Error",
        //             JOptionPane.ERROR_MESSAGE
        //         );
        //     }
            
        //     @Override
        //     public void mouseEntered(MouseEvent e) {
        //         recargarLabel.setForeground(Color.WHITE);
        //         recargarLabel.setText("<html><u>Click para recargar</u></html>");
        //     }
            
        //     @Override
        //     public void mouseExited(MouseEvent e) {
        //         recargarLabel.setForeground(new Color(255, 255, 255, 180));
        //         recargarLabel.setText("[Click para recargar]");
        //     }
        // });

        GridBagConstraints gbcSaldo = new GridBagConstraints();
        gbcSaldo.gridx = 0;
        gbcSaldo.gridy = 0;
        gbcSaldo.anchor = GridBagConstraints.WEST;
        gbcSaldo.insets = new Insets(0, 0, 5, 0);
        infoSaldoPanel.add(saldoLabel, gbcSaldo);

        gbcSaldo.gridy = 1;
        gbcSaldo.insets = new Insets(0, 0, 5, 0);
        infoSaldoPanel.add(montoLabel, gbcSaldo);

        gbcSaldo.gridy = 2;
        gbcSaldo.insets = new Insets(0, 0, 0, 0);
        infoSaldoPanel.add(recargarLabel, gbcSaldo);

        JPanel saldoVerticalCenter = new JPanel(new GridBagLayout());
        saldoVerticalCenter.setOpaque(false);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 0;
        gbcCenter.anchor = GridBagConstraints.CENTER;

        JPanel saldoCompletoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        saldoCompletoPanel.setOpaque(false);
        saldoCompletoPanel.add(iconoMonedero);
        saldoCompletoPanel.add(infoSaldoPanel);

        saldoVerticalCenter.add(saldoCompletoPanel, gbcCenter);

        saldoPanel.setBorder(new EmptyBorder(0, 0, 0, 40));
        saldoPanel.add(saldoVerticalCenter);

        bottomBarContainer.add(saldoPanel, BorderLayout.EAST);

        backgroundPanel.add(bottomBarContainer, BorderLayout.SOUTH);

        // --- AGREGAR PANEL DE RECARGA AL CENTRO ---
        // Usamos el contentHost que ya estaba creado pero no añadido
        backgroundPanel.add(contentHost, BorderLayout.CENTER);
    };

    // Crea una etiqueta estilizada para las pestañas de navegación superior
    private JLabel createTabLabel(String text) {
    JLabel tab = new JLabel(text);
    tab.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Aumentado de 18 a 24
    tab.setForeground(Color.WHITE);
    tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    tab.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            tab.setForeground(new Color(255, 255, 255, 220));
            tab.setFont(new Font("Segoe UI", Font.BOLD, 25)); // Efecto hover aumentado
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            tab.setForeground(Color.WHITE);
            tab.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Regresa a tamaño base aumentado
        }
    });
    
    return tab;
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrincipalUserUI().setVisible(true));
    };

};