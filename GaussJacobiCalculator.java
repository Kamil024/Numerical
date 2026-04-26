import javax.swing.*;
import java.awt.*;

public class GaussJacobiCalculator extends JDialog {
    private JTextField sizeField;
    private JTextArea matrixArea, resultArea;

    public GaussJacobiCalculator(JFrame parent) {
        super(parent, "Gauss-Jacobi Method", true);
        setSize(620, 620);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(239, 68, 68));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Gauss-Jacobi Method");
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
        matrixArea = new JTextArea("10,1,1,13\n1,10,1,14\n1,1,10,15", 4, 30);
        matrixArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(new JLabel("System size (n):"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(sizeField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 1;
        formGbc.gridwidth = 1;
        formPanel.add(new JLabel("Augmented Matrix:"), formGbc);
        formGbc.gridx = 1; formGbc.gridwidth = 2;
        formPanel.add(new JScrollPane(matrixArea), formGbc);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Arial", Font.BOLD, 16));
        calcButton.setBackground(new Color(220, 38, 38));
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
            int n = Integer.parseInt(sizeField.getText());
            double tolerance = 0.001;
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
            StringBuilder output = new StringBuilder();
            output.append("Iter");
            for (int i = 0; i < n; i++) {
                output.append(String.format("\tx%d", i + 1));
            }
            output.append("\tmaxDiff\n");

            while (iterations < maxIterations) {
                double[] xNew = new double[n];

                for (int i = 0; i < n; i++) {
                    double sum = b[i];
                    for (int j = 0; j < n; j++) {
                        if (i != j) {
                            sum -= A[i][j] * x[j];
                        }
                    }
                    xNew[i] = sum / A[i][i];
                }

                double maxDiff = 0;
                for (int i = 0; i < n; i++) {
                    maxDiff = Math.max(maxDiff, Math.abs(xNew[i] - x[i]));
                }

                output.append(String.format("%d", iterations + 1));
                for (int i = 0; i < n; i++) {
                    output.append(String.format("\t%.6f", xNew[i]));
                }
                output.append(String.format("\t%.6e\n", maxDiff));

                x = xNew;
                iterations++;

                if (maxDiff < tolerance) {
                    break;
                }
            }

            output.append("\nSolution:\n");
            for (int i = 0; i < n; i++) {
                output.append(String.format("x%d = %.2f\n", i + 1, x[i]));
            }
            output.append(String.format("Iterations: %d", iterations));
            resultArea.setText(output.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
}
