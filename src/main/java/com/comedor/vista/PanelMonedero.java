package com.comedor.vista;

import javax.swing.*;
import java.awt.*;

import com.comedor.controlador.ServicioCosto;
import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.entidades.Usuario;

public class PanelMonedero extends JPanel {
    private Monedero monedero;
    private Usuario usuario;
    private ServicioCosto servicioCosto;
    private JLabel lblSaldo;
    private JTextField txtRecarga;
    private JTextField txtReferencia;
    private JComboBox<String> cmbBanco;

    public PanelMonedero(Usuario usuario) {
        this.usuario = usuario;
        this.monedero = new Monedero(usuario);
        this.servicioCosto = new ServicioCosto();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Mi Monedero"));

        // Panel Superior: Saldo
        JPanel pnlInfo = new JPanel();
        lblSaldo = new JLabel("Saldo Disponible: $" + String.format("%.2f", monedero.obtSaldo()));
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        lblSaldo.setForeground(new Color(0, 100, 0));
        pnlInfo.add(lblSaldo);
        add(pnlInfo, BorderLayout.NORTH);

        // Panel Central: Formulario de Recarga
        JPanel pnlRecarga = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo Banco
        gbc.gridx = 0; gbc.gridy = 0;
        pnlRecarga.add(new JLabel("Banco:"), gbc);

        gbc.gridx = 1;
        String[] bancos = {"Mercantil", "Banesco", "Venezuela", "Bancamiga", "Provincial", "BNC"};
        cmbBanco = new JComboBox<>(bancos);
        pnlRecarga.add(cmbBanco, gbc);

        // Campo Referencia
        gbc.gridx = 0; gbc.gridy = 1;
        pnlRecarga.add(new JLabel("Referencia:"), gbc);

        gbc.gridx = 1;
        txtReferencia = new JTextField(15);
        pnlRecarga.add(txtReferencia, gbc);

        // Campo Monto
        gbc.gridx = 0; gbc.gridy = 2;
        pnlRecarga.add(new JLabel("Monto ($):"), gbc);

        gbc.gridx = 1;
        txtRecarga = new JTextField(10);
        pnlRecarga.add(txtRecarga, gbc);

        // Botón Recargar
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton btnRecargar = new JButton("Recargar Saldo");
        btnRecargar.setBackground(new Color(0, 51, 102));
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRecargar.addActionListener(e -> realizarRecarga());
        
        pnlRecarga.add(btnRecargar, gbc);
        add(pnlRecarga, BorderLayout.CENTER);
    }

    private void realizarRecarga() {
        try {
            double monto = Double.parseDouble(txtRecarga.getText());
            String banco = (String) cmbBanco.getSelectedItem();
            String referencia = txtReferencia.getText();

            // Delegar validación y persistencia al servicio
            servicioCosto.procesarRecarga(usuario, monto, banco, referencia);
            
            actualizarVisualizacion();
            
            // Limpiar campos
            txtRecarga.setText("");
            txtReferencia.setText("");
            cmbBanco.setSelectedIndex(0);
            
            JOptionPane.showMessageDialog(this, "Recarga exitosa.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un monto numérico válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en Recarga", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarVisualizacion() {
        lblSaldo.setText("Saldo Disponible: $" + String.format("%.2f", monedero.obtSaldo()));
    }
}