package app;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class PromptWindow {
    JLayeredPane panel;
    Game game;
    JLabel label;
    JButton button;

    static double xScale = 1;
    static double yScale = 1;

    public PromptWindow(Game game, String message) {
        this.game = game;

        panel = new JLayeredPane();
        panel.setLayout(null);
        panel.setBounds((int) (262 * xScale), (int) (457 * yScale), (int) (500 * xScale), (int) (150 * yScale));
        panel.setBackground((java.awt.Color.WHITE));
        panel.setOpaque(true);

        label = new JLabel(message);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (50 * yScale));
        panel.add(label, 1);

        button = new JButton("OK");
        button.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (400 * xScale), (int) (50 * yScale));
        panel.add(button, 1);
        button.addActionListener(e -> {
            panel.setVisible(false);
            game.closePromptWindow();
        });

        panel.setVisible(true);
    }

    public JLayeredPane getPanel() {
        return panel;
    }

    public void resize(double xScale, double yScale) {
        panel.setBounds((int) (262 * xScale), (int) (457 * yScale), (int) (500 * xScale), (int) (150 * yScale));
        label.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (50 * yScale));
        button.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (400 * xScale), (int) (50 * yScale));
    }

    public static void staticResize(double xScale, double yScale)
    {
        PromptWindow.xScale = xScale;
        PromptWindow.yScale = yScale;
    }
}
