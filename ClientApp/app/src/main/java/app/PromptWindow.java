package app;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class PromptWindow {
    JLayeredPane panel;
    Game game;

    public PromptWindow(Game game, String message) {
        this.game = game;

        panel = new JLayeredPane();
        panel.setLayout(null);
        panel.setBounds(262, 457, 500, 150);
        panel.setBackground((java.awt.Color.WHITE));
        panel.setOpaque(true);

        JLabel label = new JLabel(message);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBounds(50, 25, 400, 50);
        panel.add(label, 1);

        JButton button = new JButton("OK");
        button.setBounds(50, 75, 400, 50);
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
}
