import javax.swing.*;
import java.awt.*;

public class BisectionCalculator extends JDialog {
    private JTextField equationField, aField, bField;
    private JTextArea resultArea;

    public BisectionCalculator(JFrame parent) {
        super(parent, "Bisection Method", true);
        setSize(620, 520);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(59, 130, 246));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Bisection Method");
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
        aField = new JTextField("1", 10);
        bField = new JTextField("2", 10);

        addFormField(formPanel, formGbc, "Function f(x):", equationField, 0);
        addFormField(formPanel, formGbc, "Lower bound (a):", aField, 1);
        addFormField(formPanel, formGbc, "Upper bound (b):", bField, 2);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(37, 99, 235));
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
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            double tolerance = 1e-6;

            int iterations = 0;
            int maxIterations = 100;
            StringBuilder output = new StringBuilder();
            output.append("Iter\t a\t b\t c\t f(c)\n");

            while (Math.abs(b - a) > tolerance && iterations < maxIterations) {
                double c = (a + b) / 2;
                double fc = evaluateFunction(c, equation);
                double fa = evaluateFunction(a, equation);

                if (Double.isNaN(fc) || Double.isNaN(fa)) {
                    throw new RuntimeException("Invalid function evaluation");
                }

                output.append(String.format("%d\t%.8f\t%.8f\t%.8f\t%.6e\n", iterations + 1, a, b, c, fc));

                if (Math.abs(fc) < tolerance) {
                    output.append(String.format("\nRoot found: %.2f\nIterations: %d", c, iterations + 1));
                    resultArea.setText(output.toString());
                    return;
                }

                if (fa * fc < 0) {
                    b = c;
                } else {
                    a = c;
                }
                iterations++;
            }

            double root = (a + b) / 2;
            output.append(String.format("\nRoot: %.2f\nIterations: %d", root, iterations));
            resultArea.setText(output.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private double evaluateFunction(double x, String equation) {
        String expr = buildExpression(x, equation);
        try {
            return eval(expr);
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
                    if (prev == ')' || prev == 'x' || Character.isDigit(prev)) {
                        expr.append('*');
                    }
                }
                expr.append(c);
            }
        }
        return expr.toString();
    }

    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}
