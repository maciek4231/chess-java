package app;

public class RequestButton {
    protected static double xScale = 1;
    protected static double yScale = 1;
    protected CustomButton button;
    Board board;
    int xPos;
    int yPos;

    public RequestButton(Board board, int xPos, int yPos) {
        this.board = board;
        this.xPos = xPos;
        this.yPos = yPos;
        button = new CustomButton();
        button.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (128 * yScale));
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
        button.setIcon(IconLoader.getSurrenderActiveIcon());
    }
}
