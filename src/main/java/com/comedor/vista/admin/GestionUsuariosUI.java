package com.comedor.vista.admin;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.EstudianteBecario;
import com.comedor.modelo.entidades.EstudianteExonerado;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.modelo.persistencia.RepoSecretaria;
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
                JOptionPane.showMessageDialog(this, "Error al volver al panel principal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backgroundPanel.add(sideBar, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(20, 20, 40, 20));

        model = new DefaultTableModel(new Object[]{"Tipo", "Cédula", "Nombre", "Estado", "Saldo", "Descuento"}, 0) {
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

        JButton btnBecario = new JButton("Agregar Becario");
        btnBecario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBecario.setBackground(new Color(0, 100, 200));
        btnBecario.setForeground(Color.WHITE);
        btnBecario.setFocusPainted(false);
        btnBecario.setOpaque(true);
        btnBecario.setContentAreaFilled(true);

        JButton btnExonerado = new JButton("Agregar Exonerado");
        btnExonerado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnExonerado.setBackground(new Color(100, 50, 150));
        btnExonerado.setForeground(Color.WHITE);
        btnExonerado.setFocusPainted(false);
        btnExonerado.setOpaque(true);
        btnExonerado.setContentAreaFilled(true);

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
        actions.add(btnBecario);
        actions.add(btnExonerado);
        actions.add(btnGuardar);
        rightPanel.add(actions, BorderLayout.SOUTH);

        btnToggleEstado.addActionListener(e -> toggleEstadoSeleccionado());
        btnBecario.addActionListener(e -> agregarBecario());
        btnExonerado.addActionListener(e -> agregarExonerado());
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
            String descuento = obtenerDescuento(u);
            model.addRow(new Object[]{
                    u.obtTipo(),
                    u.obtCedula(),
                    u.obtNombre(),
                    u.obtEstado() ? "Activo" : "Inactivo",
                    String.format("$ %.2f", u.obtSaldo()),
                    descuento
            });
        }
    }

    private String obtenerDescuento(Usuario u) {
        if (u instanceof EstudianteBecario) {
            EstudianteBecario becario = (EstudianteBecario) u;
            return becario.obtPorcentajeDescuento() + "%";
        } else if (u instanceof EstudianteExonerado) {
            return "100% (Exonerado)";
        } else {
            return "N/A";
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
        model.setValueAt(obtenerDescuento(u), row, 5); // Actualizar columna de descuento
        
        // Guardar automáticamente el cambio de estado
        RepoUsuarios repo = new RepoUsuarios();
        try {
            repo.guardarTodos(usuarios);
            JOptionPane.showMessageDialog(this, 
                "Estado del usuario actualizado a " + (u.obtEstado() ? "Activo" : "Inactivo"), 
                "Gestión de usuarios", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            Logger.error("Error guardando cambios de estado", e);
            JOptionPane.showMessageDialog(this, 
                "No se pudo guardar el cambio de estado.", 
                "Gestión de usuarios", JOptionPane.ERROR_MESSAGE);
            // Revertir el cambio si no se pudo guardar
            u.setEstado(!u.obtEstado());
            model.setValueAt(u.obtEstado() ? "Activo" : "Inactivo", row, 3);
            model.setValueAt(obtenerDescuento(u), row, 5); // Revertir columna de descuento
        }
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

    private void agregarBecario() {
        // Pedir CI del estudiante
        String cedula = JOptionPane.showInputDialog(this, 
            "Ingrese la cédula del estudiante:", 
            "Agregar Becario", JOptionPane.QUESTION_MESSAGE);
        
        if (cedula == null || cedula.trim().isEmpty()) {
            return;
        }
        
        cedula = cedula.trim();
        
        // VALIDAR: Buscar en Secretaría UCV primero
        Usuario datosSecretaria = null;
        try {
            RepoSecretaria repoSecretaria = new RepoSecretaria();
            datosSecretaria = repoSecretaria.buscarRegistroUCV(cedula);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al consultar secretaría: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Si NO existe en secretaría, NO se crea
        if (datosSecretaria == null) {
            JOptionPane.showMessageDialog(this, 
                "El estudiante con CI " + cedula + " no está registrado en la Secretaría UCV.\n" +
                "No se puede agregar como becario.", 
                "Estudiante No Encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que sea un estudiante (no empleado ni profesor)
        if (!(datosSecretaria instanceof Estudiante)) {
            JOptionPane.showMessageDialog(this, 
                "El usuario con CI " + cedula + " no es un estudiante.\n" +
                "Tipo encontrado: " + datosSecretaria.obtTipo(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obtener datos de secretaría
        String carrera = ((Estudiante) datosSecretaria).obtCarrera();
        String facultad = ((Estudiante) datosSecretaria).obtFacultad();
        String nombre = datosSecretaria.obtNombre();
        
        // Verificar si ya existe en el sistema de usuarios
        Usuario usuarioExistente = null;
        int indexExistente = -1;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).obtCedula().equals(cedula)) {
                usuarioExistente = usuarios.get(i);
                indexExistente = i;
                break;
            }
        }
        
        // Pedir % de descuento
        String inputDescuento = JOptionPane.showInputDialog(this, 
            "Estudiante: " + nombre + "\n" +
            "Carrera: " + carrera + "\n\n" +
            "Ingrese el % de descuento del becario:\n" +
            "(Ejemplo: 95 = paga solo el 5% del precio)", 
            "95");
        
        if (inputDescuento == null || inputDescuento.trim().isEmpty()) {
            return;
        }
        
        double porcentajeDescuento;
        try {
            porcentajeDescuento = Double.parseDouble(inputDescuento.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Porcentaje inválido.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validación: % becario debe ser > 80% (mayor descuento que regular de 80%)
        if (porcentajeDescuento <= 80.0) {
            JOptionPane.showMessageDialog(this, 
                "El % de descuento debe ser mayor al 80%\n" +
                "(Un becario debe tener mayor beneficio que un estudiante regular\n" +
                "que tiene 80% de descuento)", 
                "Validación Fallida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (porcentajeDescuento > 100.0) {
            JOptionPane.showMessageDialog(this, 
                "El % de descuento no puede ser mayor al 100%.", 
                "Validación Fallida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear becario con datos de secretaría
        EstudianteBecario becario = new EstudianteBecario(
            cedula, 
            usuarioExistente != null ? usuarioExistente.obtContraseña() : cedula,
            carrera,
            facultad,
            porcentajeDescuento
        );
        becario.setNombre(nombre);
        
        // Copiar datos si existía en usuarios
        if (usuarioExistente != null) {
            becario.setEstado(usuarioExistente.obtEstado());
            becario.setSaldo(usuarioExistente.obtSaldo());
            usuarios.set(indexExistente, becario);
        } else {
            becario.setEstado(true);
            becario.setSaldo(0.0);
            usuarios.add(becario);
        }
        
        // Guardar en archivo usuarios.txt inmediatamente
        RepoUsuarios repo = new RepoUsuarios();
        try {
            repo.guardarTodos(usuarios);
        } catch (IOException e) {
            Logger.error("Error guardando becario en usuarios.txt", e);
            JOptionPane.showMessageDialog(this, 
                "Error al guardar en archivo: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Guardar historial
        guardarHistorial("BECARIO", cedula, porcentajeDescuento + "% descuento");
        
        // Recargar tabla desde archivo
        cargarUsuarios();
        
        JOptionPane.showMessageDialog(this, 
            "Estudiante becario agregado exitosamente.\n" +
            "Nombre: " + nombre + "\n" +
            "CI: " + cedula + "\n" +
            "Descuento: " + porcentajeDescuento + "%", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void agregarExonerado() {
        // Pedir CI del estudiante
        String cedula = JOptionPane.showInputDialog(this, 
            "Ingrese la cédula del estudiante:", 
            "Agregar Exonerado", JOptionPane.QUESTION_MESSAGE);
        
        if (cedula == null || cedula.trim().isEmpty()) {
            return;
        }
        
        cedula = cedula.trim();
        
        // VALIDAR: Buscar en Secretaría UCV primero
        Usuario datosSecretaria = null;
        try {
            RepoSecretaria repoSecretaria = new RepoSecretaria();
            datosSecretaria = repoSecretaria.buscarRegistroUCV(cedula);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al consultar secretaría: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Si NO existe en secretaría, NO se crea
        if (datosSecretaria == null) {
            JOptionPane.showMessageDialog(this, 
                "El estudiante con CI " + cedula + " no está registrado en la Secretaría UCV.\n" +
                "No se puede agregar como exonerado.", 
                "Estudiante No Encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que sea un estudiante (no empleado ni profesor)
        if (!(datosSecretaria instanceof Estudiante)) {
            JOptionPane.showMessageDialog(this, 
                "El usuario con CI " + cedula + " no es un estudiante.\n" +
                "Tipo encontrado: " + datosSecretaria.obtTipo(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obtener datos de secretaría
        String carrera = ((Estudiante) datosSecretaria).obtCarrera();
        String facultad = ((Estudiante) datosSecretaria).obtFacultad();
        String nombre = datosSecretaria.obtNombre();
        
        // Verificar si ya existe en el sistema de usuarios
        Usuario usuarioExistente = null;
        int indexExistente = -1;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).obtCedula().equals(cedula)) {
                usuarioExistente = usuarios.get(i);
                indexExistente = i;
                break;
            }
        }
        
        // Crear exonerado con datos de secretaría
        EstudianteExonerado exonerado = new EstudianteExonerado(
            cedula,
            usuarioExistente != null ? usuarioExistente.obtContraseña() : cedula,
            carrera,
            facultad
        );
        exonerado.setNombre(nombre);
        
        // Copiar datos si existía en usuarios
        if (usuarioExistente != null) {
            exonerado.setEstado(usuarioExistente.obtEstado());
            exonerado.setSaldo(usuarioExistente.obtSaldo());
            usuarios.set(indexExistente, exonerado);
        } else {
            exonerado.setEstado(true);
            exonerado.setSaldo(0.0);
            usuarios.add(exonerado);
        }
        
        // Guardar en archivo usuarios.txt inmediatamente
        RepoUsuarios repo = new RepoUsuarios();
        try {
            repo.guardarTodos(usuarios);
        } catch (IOException e) {
            Logger.error("Error guardando exonerado en usuarios.txt", e);
            JOptionPane.showMessageDialog(this, 
                "Error al guardar en archivo: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Guardar historial
        guardarHistorial("EXONERADO", cedula, "100% descuento");
        
        // Recargar tabla desde archivo
        cargarUsuarios();
        
        JOptionPane.showMessageDialog(this, 
            "Estudiante exonerado agregado exitosamente.\n" +
            "Nombre: " + nombre + "\n" +
            "CI: " + cedula + "\n" +
            "Descuento: 100% (No paga)", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void guardarHistorial(String tipoOperacion, String cedula, String detalle) {
        try {
            java.io.FileWriter fw = new java.io.FileWriter("historial_cambios_admin.txt", true);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String admin = usuarioAdmin != null ? usuarioAdmin.obtCedula() : "Sistema";
            bw.write(String.format("[%s] Admin: %s | Operación: %s | CI: %s | Detalle: %s",
                now.toString(), admin, tipoOperacion, cedula, detalle));
            bw.newLine();
            bw.close();
            Logger.info("Historial guardado: " + tipoOperacion + " para CI " + cedula);
        } catch (IOException e) {
            Logger.error("Error guardando historial", e);
        }
    }

    public static void main(String[] args) {
        com.comedor.modelo.entidades.Usuario adminDummy = new com.comedor.modelo.entidades.Administrador("0", "admin", "0");
        SwingUtilities.invokeLater(() -> new GestionUsuariosUI(adminDummy).setVisible(true));
    }
}
