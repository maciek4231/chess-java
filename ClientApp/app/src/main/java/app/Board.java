package app;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import java.awt.Image;


public class Board {

    JLayeredPane jPane;

    HashMap<Coords, Piece> pieces;

    public Board()
    {
        jPane = new JLayeredPane();
        pieces = new HashMap<Coords, Piece>();

        createPane();
        createPieces();
    }

    public JLayeredPane getPane()
    {
        return jPane;
    }

    private void createPane()
    {
        ImageIcon icon = new ImageIcon(Main.class.getResource("/board.png"));
        Image image = icon.getImage();

        JLabel label = new JLabel(new ImageIcon(image));
        label.setBounds(0, 0, 1024, 1024);

        jPane.add(label);
        jPane.setLayer(label, 0);
    }

    private void createPieces()     // TODO: Remove this method
    {
        pieces.put(new Coords(0, 0), new Piece(PieceType.B_ROOK, 0, 0));
        pieces.put(new Coords(7, 0), new Piece(PieceType.B_ROOK, 7, 0));
        pieces.put(new Coords(1, 0), new Piece(PieceType.B_KNIGHT, 1, 0));
        pieces.put(new Coords(6, 0), new Piece(PieceType.B_KNIGHT, 6, 0));
        pieces.put(new Coords(2, 0), new Piece(PieceType.B_BISHOP, 2, 0));
        pieces.put(new Coords(5, 0), new Piece(PieceType.B_BISHOP, 5, 0));
        pieces.put(new Coords(3, 0), new Piece(PieceType.B_QUEEN, 3, 0));
        pieces.put(new Coords(4, 0), new Piece(PieceType.B_KING, 4, 0));
        pieces.put(new Coords(0, 1), new Piece(PieceType.B_PAWN, 0, 1));
        pieces.put(new Coords(1, 1), new Piece(PieceType.B_PAWN, 1, 1));
        pieces.put(new Coords(2, 1), new Piece(PieceType.B_PAWN, 2, 1));
        pieces.put(new Coords(3, 1), new Piece(PieceType.B_PAWN, 3, 1));
        pieces.put(new Coords(4, 1), new Piece(PieceType.B_PAWN, 4, 1));
        pieces.put(new Coords(5, 1), new Piece(PieceType.B_PAWN, 5, 1));
        pieces.put(new Coords(6, 1), new Piece(PieceType.B_PAWN, 6, 1));
        pieces.put(new Coords(7, 1), new Piece(PieceType.B_PAWN, 7, 1));

        pieces.put(new Coords(0, 7), new Piece(PieceType.W_ROOK, 0, 7));
        pieces.put(new Coords(7, 7), new Piece(PieceType.W_ROOK, 7, 7));
        pieces.put(new Coords(1, 7), new Piece(PieceType.W_KNIGHT, 1, 7));
        pieces.put(new Coords(6, 7), new Piece(PieceType.W_KNIGHT, 6, 7));
        pieces.put(new Coords(2, 7), new Piece(PieceType.W_BISHOP, 2, 7));
        pieces.put(new Coords(5, 7), new Piece(PieceType.W_BISHOP, 5, 7));
        pieces.put(new Coords(3, 7), new Piece(PieceType.W_QUEEN, 3, 7));
        pieces.put(new Coords(4, 7), new Piece(PieceType.W_KING, 4, 7));
        pieces.put(new Coords(0, 6), new Piece(PieceType.W_PAWN, 0, 6));
        pieces.put(new Coords(1, 6), new Piece(PieceType.W_PAWN, 1, 6));
        pieces.put(new Coords(2, 6), new Piece(PieceType.W_PAWN, 2, 6));
        pieces.put(new Coords(3, 6), new Piece(PieceType.W_PAWN, 3, 6));
        pieces.put(new Coords(4, 6), new Piece(PieceType.W_PAWN, 4, 6));
        pieces.put(new Coords(5, 6), new Piece(PieceType.W_PAWN, 5, 6));
        pieces.put(new Coords(6, 6), new Piece(PieceType.W_PAWN, 6, 6));
        pieces.put(new Coords(7, 6), new Piece(PieceType.W_PAWN, 7, 6));

        for (Entry<Coords, Piece> entry : pieces.entrySet())
        {
            Piece piece = entry.getValue();
            jPane.add(piece.getButton());
            jPane.setLayer(piece.getButton(), 1);
        }

        Move mov = new Move(new Coords(1, 1), new Coords(1, 3));
        jPane.add(mov.getButton());
        jPane.setLayer(mov.getButton(), 2);
    }
}
