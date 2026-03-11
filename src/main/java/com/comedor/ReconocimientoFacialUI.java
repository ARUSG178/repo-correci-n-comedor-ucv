package com.comedor;

import com.comedor.controlador.ServicioBiometrico;
import com.comedor.controlador.ServicioPago;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Reserva;
import com.comedor.modelo.persistencia.RepoReservas;
import com.comedor.modelo.excepciones.BiometriaFallidaException;
import com.comedor.vista.usuario.HistorialReservasUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.awt.Window;

public class ReconocimientoFacialUI extends JFrame {

    private final Usuario usuario;
    private final double costoPlatillo;
    private final LocalDateTime fechaReserva;
    
    private File archivoSeleccionado;
    private JLabel lblPreview;
    private JLabel lblEstado;
    private JButton btnConfirmar;

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);

    private BufferedImage backgroundImage;

    // Constructor principal que recibe los datos de la transacción
    public ReconocimientoFacialUI(Usuario usuario, double costoPlatillo, LocalDateTime fechaReserva) {
        this.usuario = usuario;
        this.costoPlatillo = costoPlatillo;
        this.fechaReserva = fechaReserva;
        
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }

        configurarVentana();
        initUI();
    }

    // Configura las propiedades de la ventana
    private void configurarVentana() {
        setTitle("Verificación Biométrica - SAGC");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
    }

    // Inicializa los componentes de la interfaz de reconocimiento
    private void initUI() {
        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel lblTitulo = new JLabel("Validación de Identidad", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);

        // Centro
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel lblInstruccion = new JLabel("<html><center>Suba una foto de su rostro para autorizar<br>el cobro de <b>$ " + String.format("%.2f", costoPlatillo) + "</b></center></html>");
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblInstruccion.setForeground(Color.WHITE);
        gbc.gridy = 0;
        centerPanel.add(lblInstruccion, gbc);

        JPanel previewContainer = new JPanel(new BorderLayout());
        previewContainer.setPreferredSize(new Dimension(300, 300));
        previewContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        previewContainer.setBackground(Color.WHITE);

        lblPreview = new JLabel("Sin imagen", SwingConstants.CENTER);
        previewContainer.add(lblPreview, BorderLayout.CENTER);

        gbc.gridy = 1;
        centerPanel.add(previewContainer, gbc);

        JButton btnSubir = new JButton("Cargar Foto");
        btnSubir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubir.setBackground(COLOR_AZUL_INST);
        btnSubir.setForeground(Color.WHITE);
        btnSubir.setFocusPainted(false);
        btnSubir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubir.addActionListener(e -> seleccionarFoto());
        gbc.gridy = 2;
        centerPanel.add(btnSubir, gbc);

        lblEstado = new JLabel("Esperando imagen...");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(new Color(220, 220, 220));
        gbc.gridy = 3;
        centerPanel.add(lblEstado, gbc);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 40));
        footerPanel.setOpaque(false);

        btnConfirmar = new JButton("Verificar y Pagar");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setBackground(COLOR_AZUL_INST);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setPreferredSize(new Dimension(200, 45));
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> procesarVerificacion());

        footerPanel.add(btnConfirmar);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Abre un selector de archivos para cargar la imagen del rostro
    private void seleccionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(archivoSeleccionado);
                
                // CORRECCIÓN: Escalar proporcionalmente para evitar que se vea estirada
                ImageIcon icon = escalarImagenProporcional(img, 300, 300);
                
                lblPreview.setText("");
                lblPreview.setIcon(icon);
                btnConfirmar.setEnabled(true);
                lblEstado.setText("Imagen cargada.");
            } catch (IOException e) {
                lblEstado.setText("Error al cargar imagen.");
            }
        }
    }

    // Escala la imagen manteniendo la proporción para la previsualización
    private ImageIcon escalarImagenProporcional(BufferedImage img, int maxWidth, int maxHeight) {
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();
        double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
        
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);
        
        Image dimg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(dimg);
    }

    // Ejecuta la lógica de validación biométrica y cobro
    private void procesarVerificacion() {
        lblEstado.setText("Procesando...");
        try {
            // 1. Delegar validación biométrica
            ServicioBiometrico sBiometrico = new ServicioBiometrico();
            double similitud = sBiometrico.calcularSimilitud(usuario, archivoSeleccionado);
            
            if (similitud <= 60.0) {
                lblEstado.setText("Usuario inválido");
                throw new BiometriaFallidaException("Usuario inválido");
            }

            lblEstado.setText("Usuario válido");

            // 2. Delegar cobro
            ServicioPago sPago = new ServicioPago();
            sPago.procesarCobro(usuario, costoPlatillo);

            // 3. Guardar la reserva
            Reserva nuevaReserva = new Reserva(usuario, fechaReserva, "Completado");
            RepoReservas.guardarReserva(nuevaReserva);

            // 3. Actualizar historial de reservas
            try {
                // Abrir o actualizar la ventana de historial
                SwingUtilities.invokeLater(() -> {
                    // Buscar si ya hay una ventana de historial abierta
                    for (Window window : Window.getWindows()) {
                        if (window instanceof HistorialReservasUI) {
                            window.dispose(); // Cerrar la ventana vieja
                            break;
                        }
                    }
                    // Crear nueva ventana de historial con datos actualizados
                    new HistorialReservasUI(usuario).setVisible(true);
                });
            } catch (Exception ex) {
                System.err.println("Error al actualizar historial: " + ex.getMessage());
            }

            String mensaje = String.format("¡Pago Exitoso!\nUsuario válido\nReserva confirmada para: %s\n\nEl historial ha sido actualizado.",
                    fechaReserva.toString().replace("T", " "));
            JOptionPane.showMessageDialog(this, mensaje, "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Resultado de Verificación", JOptionPane.ERROR_MESSAGE);
            lblEstado.setText("Intente nuevamente.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Properties props = new Properties();
            File requestFile = new File("verification_request.properties");

            if (!requestFile.exists()) {
                JOptionPane.showMessageDialog(null, "No se encontró una solicitud de verificación pendiente.", "Módulo Biométrico", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (FileInputStream in = new FileInputStream(requestFile)) {
                props.load(in);

                String cedula = props.getProperty("cedula");
                double costo = Double.parseDouble(props.getProperty("costo"));
                LocalDateTime fecha = LocalDateTime.parse(props.getProperty("fechaReserva"));

                if (cedula == null || cedula.isEmpty()) {
                    throw new Exception("El archivo de solicitud no contiene una cédula válida.");
                }

                // Buscar al usuario completo en la base de datos de usuarios
                RepoUsuarios repoUsuarios = new RepoUsuarios();
                List<Usuario> usuarios = repoUsuarios.listarUsuarios();
                Usuario usuarioAVerificar = usuarios.stream()
                        .filter(u -> u.obtCedula().equals(cedula))
                        .findFirst()
                        .orElse(null);

                if (usuarioAVerificar == null) {
                    throw new Exception("No se encontró al usuario con cédula " + cedula + " en la base de datos.");
                }

                // Una vez que tenemos todos los datos, lanzamos la UI
                new ReconocimientoFacialUI(usuarioAVerificar, costo, fecha).setVisible(true);

                // Borrar el archivo de solicitud para que no se reutilice
                requestFile.delete();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al iniciar el módulo de verificación:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}