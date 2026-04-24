import javax.swing.*;
import java.awt.*;

public class SimpsonCalculator extends JDialog {
    private JTextField equationField, aField, bField, nField;
    private JRadioButton rule13, rule38;
    private JTextArea resultArea;

    public SimpsonCalculator(JFrame parent) {
        super(parent, "Simpson's Rule", true);
        setSize(600, 600);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 72, 153));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Simpson's Rule");
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

        equationField = new JTextField("x^2", 20);
        aField = new JTextField("0", 10);
        bField = new JTextField("2", 10);
        nField = new JTextField("6", 10);

        formGbc.gridx = 0; formGbc.gridy = 0; formGbc.gridwidth = 1;
        formPanel.add(new JLabel("Function f(x):"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(equationField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 1;
        formPanel.add(new JLabel("Lower limit (a):"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(aField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 2;
        formPanel.add(new JLabel("Upper limit (b):"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(bField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 3;
        formPanel.add(new JLabel("Number of intervals (n):"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(nField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 4;
        formPanel.add(new JLabel("Rule:"), formGbc);
        formGbc.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rule13 = new JRadioButton("Simpson's 1/3", true);
        rule38 = new JRadioButton("Simpson's 3/8");
        ButtonGroup group = new ButtonGroup();
        group.add(rule13);
        group.add(rule38);
        radioPanel.add(rule13);
        radioPanel.add(rule38);
        formPanel.add(radioPanel, formGbc);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(219, 39, 119));
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

    private void calculate() {
        try {
            String equation = equationField.getText();
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            int n = Integer.parseInt(nField.getText());

            if (rule13.isSelected()) {
                if (n % 2 != 0) {
                    resultArea.setText("Error: n must be even for Simpson's 1/3 rule");
                    return;
                }

                double h = (b - a) / n;
                double sum = evaluateFunction(a, equation) + evaluateFunction(b, equation);

                for (int i = 1; i < n; i++) {
                    double x = a + i * h;
                    double fx = evaluateFunction(x, equation);
                    sum += (i % 2 == 0 ? 2 : 4) * fx;
                }

                double integral = (h / 3) * sum;
                resultArea.setText(String.format("Integral ≈ %.6f\n(Simpson's 1/3 Rule)", integral));
            } else {
                if (n % 3 != 0) {
                    resultArea.setText("Error: n must be divisible by 3 for Simpson's 3/8 rule");
                    return;
                }

                double h = (b - a) / n;
                double sum = evaluateFunction(a, equation) + evaluateFunction(b, equation);

                for (int i = 1; i < n; i++) {
                    double x = a + i * h;
                    double fx = evaluateFunction(x, equation);
                    sum += (i % 3 == 0 ? 2 : 3) * fx;
                }

                double integral = (3 * h / 8) * sum;
                resultArea.setText(String.format("Integral ≈ %.6f\n(Simpson's 3/8 Rule)", integral));
            }

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
