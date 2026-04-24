import javax.swing.*;
import java.awt.*;

public class SecantCalculator extends JDialog {
    private JTextField equationField, x0Field, x1Field;
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
            double tolerance = 0.0001;

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

            while (iterations < 100) {
                if (Math.abs(f1 - f0) < 1e-10) {
                    resultArea.setText("Error: Division by zero in secant formula");
                    return;
                }

                x2 = x1 - (f1 * (x1 - x0)) / (f1 - f0);
                double f2 = evaluateFunction(x2, equation);

                iterations++;

                if (iterations > 1) {
                    ea = Math.abs(x2 - prevX2);
                    if (ea <= tolerance) {
                        resultArea.setText(String.format("Root found: %.2f\nIterations: %d", x2, iterations));
                        return;
                    }
                }

                x0 = x1;
                f0 = f1;
                x1 = x2;
                f1 = f2;
                prevX2 = x2;
            }

            resultArea.setText(String.format("Root: %.2f\nIterations: %d", x2, iterations));

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
