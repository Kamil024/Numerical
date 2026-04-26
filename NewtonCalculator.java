import javax.swing.*;
import java.awt.*;

public class NewtonCalculator extends JDialog {
    private JTextField equationField, x0Field;
    private JTextArea resultArea;

    public NewtonCalculator(JFrame parent) {
        super(parent, "Newton-Raphson Method", true);
        setSize(620, 560);
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
        x0Field = new JTextField("1.5", 10);

        addFormField(formPanel, formGbc, "Function f(x):", equationField, 0);
        addFormField(formPanel, formGbc, "Initial guess x₀:", x0Field, 1);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(234, 88, 12));
        calcButton.setForeground(Color.BLACK);
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
            double x = Double.parseDouble(x0Field.getText());
            double tolerance = 1e-6;

            int iterations = 0;
            int maxIterations = 100;
            StringBuilder output = new StringBuilder();
            output.append("Iter\t x\t f(x)\t f'(x)\t x_new\t Error\n");

            for (; iterations < maxIterations; iterations++) {
                double fx = evaluateFunction(x, equation);

                double h = Math.max(Math.abs(x) * 1e-8, 1e-8);
                double dfx = derivative(x, h);

                if (Math.abs(dfx) < 1e-12) {
                    resultArea.setText("Error: Derivative near zero");
                    return;
                }

                double xNew = x - fx / dfx;
                double error = Math.abs(xNew - x);
                output.append(String.format("%d\t%.8f\t%.6e\t%.6e\t%.8f\t%.6e\n", iterations + 1, x, fx, dfx, xNew, error));

                x = xNew;
                if (error < tolerance) {
                    output.append(String.format("\nRoot ≈ %.2f after %d iterations", x, iterations + 1));
                    resultArea.setText(output.toString());
                    return;
                }
            }

            output.append(String.format("\nStopped after %d iterations, last x ≈ %.2f", iterations, x));
            resultArea.setText(output.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private double derivative(double x, double h) {
        return (evaluateFunction(x + h, equationField.getText()) - evaluateFunction(x - h, equationField.getText())) / (2 * h);
    }

    private double evaluateFunction(double x, String equation) {
        String expr = equation.replace("^", "**").replaceAll("\\bx\\b", String.valueOf(x));
        try {
            double value = new ExpressionEvaluator().eval(expr);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new RuntimeException("Invalid expression result");
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Invalid expression: " + e.getMessage(), e);
        }
    }
}

