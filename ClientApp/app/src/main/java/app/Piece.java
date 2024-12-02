package app;

import javax.swing.JButton;

public class Piece {

    PieceType type;
    Coords coords;
    CustomButton button;
    Board board;

    public Piece(Board board, PieceType type, Coords coords) {
        this.board = board;
        this.type = type;
        this.coords = coords;

        createButton();
    }

    public Piece(Board board, PieceType type, int x, int y) {
        this.board = board;
        this.type = type;
        this.coords = new Coords(x, y);

        createButton();
    }

    public JButton getButton() {
        return button;
    }

    private void createButton() {
        button = new CustomButton();
        button.setIcon(this.getPieceImage(this.type));
        button.setBounds(coords.getX()*128, coords.getY()*128, 128, 128);
        button.addActionListener(e -> {
            board.selectPiece(coords);
        });
    }

    public void move(Coords coords) {
        this.coords = coords;
        button.setBounds(coords.getX()*128, coords.getY()*128, 128, 128);
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
