package app;

import javax.swing.JButton;

public class Piece {

    PieceType type;
    int x;
    int y;
    JButton button;

    public Piece(PieceType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;

        button = new JButton("");
        button.setIcon(this.getPieceImage(this.type));
        button.setBounds(x*128, y*128, 128, 128);
        button.setBackground(new java.awt.Color(0, 0, 0, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(null);
        button.addActionListener(e -> {
            System.out.println(type.toString());
        });
    }

    public JButton getButton() {
        return button;
    }

    javax.swing.ImageIcon getPieceImage(PieceType type) {
        switch (type) {
            case W_PAWN:
                return new javax.swing.ImageIcon(getClass().getResource("/white-pawn.png"));
            case W_ROOK:
                return new javax.swing.ImageIcon(getClass().getResource("/white-rook.png"));
            case W_KNIGHT:
                return new javax.swing.ImageIcon(getClass().getResource("/white-knight.png"));
            case W_BISHOP:
                return new javax.swing.ImageIcon(getClass().getResource("/white-bishop.png"));
            case W_QUEEN:
                return new javax.swing.ImageIcon(getClass().getResource("/white-queen.png"));
            case W_KING:
                return new javax.swing.ImageIcon(getClass().getResource("/white-king.png"));
            case B_PAWN:
                return new javax.swing.ImageIcon(getClass().getResource("/black-pawn.png"));
            case B_ROOK:
                return new javax.swing.ImageIcon(getClass().getResource("/black-rook.png"));
            case B_KNIGHT:
                return new javax.swing.ImageIcon(getClass().getResource("/black-knight.png"));
            case B_BISHOP:
                return new javax.swing.ImageIcon(getClass().getResource("/black-bishop.png"));
            case B_QUEEN:
                return new javax.swing.ImageIcon(getClass().getResource("/black-queen.png"));
            case B_KING:
                return new javax.swing.ImageIcon(getClass().getResource("/black-king.png"));
            default:
                return null;
        }
    }
}
