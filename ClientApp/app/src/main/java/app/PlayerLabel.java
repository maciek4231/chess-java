package app;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class PlayerLabel {
    static double xScale = 1;
    static double yScale = 1;
    int xPos;
    int yPos;

    JLabel label;

    public PlayerLabel(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;

        label = new JLabel("Guest", SwingConstants.CENTER);
        label.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (64 * yScale));
        correctFontSize();
    }

    public static void staticResize(double xScale, double yScale) {
        PlayerLabel.xScale = xScale;
        PlayerLabel.yScale = yScale;
    }

    public void resize(double xScale, double yScale) {
        label.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (64 * yScale));
        correctFontSize();
    }

    public JLabel getLabel() {
        return label;
    }

    public void move(int x, int y) {
        xPos = x;
        yPos = y;
        label.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (64 * yScale));
    }

    public void setText(String text) {
        label.setText(text);
        correctFontSize();
    }

    void correctFontSize()
    {
        double factor = 0.9;
        Font font = label.getFont();
        int width = (int) (factor * font.getSize() * (128 * xScale) / label.getFontMetrics(font).stringWidth(label.getText()));
        int maxHeight = (int) (64 * yScale);
        int fontSize = Math.min(width, maxHeight);
        label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, fontSize));
    }

    public void setVisible(boolean visible) {
        label.setVisible(visible);
    }
}
