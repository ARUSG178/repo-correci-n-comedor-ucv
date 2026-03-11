package com.comedor.vista.usuario;

import com.comedor.ReconocimientoFacialUI;
import com.comedor.modelo.entidades.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ReservaFechaUI extends JFrame {

    private final Usuario usuario;
    private final double costoPlatillo;
    private JSpinner dateSpinner;

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_FONDO = new Color(245, 245, 250);

    // Constructor que recibe usuario y costo para procesar la reserva
    public ReservaFechaUI(Usuario usuario, double costoPlatillo) {
        this.usuario = usuario;
        this.costoPlatillo = costoPlatillo;
        configurarVentana();
        initUI();
    }

    // Configura las propiedades de la ventana
    private void configurarVentana() {
        setTitle("Programar Reserva - SAGC");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
    }

    // Inicializa los componentes gráficos de la interfaz
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_FONDO);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_AZUL_INST);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel lblTitulo = new JLabel("Fecha de Reserva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);

        JLabel btnVolver = new JLabel("  < Volver");
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Aumentado de 14 a 20
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MenuUserUI(usuario).setVisible(true);
                dispose();
            }
        });
        headerPanel.add(btnVolver, BorderLayout.WEST);

        // --- CONTENIDO ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblInstruccion = new JLabel("Seleccione fecha y hora para su comida:");
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        centerPanel.add(lblInstruccion, gbc);

        // Selector de Fecha y Hora
        SpinnerDateModel model = new SpinnerDateModel();
        model.setValue(new Date()); // Fecha actual por defecto
        dateSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy HH:mm");
        dateSpinner.setEditor(editor);
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateSpinner.setPreferredSize(new Dimension(200, 30));
        
        gbc.gridy = 1;
        centerPanel.add(dateSpinner, gbc);

        JLabel lblCosto = new JLabel(String.format("Costo a pagar: $ %.2f", costoPlatillo));
        lblCosto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCosto.setForeground(new Color(0, 100, 0));
        gbc.gridy = 2;
        centerPanel.add(lblCosto, gbc);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        footerPanel.setOpaque(false);

        JButton btnContinuar = new JButton("Continuar a Verificación");
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnContinuar.setBackground(COLOR_AZUL_INST);
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFocusPainted(false);
        btnContinuar.setPreferredSize(new Dimension(220, 45));
        btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnContinuar.addActionListener(e -> irAReconocimiento());
        
        footerPanel.add(btnContinuar);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Captura la fecha seleccionada y avanza a la pantalla de reconocimiento facial
    private void irAReconocimiento() {
        Date date = (Date) dateSpinner.getValue();
        LocalDateTime fechaReserva = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        // Pasamos al siguiente paso: Reconocimiento Facial
        new ReconocimientoFacialUI(usuario, costoPlatillo, fechaReserva).setVisible(true);
        this.dispose();
    }
}