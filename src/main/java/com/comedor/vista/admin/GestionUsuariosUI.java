package com.comedor.vista.admin;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.utilidades.Logger;
import com.comedor.vista.components.SideBarNavigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);

    private final Usuario usuarioAdmin;
    private List<Usuario> usuarios = new ArrayList<>();

    private DefaultTableModel model;
    private JTable table;

    public GestionUsuariosUI(Usuario usuarioAdmin) {
        this.usuarioAdmin = usuarioAdmin;
        configurarVentana();
        initUI();
        cargarUsuarios();
    }

    private void configurarVentana() {
        setTitle("Gestionar Usuarios - SAGC UCV");
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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel title = new JLabel("Gestionar Usuarios", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        SideBarNavigation sideBar = new SideBarNavigation(usuarioAdmin, () -> {
            try {
                new PrincipalAdminUI(usuarioAdmin).setVisible(true);
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error al volver al panel principal:\n" + e.getMessage(),
                    "Error de Navegación", JOptionPane.ERROR_MESSAGE);
            }
        });
        backgroundPanel.add(sideBar, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(20, 20, 40, 20));

        model = new DefaultTableModel(new Object[]{"Tipo", "Cédula", "Nombre", "Estado", "Saldo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0, 81, 132), 2));
        rightPanel.add(sp, BorderLayout.CENTER);

        JButton btnToggleEstado = new JButton("Activar/Desactivar");
        btnToggleEstado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnToggleEstado.setBackground(new Color(0, 81, 132));
        btnToggleEstado.setForeground(Color.WHITE);
        btnToggleEstado.setFocusPainted(false);
        btnToggleEstado.setOpaque(true);
        btnToggleEstado.setContentAreaFilled(true);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setBackground(new Color(0, 100, 0));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setContentAreaFilled(true);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actions.setOpaque(false);
        actions.add(btnToggleEstado);
        actions.add(btnGuardar);
        rightPanel.add(actions, BorderLayout.SOUTH);

        btnToggleEstado.addActionListener(e -> toggleEstadoSeleccionado());
        btnGuardar.addActionListener(e -> guardarCambios());

        backgroundPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private void cargarUsuarios() {
        RepoUsuarios repo = new RepoUsuarios();
        try {
            usuarios = repo.listarUsuarios();
        } catch (IOException e) {
            Logger.error("Error cargando usuarios", e);
            usuarios = new ArrayList<>();
        }

        model.setRowCount(0);
        for (Usuario u : usuarios) {
            model.addRow(new Object[]{
                    u.obtTipo(),
                    u.obtCedula(),
                    u.obtNombre(),
                    u.obtEstado() ? "Activo" : "Inactivo",
                    String.format("$ %.2f", u.obtSaldo())
            });
        }
    }

    private void toggleEstadoSeleccionado() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= usuarios.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Gestión de usuarios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Usuario u = usuarios.get(row);
        u.setEstado(!u.obtEstado());
        model.setValueAt(u.obtEstado() ? "Activo" : "Inactivo", row, 3);
    }

    private void guardarCambios() {
        RepoUsuarios repo = new RepoUsuarios();
        try {
            repo.guardarTodos(usuarios);
            JOptionPane.showMessageDialog(this, "Cambios guardados.", "Gestión de usuarios", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            Logger.error("Error guardando usuarios", e);
            JOptionPane.showMessageDialog(this, "No se pudo guardar.", "Gestión de usuarios", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        com.comedor.modelo.entidades.Usuario adminDummy = new com.comedor.modelo.entidades.Administrador("0", "admin", "0");
        SwingUtilities.invokeLater(() -> new GestionUsuariosUI(adminDummy).setVisible(true));
    }
}
