package app;

import javax.swing.Icon;

public class RequestButton {
    protected static double xScale = 1;
    protected static double yScale = 1;
    protected CustomButton button;
    Board board;
    int xPos;
    int yPos;

    protected boolean active = false;

    public RequestButton(Board board, int xPos, int yPos) {
        this.board = board;
        this.xPos = xPos;
        this.yPos = yPos;
        button = new CustomButton();
        button.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (128 * yScale));
        button.setIcon(getIcon());
    }

    public CustomButton getButton() {
        return button;
    }

    public static void staticResize(double xScale, double yScale) {
        RequestButton.xScale = xScale;
        RequestButton.yScale = yScale;
    }

    public void resize(double xScale, double yScale) {
        button.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (128 * yScale));
        button.setIcon(getIcon());
    }

    public void setActive(boolean active) {
        this.active = active;
        button.setIcon(getIcon());
    }

    Icon getIcon() {
        return IconLoader.getPieceIcon(PieceType.B_KNIGHT);
    }
}
