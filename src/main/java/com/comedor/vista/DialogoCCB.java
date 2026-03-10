package com.comedor.vista;

import javax.swing.*;
import java.awt.*;
import com.comedor.controlador.ServicioCosto;
import javax.swing.border.EmptyBorder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class DialogoCCB extends JDialog {
    private JTextField txtFijos;
    private JTextField txtVariables;
    private JTextField txtProduccion;
    private JTextField txtMerma;
    private JTextField txtPctEstudiante;
    private JTextField txtPctEmpleado;
    private JTextField txtPctProfesor;
    private JLabel lblResultado;
    private ServicioCosto servicioCosto;

    public DialogoCCB(Frame parent) {
        super(parent, "Cálculo de CCB (Costo Cubierto por Bandeja)", true);
        this.servicioCosto = new ServicioCosto();
        
        // Diseño más compacto y centrado
        setSize(450, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 250));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("Cálculo de Costos", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 51, 102));
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(title, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(crearLabel("Costos Fijos ($):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtFijos = crearInput();
        mainPanel.add(txtFijos, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(crearLabel("Costos Variables ($):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtVariables = crearInput();
        mainPanel.add(txtVariables, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(crearLabel("Producción Total (Bandejas):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtProduccion = crearInput();
        mainPanel.add(txtProduccion, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(crearLabel("% Merma (Ej: 10 para 10%):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        txtMerma = crearInput();
        mainPanel.add(txtMerma, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(crearLabel("% Tarifa Estudiante (Rango: 20-30):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtPctEstudiante = crearInput();
        mainPanel.add(txtPctEstudiante, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(crearLabel("% Tarifa Empleado (Rango: 90-110):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtPctEmpleado = crearInput();
        mainPanel.add(txtPctEmpleado, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(crearLabel("% Tarifa Profesor (Rango: 70-90):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        txtPctProfesor = crearInput();
        mainPanel.add(txtPctProfesor, gbc);

        JButton btnCalcular = new JButton("Calcular y Guardar");
        btnCalcular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCalcular.setBackground(new Color(0, 51, 102));
        btnCalcular.setForeground(Color.WHITE);
        btnCalcular.setFocusPainted(false);
        btnCalcular.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalcular.setPreferredSize(new Dimension(200, 40));
        btnCalcular.addActionListener(e -> calcular());
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 15, 0);
        mainPanel.add(btnCalcular, gbc);

        lblResultado = new JLabel("CCB Actual: -");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblResultado.setForeground(new Color(0, 100, 0));
        lblResultado.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy++;
        mainPanel.add(lblResultado, gbc);

        // Contenedor externo para centrar el panel blanco
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(mainPanel);
        
        add(wrapper, BorderLayout.CENTER);

        cargarTarifasDesdeConfig();
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private JTextField crearInput() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return txt;
    }

    private void cargarTarifasDesdeConfig() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
        } catch (Exception e) {
            // Sin archivo aún
        }

        txtPctEstudiante.setText(props.getProperty("tarifa_pct_estudiante", "25"));
        txtPctEmpleado.setText(props.getProperty("tarifa_pct_empleado", "100"));
        txtPctProfesor.setText(props.getProperty("tarifa_pct_profesor", "80"));
    }

    private void calcular() {
        try {
            double fijos = Double.parseDouble(txtFijos.getText());
            double variables = Double.parseDouble(txtVariables.getText());
            int produccion = Integer.parseInt(txtProduccion.getText());
            double porcentajeMerma = Double.parseDouble(txtMerma.getText());

            double pctEstudiante = Double.parseDouble(txtPctEstudiante.getText());
            double pctEmpleado = Double.parseDouble(txtPctEmpleado.getText());
            double pctProfesor = Double.parseDouble(txtPctProfesor.getText());

            // --- VALIDACIÓN DE RANGOS DE TARIFA ---
            if (pctEstudiante < 20 || pctEstudiante > 30) {
                JOptionPane.showMessageDialog(this, "La tarifa de Estudiante debe estar entre 20% y 30%.", "Rango Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pctProfesor < 70 || pctProfesor > 90) {
                JOptionPane.showMessageDialog(this, "La tarifa de Profesor debe estar entre 70% y 90%.", "Rango Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pctEmpleado < 90 || pctEmpleado > 110) {
                JOptionPane.showMessageDialog(this, "La tarifa de Empleado debe estar entre 90% y 110%.", "Rango Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- VALIDACIÓN DE OTROS VALORES ---
            if (fijos < 0 || variables < 0) {
                JOptionPane.showMessageDialog(this, "Los costos no pueden ser negativos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (produccion <= 0) {
                JOptionPane.showMessageDialog(this, "La producción debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (porcentajeMerma < 0) {
                JOptionPane.showMessageDialog(this, "El porcentaje de merma no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convertimos el entero (ej: 10) a decimal (0.10) para el cálculo
            double ccb = servicioCosto.calcularRegistrarCCBCompleto(fijos, variables, produccion, porcentajeMerma / 100.0);
            lblResultado.setText(String.format("CCB: $%.2f", ccb));
            
            // Guardar toda la configuración de una vez para evitar errores
            guardarConfiguracionCompleta(ccb, pctEstudiante, pctEmpleado, pctProfesor);
            
            double precioEstudiante = ccb * (pctEstudiante / 100.0);
            double precioEmpleado = ccb * (pctEmpleado / 100.0);
            double precioProfesor = ccb * (pctProfesor / 100.0);

            String mensaje = String.format("Cálculo y tarifas guardados exitosamente.\n\n" +
                    "Nuevo Costo por Bandeja (CCB): $%.2f\n\n" +
                    "Precios Finales:\n" +
                    "- Estudiante: $%.2f\n" +
                    "- Empleado: $%.2f\n" +
                    "- Profesor: $%.2f", 
                    ccb, precioEstudiante, precioEmpleado, precioProfesor);

            JOptionPane.showMessageDialog(this, mensaje);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarConfiguracionCompleta(double ccb, double pctEstudiante, double pctEmpleado, double pctProfesor) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
        } catch (Exception e) {
            // Si no existe, se crea uno nuevo
        }

        try (FileOutputStream out = new FileOutputStream("menu_config.properties")) {
            props.setProperty("ccb_actual", String.valueOf(ccb));
            props.setProperty("tarifa_pct_estudiante", String.valueOf(pctEstudiante));
            props.setProperty("tarifa_pct_empleado", String.valueOf(pctEmpleado));
            props.setProperty("tarifa_pct_profesor", String.valueOf(pctProfesor));
            props.store(out, "Actualizacion de Costos (CCB) y Tarifas");
        } catch (Exception e) {
            System.err.println("Error guardando tarifas: " + e.getMessage());
        }
    }
}