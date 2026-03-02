package com.comedor.vista.usuario;

import com.comedor.modelo.entidades.Usuario;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RecargaSaldoUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);

    private final Usuario usuario;
    private final Runnable onRecargaExitosa;

    private final CardLayout cards = new CardLayout();
    private JPanel cardsPanel;
    private SegmentedRecargaToggle toggle;

    public RecargaSaldoUI(Usuario usuario, Runnable onRecargaExitosa) {
        this.usuario = usuario;
        this.onRecargaExitosa = onRecargaExitosa;

        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Recargar Saldo - SAGC UCV");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(22, 26, 22, 26));
        centerPanel.setLayout(new BorderLayout());

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        toggle = new SegmentedRecargaToggle();
        toggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(toggle);
        body.add(Box.createRigidArea(new Dimension(0, 18)));

        cardsPanel = new JPanel(cards);
        cardsPanel.setOpaque(false);

        PRecarga panelRecarga = new PRecarga(usuario, () -> {
            if (onRecargaExitosa != null) onRecargaExitosa.run();
            dispose();
        }, false);

        PRecarga panelSaldoPana = new PRecarga(usuario, () -> {
            if (onRecargaExitosa != null) onRecargaExitosa.run();
            dispose();
        }, true);

        cardsPanel.add(panelRecarga, "RECARGAR");
        cardsPanel.add(panelSaldoPana, "PANA");

        body.add(cardsPanel);
        centerPanel.add(body, BorderLayout.CENTER);

        root.add(centerPanel, BorderLayout.CENTER);
        setContentPane(root);

        mostrarRecargar();

        pack();
        setLocationRelativeTo(null);
    }

    private void mostrarRecargar() {
        cards.show(cardsPanel, "RECARGAR");
        if (toggle != null) toggle.setSelected(Mode.RECARGAR);
    }

    private void mostrarSaldoPana() {
        cards.show(cardsPanel, "PANA");
        if (toggle != null) toggle.setSelected(Mode.PANA);
    }

    private enum Mode { RECARGAR, PANA }

    private class SegmentedRecargaToggle extends JPanel {
        private final JLabel left = new JLabel("Recargar", SwingConstants.CENTER);
        private final JLabel right = new JLabel("Saldo Pana", SwingConstants.CENTER);
        private Mode selected = Mode.RECARGAR;

        SegmentedRecargaToggle() {
            setOpaque(false);
            setLayout(new GridLayout(1, 2));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

            configurarLabel(left, Mode.RECARGAR);
            configurarLabel(right, Mode.PANA);
            add(left);
            add(right);
            setSelected(Mode.RECARGAR);
        }

        private void configurarLabel(JLabel label, Mode mode) {
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.setBorder(new EmptyBorder(14, 10, 14, 10));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (mode == Mode.RECARGAR) {
                        mostrarRecargar();
                    } else {
                        mostrarSaldoPana();
                    }
                }
            });
        }

        void setSelected(Mode mode) {
            this.selected = mode;

            Color activeBg = new Color(255, 255, 255);
            Color activeFg = COLOR_AZUL_INST;
            Color inactiveBg = new Color(230, 235, 245);
            Color inactiveFg = new Color(0, 51, 102, 160);

            boolean leftActive = selected == Mode.RECARGAR;
            left.setBackground(leftActive ? activeBg : inactiveBg);
            left.setForeground(leftActive ? activeFg : inactiveFg);
            right.setBackground(!leftActive ? activeBg : inactiveBg);
            right.setForeground(!leftActive ? activeFg : inactiveFg);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Usuario dummy = new com.comedor.modelo.entidades.Estudiante("123", "123", "Ingeniería", "Ciencias");
            new RecargaSaldoUI(dummy, () -> System.out.println("Recarga exitosa")).setVisible(true);
        });
    }
}
