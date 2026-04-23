import javax.swing.*;
import java.awt.*;

public class SecantCalculator extends JDialog {
    private JTextField equationField, x0Field, x1Field, toleranceField;
    private JTextArea resultArea;

    public SecantCalculator(JFrame parent) {
        super(parent, "Secant Method", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(168, 85, 247));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Secant Method");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(5, 5, 5, 5);

        equationField = new JTextField("x^3 - x - 2", 20);
        x0Field = new JTextField("1", 10);
        x1Field = new JTextField("2", 10);
        toleranceField = new JTextField("0.0001", 10);

        addFormField(formPanel, formGbc, "Function f(x):", equationField, 0);
        addFormField(formPanel, formGbc, "Initial guess x₀:", x0Field, 1);
        addFormField(formPanel, formGbc, "Initial guess x₁:", x1Field, 2);
        addFormField(formPanel, formGbc, "Tolerance:", toleranceField, 3);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(147, 51, 234));
        calcButton.setForeground(Color.WHITE);
        calcButton.setFocusPainted(false);
        calcButton.addActionListener(e -> calculate());
        gbc.gridy = 2;
        panel.add(calcButton, gbc);

        resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBackground(new Color(240, 253, 244));
        resultArea.setBorder(BorderFactory.createLineBorder(new Color(34, 197, 94), 2));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridy = 3;
        panel.add(scrollPane, gbc);

        add(panel);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(field, gbc);
    }

    private void calculate() {
        try {
            String equation = equationField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tolerance = Double.parseDouble(toleranceField.getText());

            int iterations = 0;
            int maxIterations = 100;

            while (iterations < maxIterations) {
                double f0 = evaluateFunction(x0, equation);
                double f1 = evaluateFunction(x1, equation);

                if (Math.abs(f1 - f0) < 1e-10) {
                    resultArea.setText("Error: Division by zero");
                    return;
                }

                double x2 = x1 - (f1 * (x1 - x0)) / (f1 - f0);
                double f2 = evaluateFunction(x2, equation);

                if (Math.abs(f2) < tolerance) {
                    resultArea.setText(String.format("Root found: %.6f\nIterations: %d", x2, iterations));
                    return;
                }

                x0 = x1;
                x1 = x2;
                iterations++;
            }

            resultArea.setText(String.format("Root: %.6f\nIterations: %d", x1, iterations));

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private double evaluateFunction(double x, String equation) {
        String expr = equation.replace("^", "**").replace("x", String.valueOf(x));
        try {
            return new ExpressionEvaluator().eval(expr);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}
