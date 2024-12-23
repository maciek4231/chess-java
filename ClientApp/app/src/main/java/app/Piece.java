package app;

import javax.swing.JButton;

public class Piece {

    PieceType type;
    Coords coords;
    CustomButton button;
    Board board;

    static double xScale = 1;
    static double yScale = 1;

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
        button.setIcon(IconLoader.getPieceIcon(type));
        button.setBounds((int) (coords.getRelX(board.getIsWhite()) * 128 * xScale), (int) (coords.getRelY(board.getIsWhite()) * 128 * yScale), (int) (128 * xScale), (int) (128 * yScale));
        button.addActionListener(e -> {
            board.selectPiece(coords);
        });
    }

    public void move(Coords coords) {
        this.coords = coords;
        button.setBounds((int) (coords.getRelX(board.getIsWhite()) * 128 * xScale), (int) (coords.getRelY(board.getIsWhite()) * 128 * yScale), (int) (128 * xScale), (int) (128 * yScale));
    }

    public void changeType(PieceType type) {
        this.type = type;
        button.setIcon(IconLoader.getPieceIcon(type));
    }

    public void resize(double xScale, double yScale) {
        button.setBounds((int) (coords.getRelX(board.getIsWhite())*128 * xScale), (int) (coords.getRelY(board.getIsWhite())*128 * yScale), (int) (128 * xScale), (int) (128 * yScale));
        button.setIcon(IconLoader.getPieceIcon(type));
    }

    public static void staticResize(double xScale, double yScale) {
        Piece.xScale = xScale;
        Piece.yScale = yScale;
    }
}
