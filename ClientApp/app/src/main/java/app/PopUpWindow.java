package app;

import javax.swing.JLayeredPane;

public class PopUpWindow {
    protected JLayeredPane panel;

    protected static double xScale = 1;
    protected static double yScale = 1;

    public JLayeredPane getPanel() {
        return panel;
    }

    public static void staticResize(double xScale, double yScale)
    {
        PopUpWindow.xScale = xScale;
        PopUpWindow.yScale = yScale;
    }

    public void hidePanel() {
        panel.setVisible(false);
    }

    public void resize(double xScale, double yScale) {
        // Method should be overridden in subclasses
    }
}
