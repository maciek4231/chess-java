package app;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class YesNoWindow extends PopUpWindow {

    Board board;

    JLabel label;
    protected JButton noButton;
    protected JButton yesButton;

    public YesNoWindow(Board board, String message)
    {
        this.board = board;

        panel = new JLayeredPane();
        panel.setLayout(null);
        panel.setBounds((int) (262 * xScale), (int) (457 * yScale), (int) (500 * xScale), (int) (150 * yScale));
        panel.setBackground((java.awt.Color.WHITE));
        panel.setOpaque(true);

        label = new JLabel(message);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (50 * yScale));
        panel.add(label, 1);

        noButton = new JButton("No");
        noButton.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (190 * xScale), (int) (50 * yScale));
        panel.add(noButton, 1);

        yesButton = new JButton("Yes");
        yesButton.setBounds((int) (260 * xScale), (int) (75 * yScale), (int) (190 * xScale), (int) (50 * yScale));
        panel.add(yesButton, 1);

        panel.setVisible(true);
    }

    public JLayeredPane getPanel() {
        return panel;
    }

    public void resize(double xScale, double yScale) {
        panel.setBounds((int) (262 * xScale), (int) (457 * yScale), (int) (500 * xScale), (int) (150 * yScale));
        label.setBounds((int) (50 * xScale), (int) (25 * yScale), (int) (400 * xScale), (int) (50 * yScale));
        noButton.setBounds((int) (50 * xScale), (int) (75 * yScale), (int) (190 * xScale), (int) (50 * yScale));
        yesButton.setBounds((int) (260 * xScale), (int) (75 * yScale), (int) (190 * xScale), (int) (50 * yScale));
    }

}
