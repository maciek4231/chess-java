package app;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import java.awt.Image;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 1054);

        JLayeredPane board = new JLayeredPane();

        ImageIcon icon = new ImageIcon(Main.class.getResource("/board.png"));
        Image image = icon.getImage();

        JLabel label = new JLabel(new ImageIcon(image));
        label.setBounds(0, 0, 1024, 1024);

        board.add(label);

        ArrayList<Piece> pieces = new ArrayList<Piece>();

        pieces.add(new Piece(PieceType.B_ROOK, 0, 0));
        pieces.add(new Piece(PieceType.B_ROOK, 7, 0));
        pieces.add(new Piece(PieceType.B_KNIGHT, 1, 0));
        pieces.add(new Piece(PieceType.B_KNIGHT, 6, 0));
        pieces.add(new Piece(PieceType.B_BISHOP, 2, 0));
        pieces.add(new Piece(PieceType.B_BISHOP, 5, 0));
        pieces.add(new Piece(PieceType.B_QUEEN, 3, 0));
        pieces.add(new Piece(PieceType.B_KING, 4, 0));
        pieces.add(new Piece(PieceType.B_PAWN, 0, 1));
        pieces.add(new Piece(PieceType.B_PAWN, 1, 1));
        pieces.add(new Piece(PieceType.B_PAWN, 2, 1));
        pieces.add(new Piece(PieceType.B_PAWN, 3, 1));
        pieces.add(new Piece(PieceType.B_PAWN, 4, 1));
        pieces.add(new Piece(PieceType.B_PAWN, 5, 1));
        pieces.add(new Piece(PieceType.B_PAWN, 6, 1));
        pieces.add(new Piece(PieceType.B_PAWN, 7, 1));

        pieces.add(new Piece(PieceType.W_ROOK, 0, 7));
        pieces.add(new Piece(PieceType.W_ROOK, 7, 7));
        pieces.add(new Piece(PieceType.W_KNIGHT, 1, 7));
        pieces.add(new Piece(PieceType.W_KNIGHT, 6, 7));
        pieces.add(new Piece(PieceType.W_BISHOP, 2, 7));
        pieces.add(new Piece(PieceType.W_BISHOP, 5, 7));
        pieces.add(new Piece(PieceType.W_QUEEN, 3, 7));
        pieces.add(new Piece(PieceType.W_KING, 4, 7));
        pieces.add(new Piece(PieceType.W_PAWN, 0, 6));
        pieces.add(new Piece(PieceType.W_PAWN, 1, 6));
        pieces.add(new Piece(PieceType.W_PAWN, 2, 6));
        pieces.add(new Piece(PieceType.W_PAWN, 3, 6));
        pieces.add(new Piece(PieceType.W_PAWN, 4, 6));
        pieces.add(new Piece(PieceType.W_PAWN, 5, 6));
        pieces.add(new Piece(PieceType.W_PAWN, 6, 6));
        pieces.add(new Piece(PieceType.W_PAWN, 7, 6));

        for (Piece piece : pieces)
        {
            board.add(piece.getButton());
            board.setLayer(piece.getButton(), 1);
        }

        board.setLayer(label, 0);

        frame.getContentPane().add(board);
        frame.setVisible(true);
    }
}