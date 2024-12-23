package app;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Move {
    Coords from;
    Coords to;
    Board board;
    CustomButton button;
    protected static double xScale = 1.0;
    protected static double yScale = 1.0;

    public Move(Board board, Coords from, Coords to) {
        if (board == null || from == null || to == null) {
            throw new NullPointerException("Board, from, and to cannot be null");
        }
        this.board = board;
        this.from = from;
        this.to = to;

        createButton();
    }

    public Coords getFrom() {
        return from;
    }

    public Coords getTo() {
        return to;
    }

    public JButton getButton() {
        return button;
    }

    protected void createButton() {
        button = new CustomButton();
        button.setIcon(getButtonIcon());
        button.setBounds((int) (to.getRelX(board.getIsWhite()) * 128 * xScale), (int) (to.getRelY(board.getIsWhite()) * 128 * yScale), (int) (128 * xScale), (int) (128 * yScale));
        button.addActionListener(e -> {
            board.clientMove(from, to);
        });
    }

    protected ImageIcon getButtonIcon() {
        if (board.isOccupied(to)) {
            return IconLoader.getAttackIcon();
        }
        return IconLoader.getMoveIcon();
    }

    public void resize(double xScale, double yScale) {
        button.setBounds((int) (to.getRelX(board.getIsWhite()) * 128 * xScale), (int) (to.getRelY(board.getIsWhite()) * 128 * yScale), (int) (128 * xScale), (int) (128 * yScale));
        button.setIcon(getButtonIcon());
    }

    public static void staticResize(double x, double y) {
        xScale = x;
        yScale = y;
    }
}
