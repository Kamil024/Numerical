import javax.swing.*;
import java.awt.*;

public class GaussSeidelCalculator extends JDialog {
    private JTextField sizeField;
    private JTextArea matrixArea, resultArea;

    public GaussSeidelCalculator(JFrame parent) {
        super(parent, "Gauss-Seidel Method", true);
        setSize(620, 620);
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
        calcButton.setBackground(new Color(79, 70, 229));
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
            double[] prevX = new double[n];
            double[] ea = new double[n];

            for (int i = 0; i < n; i++) {
                x[i] = 0;
                prevX[i] = 0;
                ea[i] = Double.MAX_VALUE;
            }

            int iteration = 0;
            boolean converged;
            StringBuilder output = new StringBuilder();
            output.append("Iter");
            for (int i = 0; i < n; i++) {
                output.append(String.format("\tx%d", i + 1));
            }
            output.append("\tmaxError\n");

            do {
                iteration++;
                for (int i = 0; i < n; i++) {
                    prevX[i] = x[i];
                }

                for (int i = 0; i < n; i++) {
                    double sum = b[i];
                    for (int j = 0; j < n; j++) {
                        if (i != j) {
                            sum -= A[i][j] * x[j];
                        }
                    }
                    x[i] = sum / A[i][i];
                }

                for (int i = 0; i < n; i++) {
                    ea[i] = Math.abs(x[i] - prevX[i]);
                }

                double maxError = 0;
                output.append(String.format("%d", iteration));
                for (int i = 0; i < n; i++) {
                    output.append(String.format("\t%.6f", x[i]));
                    maxError = Math.max(maxError, ea[i]);
                }
                output.append(String.format("\t%.6e\n", maxError));

                converged = true;
                for (int i = 0; i < n; i++) {
                    if (ea[i] > tolerance) {
                        converged = false;
                        break;
                    }
                }

                if (iteration >= 100) {
                    break;
                }
            } while (!converged);

            output.append("\nSolution:\n");
            for (int i = 0; i < n; i++) {
                output.append(String.format("x%d = %.2f\n", i + 1, x[i]));
            }
            output.append(String.format("Iterations: %d", iteration));
            resultArea.setText(output.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
}
