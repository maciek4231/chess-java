package app;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Move {
    Coords from;
    Coords to;
    Board board;
    JButton button;

    public Move(Board board, Coords from, Coords to) {
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

    private void createButton() {
        button = new JButton("");
        button.setIcon(getButtonIcon());
        button.setBounds(to.getX()*128, to.getY()*128, 128, 128);
        button.setBackground(new java.awt.Color(0, 0, 0, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(null);
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
