package com.comedor.vista.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.persistencia.RepoUsuarios;

public class ListaUsuariosUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);

    private BufferedImage backgroundImage;
    private Usuario adminUsuario;

    public ListaUsuariosUI(Usuario adminUsuario) {
        this.adminUsuario = adminUsuario;
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
        setTitle("Lista de Usuarios - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
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
                int barHeight = 160;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // --- BARRA SUPERIOR ---
        JPanel topBarContainer = new JPanel(new BorderLayout());
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 160));
        
        JLabel brandLabel = new JLabel("< SAGC | Usuarios") {
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
        brandLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new PrincipalAdminUI(adminUsuario).setVisible(true);
                dispose();
            }
        });
        
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 45));
        logoPanel.setOpaque(false);
        logoPanel.add(brandLabel);
        topBarContainer.add(logoPanel, BorderLayout.WEST);
        
        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL (TABLA) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 40, 40, 40));

        // Título
        JLabel titleLabel = new JLabel("Usuarios Registrados", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        centerPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla
        String[] columnNames = {"Tipo", "Cédula", "Detalle 1 (Facultad/Cargo)", "Detalle 2 (Carrera/Depto)", "Saldo", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cargarDatosTabla(model);

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        
        // Centrar celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        
        // --- BARRA INFERIOR (Espacio) ---
        JPanel bottomBar = new JPanel();
        bottomBar.setOpaque(false);
        bottomBar.setPreferredSize(new Dimension(getWidth(), 160));
        backgroundPanel.add(bottomBar, BorderLayout.SOUTH);
    }

    private void cargarDatosTabla(DefaultTableModel model) {
        RepoUsuarios repo = new RepoUsuarios();
        try {
            List<Usuario> usuarios = repo.listarUsuarios();
            for (Usuario u : usuarios) {
                String tipo = u.obtTipo();
                String cedula = u.obtCedula();
                String detalle1 = "-";
                String detalle2 = "-";
                String saldo = String.format("$ %.2f", u.obtSaldo());
                String estado = u.obtEstado() ? "Activo" : "Bloqueado";

                if (u instanceof Estudiante) {
                    Estudiante est = (Estudiante) u;
                    detalle1 = est.obtFacultad();
                    detalle2 = est.obtCarrera();
                } else if (u instanceof Empleado) {
                    Empleado emp = (Empleado) u;
                    detalle1 = emp.obtCargo();
                    detalle2 = emp.obtDepartamento();
                } else if (u instanceof Administrador) {
                    detalle1 = "Admin";
                }

                model.addRow(new Object[]{tipo, cedula, detalle1, detalle2, saldo, estado});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}