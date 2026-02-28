package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.persistencia.RepoUsuarios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

/**
 * Componente gráfico reutilizable para gestionar la recarga de saldo del usuario.
 */
public class PRecarga extends JPanel {

    private final Usuario usuario;
    private final Monedero monedero;
    private final Runnable alRecargar; // Acción a ejecutar tras una recarga exitosa
    
    private JLabel lblSaldoActual;
    private JTextField txtMontoRecarga;
    private JButton btnRecargar;

    // Colores del tema
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_FONDO = new Color(245, 245, 250);

    // Constructor principal
    public PRecarga(Usuario usuario, Runnable alRecargar) {
        this.usuario = usuario;
        this.monedero = new Monedero(usuario);
        this.alRecargar = alRecargar;
        
        initUI();
        actualizarSaldoVisual();
    }

    // Inicializa los componentes del panel de recarga
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_FONDO);
        // Borde redondeado simulado con borde compuesto
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // --- SECCIÓN SUPERIOR: Título y Saldo ---
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        panelInfo.setOpaque(false);

        JLabel lblTitulo = new JLabel("Mi Monedero");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_AZUL_INST);

        lblSaldoActual = new JLabel("Saldo: $ 0.00");
        lblSaldoActual.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSaldoActual.setForeground(new Color(50, 50, 50));

        panelInfo.add(lblTitulo);
        panelInfo.add(lblSaldoActual);

        // --- SECCIÓN CENTRAL: Input y Botón ---
        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelAccion.setOpaque(false);

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

        btnRecargar = new JButton("Recargar");
        btnRecargar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRecargar.setBackground(COLOR_AZUL_INST);
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setFocusPainted(false);
        btnRecargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecargar.addActionListener(e -> procesarRecarga());

        // Espaciado entre input y botón
        panelAccion.add(new JLabel("Monto: "));
        panelAccion.add(Box.createHorizontalStrut(10));
        panelAccion.add(txtMontoRecarga);
        panelAccion.add(Box.createHorizontalStrut(15));
        panelAccion.add(btnRecargar);

        add(panelInfo, BorderLayout.NORTH);
        add(panelAccion, BorderLayout.CENTER);
        
        // Tamaño preferido para que se vea bien en el centro
        setPreferredSize(new Dimension(400, 150));
        setMaximumSize(new Dimension(500, 160));
    }

    // Actualiza la etiqueta de saldo con el valor actual del monedero
    private void actualizarSaldoVisual() {
        lblSaldoActual.setText(String.format("Saldo disponible: $ %.2f", monedero.obtSaldo()));
    }

    // Valida el monto ingresado y ejecuta la recarga y persistencia
    private void procesarRecarga() {
        String textoMonto = txtMontoRecarga.getText().trim();
        
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

            // 1. Recargar
            monedero.recargar(monto);
            
            // 2. Persistir el nuevo saldo en la base de datos (archivo)
            try {
                RepoUsuarios repo = new RepoUsuarios();
                List<Usuario> usuarios = repo.listarUsuarios();
                for (Usuario u : usuarios) {
                    if (u.obtCedula().equals(usuario.obtCedula())) {
                        u.setSaldo(monedero.obtSaldo()); // Actualizamos el saldo explícitamente
                        break;
                    }
                }
                repo.guardarTodos(usuarios);
                
                // Sincronizar el objeto usuario local por si acaso
                usuario.setSaldo(monedero.obtSaldo());
            } catch (IOException ioEx) {
                JOptionPane.showMessageDialog(this, "Error al guardar el saldo: " + ioEx.getMessage(), "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
            }

            // 3. Actualizar UI interna
            actualizarSaldoVisual();
            txtMontoRecarga.setText("");
            
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
        }
    }
}