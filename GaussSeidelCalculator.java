import javax.swing.*;
import java.awt.*;

public class GaussSeidelCalculator extends JDialog {
    private JTextField sizeField, toleranceField;
    private JTextArea matrixArea, resultArea;

    public GaussSeidelCalculator(JFrame parent) {
        super(parent, "Gauss-Seidel Method", true);
        setSize(600, 600);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(99, 102, 241));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Gauss-Seidel Method");
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

        sizeField = new JTextField("3", 10);
        toleranceField = new JTextField("0.0001", 10);
        matrixArea = new JTextArea("10,1,1,13\n1,10,1,14\n1,1,10,15", 4, 30);
        matrixArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(new JLabel("System size (n):"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(sizeField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 1;
        formPanel.add(new JLabel("Augmented Matrix:"), formGbc);
        formGbc.gridx = 1; formGbc.gridwidth = 2;
        formPanel.add(new JScrollPane(matrixArea), formGbc);

        formGbc.gridx = 0; formGbc.gridy = 2; formGbc.gridwidth = 1;
        formPanel.add(new JLabel("Tolerance:"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(toleranceField, formGbc);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(79, 70, 229));
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

    private void calculate() {
        try {
            int n = Integer.parseInt(sizeField.getText());
            double tolerance = Double.parseDouble(toleranceField.getText());
            String[] rows = matrixArea.getText().trim().split("\n");

            double[][] A = new double[n][n];
            double[] b = new double[n];

            for (int i = 0; i < n; i++) {
                String[] values = rows[i].split(",");
                for (int j = 0; j < n; j++) {
                    A[i][j] = Double.parseDouble(values[j].trim());
                }
                b[i] = Double.parseDouble(values[n].trim());
            }

            double[] x = new double[n];
            int maxIterations = 100;
            int iterations = 0;

            while (iterations < maxIterations) {
                double[] xOld = x.clone();

                for (int i = 0; i < n; i++) {
                    double sum = b[i];
                    for (int j = 0; j < n; j++) {
                        if (i != j) {
                            sum -= A[i][j] * x[j];
                        }
                    }
                    x[i] = sum / A[i][i];
                }

                double maxDiff = 0;
                for (int i = 0; i < n; i++) {
                    maxDiff = Math.max(maxDiff, Math.abs(x[i] - xOld[i]));
                }

                iterations++;

                if (maxDiff < tolerance) {
                    break;
                }
            }

            StringBuilder solution = new StringBuilder("Solution:\n");
            for (int i = 0; i < n; i++) {
                solution.append(String.format("x%d = %.6f\n", i + 1, x[i]));
            }
            solution.append(String.format("Iterations: %d", iterations));
            resultArea.setText(solution.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
}
