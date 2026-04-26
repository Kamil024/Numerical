import javax.swing.*;
import java.awt.*;

public class TrapezoidalCalculator extends JDialog {
    private JTextField equationField, aField, bField, nField;
    private JTextArea resultArea;

    public TrapezoidalCalculator(JFrame parent) {
        super(parent, "Trapezoidal Rule", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(20, 184, 166));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Trapezoidal Rule");
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
        nField = new JTextField("4", 10);

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

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(13, 148, 136));
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

            double h = (b - a) / n;
            double sum = evaluateFunction(a, equation) + evaluateFunction(b, equation);
            StringBuilder output = new StringBuilder();
            output.append("i\tx\tf(x)\tcoeff\tterm\n");
            output.append(String.format("0\t%.6f\t%.6e\t1\t%.6e\n", a, evaluateFunction(a, equation), evaluateFunction(a, equation)));

            for (int i = 1; i < n; i++) {
                double x = a + i * h;
                double fx = evaluateFunction(x, equation);
                output.append(String.format("%d\t%.6f\t%.6e\t2\t%.6e\n", i, x, fx, 2 * fx));
                sum += 2 * fx;
            }

            output.append(String.format("%d\t%.6f\t%.6e\t1\t%.6e\n", n, b, evaluateFunction(b, equation), evaluateFunction(b, equation)));
            double integral = (h / 2) * sum;
            output.append(String.format("\nIntegral ≈ %.2f", integral));
            resultArea.setText(output.toString());

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
