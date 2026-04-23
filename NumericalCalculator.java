import javax.swing.*;
import java.awt.*;

public class NumericalCalculator extends JFrame {

    public NumericalCalculator() {
        setTitle("Numerical Methods Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(30, 41, 59));

        JLabel titleLabel = new JLabel("Numerical Methods Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Select a formula to compute", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(203, 213, 225));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        String[] formulas = {
            "Bisection Method",
            "Regula Falsi",
            "Secant Method",
            "Newton Method",
            "Gauss-Jacobi",
            "Gauss-Seidel",
            "Simpson's Rule",
            "Trapezoidal Rule"
        };

        Color[] colors = {
            new Color(59, 130, 246),   // Blue
            new Color(34, 197, 94),    // Green
            new Color(168, 85, 247),   // Purple
            new Color(249, 115, 22),   // Orange
            new Color(239, 68, 68),    // Red
            new Color(99, 102, 241),   // Indigo
            new Color(236, 72, 153),   // Pink
            new Color(20, 184, 166)    // Teal
        };

        for (int i = 0; i < formulas.length; i++) {
            final int index = i;
            JButton button = createFormulaButton(formulas[i], colors[i]);
            button.addActionListener(e -> openCalculator(index));
            mainPanel.add(button);
        }

        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(30, 41, 59));
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createFormulaButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }
        });

        return button;
    }

    private void openCalculator(int index) {
        switch (index) {
            case 0: new BisectionCalculator(this).setVisible(true); break;
            case 1: new RegulaFalsiCalculator(this).setVisible(true); break;
            case 2: new SecantCalculator(this).setVisible(true); break;
            case 3: new NewtonCalculator(this).setVisible(true); break;
            case 4: new GaussJacobiCalculator(this).setVisible(true); break;
            case 5: new GaussSeidelCalculator(this).setVisible(true); break;
            case 6: new SimpsonCalculator(this).setVisible(true); break;
            case 7: new TrapezoidalCalculator(this).setVisible(true); break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new NumericalCalculator().setVisible(true);
        });
    }
}
