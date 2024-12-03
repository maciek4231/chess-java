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
        button.setIcon(PieceIcons.getIcon(type));
        button.setBounds(coords.getRelX(board.getIsWhite())*128, coords.getRelY(board.getIsWhite())*128, 128, 128);
        button.addActionListener(e -> {
            board.selectPiece(coords);
        });
    }

    public void move(Coords coords) {
        this.coords = coords;
        button.setBounds(coords.getRelX(board.getIsWhite())*128, coords.getRelY(board.getIsWhite())*128, 128, 128);
    }

    public void changeType(PieceType type) {
        this.type = type;
        button.setIcon(PieceIcons.getIcon(type));
    }
}
