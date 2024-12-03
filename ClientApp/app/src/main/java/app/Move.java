package app;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Move {
    Coords from;
    Coords to;
    Board board;
    CustomButton button;

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
        button.setBounds(to.getRelX(board.getIsWhite())*128, to.getRelY(board.getIsWhite())*128, 128, 128);
        button.addActionListener(e -> {
            board.clientMove(from, to);
        });
    }

    private ImageIcon getButtonIcon() {
        if (board.isOccupied(to))
        {
            return new ImageIcon(getClass().getResource("/attack.png"));
        }
        return new ImageIcon(getClass().getResource("/move.png"));
    }
}
