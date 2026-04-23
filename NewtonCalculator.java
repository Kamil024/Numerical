import javax.swing.*;
import java.awt.*;

public class NewtonCalculator extends JDialog {
    private JTextField equationField, derivativeField, x0Field, toleranceField;
    private JTextArea resultArea;

    public NewtonCalculator(JFrame parent) {
        super(parent, "Newton-Raphson Method", true);
        setSize(600, 550);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(249, 115, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Newton-Raphson Method");
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
        derivativeField = new JTextField("3*x^2 - 1", 20);
        x0Field = new JTextField("1.5", 10);
        toleranceField = new JTextField("0.0001", 10);

        addFormField(formPanel, formGbc, "Function f(x):", equationField, 0);
        addFormField(formPanel, formGbc, "Derivative f'(x):", derivativeField, 1);
        addFormField(formPanel, formGbc, "Initial guess x₀:", x0Field, 2);
        addFormField(formPanel, formGbc, "Tolerance:", toleranceField, 3);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(234, 88, 12));
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
            String derivative = derivativeField.getText();
            double x = Double.parseDouble(x0Field.getText());
            double tolerance = Double.parseDouble(toleranceField.getText());

            int iterations = 0;
            int maxIterations = 100;

            while (iterations < maxIterations) {
                double fx = evaluateFunction(x, equation);
                double fpx = evaluateFunction(x, derivative);

                if (Math.abs(fpx) < 1e-10) {
                    resultArea.setText("Error: Derivative too close to zero");
                    return;
                }

                double xNew = x - fx / fpx;

                if (Math.abs(xNew - x) < tolerance) {
                    resultArea.setText(String.format("Root found: %.6f\nIterations: %d", xNew, iterations));
                    return;
                }

                x = xNew;
                iterations++;
            }

            resultArea.setText(String.format("Root: %.6f\nIterations: %d", x, iterations));

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
