package com.comedor.vista.secretaria;

import com.comedor.modelo.entidades.*;
import com.comedor.modelo.persistencia.RepoSecretaria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.List;

/**
 * UI exclusiva para Secretaría - Gestión de datos sin editar archivos manualmente
 */
public class PanelSecretariaUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);
    
    private BufferedImage backgroundImage;
    private RepoSecretaria repoSecretaria;
    
    private JTable tableUsuarios;
    private DefaultTableModel modelUsuarios;
    private List<Usuario> usuariosList;

    public PanelSecretariaUI(Usuario usuario) {
        this.repoSecretaria = new RepoSecretaria();
        
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
        cargarDatos();
    }
    
    private void cargarDatos() {
        try {
            usuariosList = repoSecretaria.listarTodos();
            modelUsuarios.setRowCount(0);
            
            for (Usuario u : usuariosList) {
                String[] datos = obtenerDatosFila(u);
                modelUsuarios.addRow(datos);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String[] obtenerDatosFila(Usuario u) {
        String tipo = u.obtTipo();
        String cedula = u.obtCedula();
        String nombre = u.obtNombre();
        String campo1 = "";
        String campo2 = "";
        String estado = u.obtEstado() ? "Activo" : "Inactivo";
        
        if (u instanceof Estudiante) {
            campo1 = ((Estudiante)u).obtCarrera();
            campo2 = ((Estudiante)u).obtFacultad();
        } else if (u instanceof Profesor) {
            campo1 = ((Profesor)u).obtDepartamento();
            campo2 = ((Profesor)u).obtCodigo();
        } else if (u instanceof Empleado) {
            campo1 = ((Empleado)u).obtDepartamento();
            campo2 = ((Empleado)u).obtCargo();
        } else if (u instanceof Administrador) {
            campo1 = "N/A";
            campo2 = ((Administrador)u).obtCodigoAdministrador();
        }
        
        return new String[]{tipo, cedula, nombre, campo1, campo2, estado};
    }
    
    private void configurarVentana() {
        setTitle("Panel de Secretaría - SAGC UCV");
        setSize(1200, 900);
        setMinimumSize(new Dimension(900, 700));
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
        JLabel title = new JLabel("Panel de Secretaría - Gestión de Usuarios UCV", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);
        
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Contenido principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        JButton btnAgregar = new JButton("Agregar Usuario");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(new Color(0, 150, 0));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar("Usuario"));
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEditar.setBackground(new Color(0, 100, 200));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.addActionListener(e -> editarUsuarioSeleccionado());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.setBackground(new Color(200, 50, 50));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(100, 100, 100));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.addActionListener(e -> cargarDatos());
        
        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnActualizar);
        
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Tabla
        String[] columnas = {"Tipo", "Cédula", "Nombre", "Carrera/Depto", "Facultad/Cargo", "Estado"};
        modelUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableUsuarios = new JTable(modelUsuarios);
        tableUsuarios.setRowHeight(28);
        tableUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 81, 132), 2));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        JLabel lblInfo = new JLabel("Seleccione un usuario para editar o eliminar");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(Color.WHITE);
        infoPanel.add(lblInfo);
        contentPanel.add(infoPanel, BorderLayout.SOUTH);
        
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void mostrarDialogoAgregar(String tipo) {
        JDialog dialog = new JDialog(this, "Agregar Usuario UCV", true);
        dialog.setSize(550, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel superior: Tipo de usuario
        JPanel tipoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        tipoPanel.add(new JLabel("Tipo de Usuario:"));
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{
            "Estudiante", "EstudianteBecario", "EstudianteExonerado", "Profesor", "Empleado", "Administrador"
        });
        tipoPanel.add(comboTipo);
        
        // Campos comunes
        tipoPanel.add(new JLabel("Cédula:"));
        JTextField txtCedula = new JTextField(20);
        tipoPanel.add(txtCedula);
        
        tipoPanel.add(new JLabel("Nombre:"));
        JTextField txtNombre = new JTextField(20);
        tipoPanel.add(txtNombre);
        
        mainPanel.add(tipoPanel, BorderLayout.NORTH);
        
        // Panel dinámico: campos específicos según tipo
        JPanel camposPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        // Campos específicos con sus labels descriptivos
        JLabel lblCarrera = new JLabel("Carrera:");
        JTextField txtCarrera = new JTextField(20);
        
        JLabel lblFacultad = new JLabel("Facultad:");
        JTextField txtFacultad = new JTextField(20);
        
        JLabel lblMateria = new JLabel("Materia que imparte:");
        JTextField txtMateria = new JTextField(20);
        
        JLabel lblCargo = new JLabel("Cargo:");
        JTextField txtCargo = new JTextField(20);
        
        JLabel lblDepartamento = new JLabel("Departamento:");
        JTextField txtDepartamento = new JTextField(20);
        
        JLabel lblCodigoEmpleado = new JLabel("Código de Empleado:");
        JTextField txtCodigoEmpleado = new JTextField(20);
        
        JLabel lblDescuento = new JLabel("Porcentaje de Beca (%):");
        JTextField txtDescuento = new JTextField(20);
        
        // Guardar referencias para mostrar/ocultar
        java.util.Map<String, java.util.List<java.awt.Component>> camposPorTipo = new java.util.HashMap<>();
        camposPorTipo.put("Estudiante", java.util.Arrays.asList(lblCarrera, txtCarrera, lblFacultad, txtFacultad));
        camposPorTipo.put("EstudianteBecario", java.util.Arrays.asList(lblCarrera, txtCarrera, lblFacultad, txtFacultad, lblDescuento, txtDescuento));
        camposPorTipo.put("EstudianteExonerado", java.util.Arrays.asList(lblCarrera, txtCarrera, lblFacultad, txtFacultad));
        camposPorTipo.put("Profesor", java.util.Arrays.asList(lblDepartamento, txtDepartamento, lblMateria, txtMateria));
        camposPorTipo.put("Empleado", java.util.Arrays.asList(lblCargo, txtCargo, lblDepartamento, txtDepartamento, lblCodigoEmpleado, txtCodigoEmpleado));
        camposPorTipo.put("Administrador", java.util.Arrays.asList());
        
        // Agregar todos los campos al panel (inicialmente)
        camposPanel.add(lblCarrera);
        camposPanel.add(txtCarrera);
        camposPanel.add(lblFacultad);
        camposPanel.add(txtFacultad);
        camposPanel.add(lblDepartamento);
        camposPanel.add(txtDepartamento);
        camposPanel.add(lblMateria);
        camposPanel.add(txtMateria);
        camposPanel.add(lblCargo);
        camposPanel.add(txtCargo);
        camposPanel.add(lblCodigoEmpleado);
        camposPanel.add(txtCodigoEmpleado);
        camposPanel.add(lblDescuento);
        camposPanel.add(txtDescuento);
        
        mainPanel.add(camposPanel, BorderLayout.CENTER);
        
        // Función para actualizar visibilidad de campos
        java.util.function.Consumer<String> actualizarCampos = (tipoSeleccionado) -> {
            // Ocultar todos primero
            for (java.awt.Component c : camposPanel.getComponents()) {
                c.setVisible(false);
            }
            // Mostrar solo los del tipo seleccionado
            java.util.List<java.awt.Component> camposAMostrar = camposPorTipo.get(tipoSeleccionado);
            if (camposAMostrar != null) {
                for (java.awt.Component c : camposAMostrar) {
                    c.setVisible(true);
                }
            }
            camposPanel.revalidate();
            camposPanel.repaint();
        };
        
        // Configurar listener del combo
        comboTipo.addActionListener(e -> {
            String tipoSeleccionado = (String) comboTipo.getSelectedItem();
            actualizarCampos.accept(tipoSeleccionado);
            dialog.pack();
            dialog.setSize(550, dialog.getHeight());
        });
        
        // Mostrar campos iniciales
        actualizarCampos.accept((String) comboTipo.getSelectedItem());
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        
        // Botones
        JPanel buttonPanel = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(0, 150, 0));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(150, 0, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnGuardar.addActionListener(e -> {
            try {
                // Validar campos obligatorios comunes
                String cedula = txtCedula.getText().trim();
                String nombre = txtNombre.getText().trim();
                String tipoSeleccionado = (String) comboTipo.getSelectedItem();
                
                if (cedula.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Falta: Cédula", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
                    txtCedula.requestFocus();
                    return;
                }
                
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Falta: Nombre", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
                    txtNombre.requestFocus();
                    return;
                }
                
                // Validar formato de cédula
                if (!validarCedula(cedula)) {
                    JOptionPane.showMessageDialog(dialog, "Cédula inválida. Debe contener solo números (7-8 dígitos)", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    txtCedula.requestFocus();
                    return;
                }
                
                // Validar formato de nombre
                if (!validarNombre(nombre)) {
                    JOptionPane.showMessageDialog(dialog, "Nombre inválido. Debe contener solo letras y espacios (mínimo 3 caracteres)", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    txtNombre.requestFocus();
                    return;
                }
                
                // Validar campos específicos según tipo con mensajes detallados
                java.util.List<String> camposFaltantes = new java.util.ArrayList<>();
                
                switch (tipoSeleccionado) {
                    case "Estudiante":
                    case "EstudianteBecario":
                    case "EstudianteExonerado":
                        if (txtCarrera.getText().trim().isEmpty()) camposFaltantes.add("Carrera");
                        if (txtFacultad.getText().trim().isEmpty()) camposFaltantes.add("Facultad");
                        break;
                    case "Profesor":
                        if (txtDepartamento.getText().trim().isEmpty()) camposFaltantes.add("Departamento");
                        if (txtMateria.getText().trim().isEmpty()) camposFaltantes.add("Materia que imparte");
                        break;
                    case "Empleado":
                        if (txtCargo.getText().trim().isEmpty()) camposFaltantes.add("Cargo");
                        if (txtDepartamento.getText().trim().isEmpty()) camposFaltantes.add("Departamento");
                        if (txtCodigoEmpleado.getText().trim().isEmpty()) camposFaltantes.add("Código de Empleado");
                        break;
                    case "Administrador":
                        // No requiere campos adicionales
                        break;
                }
                
                if (!camposFaltantes.isEmpty()) {
                    String mensaje = "Faltan los siguientes campos obligatorios:\n" + String.join("\n", camposFaltantes);
                    JOptionPane.showMessageDialog(dialog, mensaje, "Campos Obligatorios", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Validaciones específicas de tipo
                if (tipoSeleccionado.equals("EstudianteBecario") && txtDescuento.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Falta: Porcentaje de Beca", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
                    txtDescuento.requestFocus();
                    return;
                }
                
                // Verificar si ya existe
                if (repoSecretaria.existeCedula(cedula)) {
                    JOptionPane.showMessageDialog(dialog, "Ya existe un usuario con esta cédula", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Crear usuario según tipo
                Usuario nuevoUsuario = crearUsuarioDesdeFormularioDinamico(
                    tipoSeleccionado,
                    cedula,
                    nombre,
                    txtCarrera.getText().trim(),
                    txtFacultad.getText().trim(),
                    txtDepartamento.getText().trim(),
                    txtMateria.getText().trim(),
                    txtCargo.getText().trim(),
                    txtCodigoEmpleado.getText().trim(),
                    txtDescuento.getText().trim()
                );
                
                if (nuevoUsuario != null) {
                    nuevoUsuario.setNombre(nombre);
                    
                    try {
                        repoSecretaria.guardar(nuevoUsuario, cedula);
                        JOptionPane.showMessageDialog(dialog, "Usuario guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        cargarDatos();
                    } catch (IOException ioEx) {
                        JOptionPane.showMessageDialog(dialog, "Error de archivo al guardar: " + ioEx.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error guardando: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // Métodos de validación de datos personales
    private boolean validarCedula(String cedula) {
        // Solo números, 7-8 dígitos
        return cedula.matches("\\d{7,8}");
    }
    
    private boolean validarNombre(String nombre) {
        // Solo letras y espacios, mínimo 3 caracteres
        if (nombre.length() < 3) return false;
        return nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+");
    }
    
    private Usuario crearUsuarioDesdeFormularioDinamico(String tipo, String cedula, String nombre, 
                                                   String carrera, String facultad, String departamento, 
                                                   String materia, String cargo, String codigoEmpleado, String descuento) {
        Usuario usuario = null;
        
        switch (tipo) {
            case "Estudiante":
                usuario = new Estudiante(cedula, cedula, carrera, facultad);
                break;
            case "EstudianteBecario":
                double desc = 95.0;
                try {
                    desc = Double.parseDouble(descuento);
                } catch (NumberFormatException e) {
                    desc = 95.0;
                }
                usuario = new EstudianteBecario(cedula, cedula, carrera, facultad, desc);
                break;
            case "EstudianteExonerado":
                usuario = new EstudianteExonerado(cedula, cedula, carrera, facultad);
                break;
            case "Profesor":
                // Profesor: cedula, contraseña, departamento, materia
                usuario = new Profesor(cedula, cedula, departamento, materia);
                break;
            case "Empleado":
                // Empleado: cedula, contraseña, cargo, departamento, codigoEmpleado
                usuario = new Empleado(cedula, cedula, cargo, departamento, codigoEmpleado);
                break;
            case "Administrador":
                // Generar código de administrador automático
                String codigoAdmin = "ADM-" + cedula.substring(0, 3);
                usuario = new Administrador(cedula, cedula, codigoAdmin);
                break;
        }
        
        return usuario;
    }
    
    private void editarUsuarioSeleccionado() {
        int row = tableUsuarios.getSelectedRow();
        if (row < 0 || row >= usuariosList.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Usuario usuarioSeleccionado = usuariosList.get(row);
        
        JOptionPane.showMessageDialog(this, 
            "Función de editar en desarrollo.\nUsuario seleccionado: " + usuarioSeleccionado.obtNombre(), 
            "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void eliminarUsuarioSeleccionado() {
        int row = tableUsuarios.getSelectedRow();
        if (row < 0 || row >= usuariosList.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Usuario usuarioSeleccionado = usuariosList.get(row);
        String cedula = usuarioSeleccionado.obtCedula();
        String nombre = usuarioSeleccionado.obtNombre();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar a:\n" + nombre + " (" + cedula + ")?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                repoSecretaria.eliminar(cedula);
                JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error eliminando: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear un usuario administrador de prueba
                Usuario adminPrueba = new Usuario("12345678", "admin123") {
                    @Override
                    public String obtTipo() {
                        return "Administrador";
                    }
                    
                    @Override
                    public double calcularTarifa(double precioBase) {
                        return 0.0; // Admin no paga
                    }
                };
                adminPrueba.setNombre("Administrador de Prueba");
                
                new PanelSecretariaUI(adminPrueba).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error iniciando Panel Secretaría: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
