package com.comedor.vista.usuario;

import com.comedor.controlador.ServicioCosto;
import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Componente gráfico reutilizable para gestionar la recarga de saldo del usuario.
 */
public class PRecarga extends JPanel {

    private final Usuario usuario;
    private final Monedero monedero;
    private final Runnable alRecargar; // Acción a ejecutar tras una recarga exitosa
    private final ServicioCosto servicioCosto;
    
    private JLabel lblSaldoActual;
    private JComboBox<String> cmbBanco;
    private JTextField txtReferencia;
    private JTextField txtMontoRecarga;
    private JTextField txtCedulaDestino;
    private JButton btnRecargar;

    private final boolean mostrarCedulaDestino;

    private Color uiColor(String key, Color fallback) {
        Color c = UIManager.getColor(key);
        return (c != null) ? c : fallback;
    }

    // Constructor principal
    public PRecarga(Usuario usuario, Runnable alRecargar) {
        this(usuario, alRecargar, true);
    }

    public PRecarga(Usuario usuario, Runnable alRecargar, boolean mostrarCedulaDestino) {
        this.usuario = usuario;
        this.monedero = new Monedero(usuario);
        this.alRecargar = alRecargar;
        this.servicioCosto = new ServicioCosto();
        this.mostrarCedulaDestino = mostrarCedulaDestino;
        
        initUI();
        actualizarSaldoVisual();
    }

    // Inicializa los componentes del panel de recarga
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false); // Para permitir el fondo redondeado personalizado
        setBackground(Color.WHITE); // Color base para el fondo redondeado
        setBorder(new EmptyBorder(20, 25, 25, 25));

        // --- SECCIÓN SUPERIOR: Título y Saldo ---
        JPanel panelInfo = new JPanel(new BorderLayout(0, 5));
        panelInfo.setOpaque(false);

        JLabel lblTitulo = new JLabel("Mi Monedero");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(uiColor("Comedor.azulInst", new Color(0, 51, 102)));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel saldoContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        saldoContainer.setOpaque(false);
        JLabel saldoTexto = new JLabel("Saldo disponible: ");
        saldoTexto.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        saldoTexto.setForeground(uiColor("Comedor.textoSecundario", new Color(100, 100, 100)));

        lblSaldoActual = new JLabel("$ 0.00");
        lblSaldoActual.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSaldoActual.setForeground(uiColor("Comedor.verdeExito", new Color(0, 100, 0)));

        saldoContainer.add(saldoTexto);
        saldoContainer.add(lblSaldoActual);

        panelInfo.add(lblTitulo, BorderLayout.NORTH);
        panelInfo.add(saldoContainer, BorderLayout.CENTER);

        // --- SECCIÓN CENTRAL: Input y Botón ---
        JPanel panelAccion = new JPanel(new GridBagLayout());
        panelAccion.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);

        txtMontoRecarga = new JTextField(10);
        txtMontoRecarga.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMontoRecarga.setToolTipText("Ingrese monto a recargar");
        
        // Validación para permitir solo números y puntos decimales
        txtMontoRecarga.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Evitar múltiples puntos decimales
                if (c == '.' && txtMontoRecarga.getText().contains(".")) {
                    e.consume();
                }
                if (!((c >= '0') && (c <= '9') || (c == '.') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });

        txtReferencia = new JTextField(12);
        txtReferencia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtReferencia.setToolTipText("Número de referencia");

        txtCedulaDestino = new JTextField(12);
        txtCedulaDestino.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCedulaDestino.setToolTipText("Cédula de otro comensal (opcional)");
        txtCedulaDestino.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });

        btnRecargar = new JButton("Recargar");
        btnRecargar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRecargar.setBackground(uiColor("Comedor.azulInst", new Color(0, 51, 102)));
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setOpaque(true);
        btnRecargar.setContentAreaFilled(true);
        btnRecargar.setFocusPainted(false);
        btnRecargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecargar.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnRecargar.addActionListener(e -> procesarRecarga());

        // Efecto Hover para el botón leyendo del "CSS"
        btnRecargar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRecargar.setBackground(uiColor("Comedor.azulHover", new Color(0, 81, 132)));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnRecargar.setBackground(uiColor("Comedor.azulInst", new Color(0, 51, 102)));
            }
        });

        // --- Construcción del Formulario ---
        
        // Fila 1: Banco
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelAccion.add(crearLabelConIcono("🏦", "Banco:"), gbc);
        
        String[] bancos = {"Mercantil", "Banesco", "Venezuela", "Bancamiga", "Provincial", "BNC"};
        cmbBanco = new JComboBox<>(bancos);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelAccion.add(cmbBanco, gbc);

        // Fila 2: Referencia
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panelAccion.add(crearLabelConIcono("🧾", "Referencia:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelAccion.add(txtReferencia, gbc);

        int rowMonto = 2;
        if (mostrarCedulaDestino) {
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
            panelAccion.add(crearLabelConIcono("🤝", "Cédula destino:"), gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panelAccion.add(txtCedulaDestino, gbc);
            rowMonto = 3;
        }

        // Fila 3: Monto
        gbc.gridx = 0; gbc.gridy = rowMonto; gbc.fill = GridBagConstraints.NONE;
        panelAccion.add(crearLabelConIcono("💵", "Monto ($):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelAccion.add(txtMontoRecarga, gbc);

        // Fila 4: Botón
        gbc.gridx = 0; gbc.gridy = rowMonto + 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        panelAccion.add(btnRecargar, gbc);

        add(panelInfo, BorderLayout.NORTH);
        add(panelAccion, BorderLayout.CENTER);
        
        // Tamaño preferido para que se vea bien en el centro
        if (mostrarCedulaDestino) {
            setPreferredSize(new Dimension(460, 390));
            setMaximumSize(new Dimension(500, 420));
        } else {
            setPreferredSize(new Dimension(420, 340));
            setMaximumSize(new Dimension(450, 360));
        }
    }

    // Dibuja el fondo redondeado del panel
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
        g2d.dispose();
    }

    // Helper para crear etiquetas con un icono (emoji) y estilo global
    private JLabel crearLabelConIcono(String icono, String texto) {
        JLabel label = new JLabel(icono + " " + texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(uiColor("Comedor.textoSecundario", new Color(100, 100, 100)));
        return label;
    }

    // Actualiza la etiqueta de saldo con el valor actual del monedero
    private void actualizarSaldoVisual() {
        lblSaldoActual.setText(String.format("$ %.2f", monedero.obtSaldo()));
    }

    // Valida el monto ingresado y ejecuta la recarga y persistencia
    private void procesarRecarga() {
        String textoMonto = txtMontoRecarga.getText().trim();
        String banco = (String) cmbBanco.getSelectedItem();
        String referencia = txtReferencia.getText().trim();
        String cedulaDestino = mostrarCedulaDestino ? txtCedulaDestino.getText().trim() : "";
        
        if (textoMonto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un monto.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double monto = Double.parseDouble(textoMonto);
            
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if ((monedero.obtSaldo() + monto) > Monedero.LIMITE_SALDO) {
                double maxPosible = Monedero.LIMITE_SALDO - monedero.obtSaldo();
                JOptionPane.showMessageDialog(this, 
                    String.format("El saldo total no puede exceder %.2f.\nPuedes recargar hasta: %.2f", Monedero.LIMITE_SALDO, maxPosible), 
                    "Límite de Saldo Excedido", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Delegar la recarga al servicio (Valida banco, referencia y persiste)
            servicioCosto.procesarRecarga(usuario, monto, banco, referencia, cedulaDestino);
            
            // 2. Actualizar UI interna
            actualizarSaldoVisual();
            txtMontoRecarga.setText("");
            txtReferencia.setText("");
            if (mostrarCedulaDestino) {
                txtCedulaDestino.setText("");
            }
            cmbBanco.setSelectedIndex(0);
            
            // 4. Notificar a la ventana padre para que actualice otros componentes
            if (alRecargar != null) {
                alRecargar.run();
            }
            
            JOptionPane.showMessageDialog(this, 
                String.format("¡Recarga exitosa!\nNuevo saldo: $ %.2f", monedero.obtSaldo()), 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido. Use formato numérico (ej: 50.00)", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en Recarga", JOptionPane.ERROR_MESSAGE);
        }
    }
}