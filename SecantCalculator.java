import javax.swing.*;
import java.awt.*;

public class SecantCalculator extends JDialog {
    private JTextField equationField, x0Field, x1Field;
    private JTextArea resultArea;

    public SecantCalculator(JFrame parent) {
        super(parent, "Secant Method", true);
        setSize(620, 520);
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

        addFormField(formPanel, formGbc, "Function f(x):", equationField, 0);
        addFormField(formPanel, formGbc, "Initial guess x₀:", x0Field, 1);
        addFormField(formPanel, formGbc, "Initial guess x₁:", x1Field, 2);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(147, 51, 234));
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
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tolerance = 1e-6;

            double f0 = evaluateFunction(x0, equation);
            double f1 = evaluateFunction(x1, equation);

            if (Double.isNaN(f0) || Double.isNaN(f1)) {
                resultArea.setText("Error: Invalid function evaluation at initial points");
                return;
            }

            int iterations = 0;
            double x2 = Double.NaN;
            double prevX2 = Double.NaN;
            double ea = Double.NaN;
            StringBuilder output = new StringBuilder();
            output.append("Iter\t x0\t x1\t x2\t f(x2)\t Error\n");

            while (iterations < 100) {
                if (Math.abs(f1 - f0) < 1e-10) {
                    resultArea.setText("Error: Division by zero in secant formula");
                    return;
                }

                x2 = x1 - (f1 * (x1 - x0)) / (f1 - f0);
                double f2 = evaluateFunction(x2, equation);
                ea = (iterations == 0) ? Double.NaN : Math.abs(x2 - prevX2);

                output.append(String.format("%d\t%.8f\t%.8f\t%.8f\t%.6e\t%s\n", iterations + 1, x0, x1, x2, f2,
                        (iterations == 0 ? "-" : String.format("%.6e", ea))));

                iterations++;
                if (iterations > 1 && ea <= tolerance) {
                    output.append(String.format("\nRoot found: %.2f\nIterations: %d", x2, iterations));
                    resultArea.setText(output.toString());
                    return;
                }

                x0 = x1;
                f0 = f1;
                x1 = x2;
                f1 = f2;
                prevX2 = x2;
            }

            output.append(String.format("\nRoot: %.2f\nIterations: %d", x2, iterations));
            resultArea.setText(output.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private double evaluateFunction(double x, String equation) {
        String expr = buildExpression(x, equation);
        try {
            return new ExpressionEvaluator().eval(expr);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private String buildExpression(double x, String equation) {
        StringBuilder expr = new StringBuilder();
        String cleaned = equation.replace(" ", "");
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c == 'x') {
                if (expr.length() > 0) {
                    char prev = expr.charAt(expr.length() - 1);
                    if (prev == ')' || Character.isDigit(prev)) {
                        expr.append('*');
                    }
                }
                expr.append('(').append(x).append(')');
                if (i + 1 < cleaned.length()) {
                    char next = cleaned.charAt(i + 1);
                    if (next == '(' || next == 'x' || Character.isDigit(next)) {
                        expr.append('*');
                    }
                }
            } else {
                if (c == '(' && expr.length() > 0) {
                    char prev = expr.charAt(expr.length() - 1);
                    if (prev == ')' || Character.isDigit(prev)) {
                        expr.append('*');
                    }
                }
                expr.append(c);
            }
        }
        return expr.toString();
    }
}
