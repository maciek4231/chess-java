package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import java.awt.Image;


public class Board {

    MessageHandler messageHandler;

    JLayeredPane jPane;

    HashMap<Coords, Piece> pieces;

    ArrayList<Move> availableMoves;
    ArrayList<Move> selectableMoves;

    public Board()
    {
        jPane = new JLayeredPane();
        pieces = new HashMap<Coords, Piece>();

        availableMoves = new ArrayList<Move>();
        selectableMoves = new ArrayList<Move>();

        createPane();
        createPieces();
    }

    public void setMessageHandler(MessageHandler messageHandler)
    {
        this.messageHandler = messageHandler;
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

    public void addAvailableMove(Coords from, Coords to)
    {
        availableMoves.add(new Move(this, from, to));
    }

    public void selectPiece(Coords coords)
    {
        for (Move move : selectableMoves)
        {
            move.getButton().setVisible(false);
            jPane.remove(move.getButton());
        }
        Piece piece = pieces.get(coords);
        if (piece != null)
        {
            for (Move move : availableMoves)
            {
                if (move.getFrom().equals(coords))
                {
                    selectableMoves.add(move);
                    jPane.add(move.getButton());
                    jPane.setLayer(move.getButton(), 2);
                    move.getButton().setVisible(true);
                }
            }
        }
    }

    public boolean isOccupied(Coords coords)
    {
        return pieces.containsKey(coords);
    }

    public void clientMove(Coords from, Coords to)
    {
        makeMove(from, to);
        clearMoves();
        // TODO: Send move to server
    }

    public void makeMove(Coords from, Coords to)
    {
        Piece piece = pieces.get(from);
        piece.move(to);
        if (isOccupied(to))
        {
            jPane.remove(pieces.get(to).getButton());
            pieces.remove(to);
        }
        pieces.remove(from);
        pieces.put(to, piece);
    }

    private void clearMoves() {
        for (Move move : availableMoves)
        {
            move.getButton().setVisible(false);
            jPane.remove(move.getButton());
        }
        availableMoves.clear();
        selectableMoves.clear();
    }

    public void addPiece(PieceType type, Coords coords)
    {
        Piece piece = new Piece(this, type, coords);
        pieces.put(coords, piece);
        jPane.add(piece.getButton());
        jPane.setLayer(piece.getButton(), 1);
    }

    private void createPieces()     // TODO: Remove this method
    {
        pieces.put(new Coords(0, 0), new Piece(this, PieceType.B_ROOK, 0, 0));
        pieces.put(new Coords(7, 0), new Piece(this, PieceType.B_ROOK, 7, 0));
        pieces.put(new Coords(1, 0), new Piece(this, PieceType.B_KNIGHT, 1, 0));
        pieces.put(new Coords(6, 0), new Piece(this, PieceType.B_KNIGHT, 6, 0));
        pieces.put(new Coords(2, 0), new Piece(this, PieceType.B_BISHOP, 2, 0));
        pieces.put(new Coords(5, 0), new Piece(this, PieceType.B_BISHOP, 5, 0));
        pieces.put(new Coords(3, 0), new Piece(this, PieceType.B_QUEEN, 3, 0));
        pieces.put(new Coords(4, 0), new Piece(this, PieceType.B_KING, 4, 0));
        pieces.put(new Coords(0, 1), new Piece(this, PieceType.B_PAWN, 0, 1));
        pieces.put(new Coords(1, 1), new Piece(this, PieceType.B_PAWN, 1, 1));
        pieces.put(new Coords(2, 1), new Piece(this, PieceType.B_PAWN, 2, 1));
        pieces.put(new Coords(3, 1), new Piece(this, PieceType.B_PAWN, 3, 1));
        pieces.put(new Coords(4, 1), new Piece(this, PieceType.B_PAWN, 4, 1));
        pieces.put(new Coords(5, 1), new Piece(this, PieceType.B_PAWN, 5, 1));
        pieces.put(new Coords(6, 1), new Piece(this, PieceType.B_PAWN, 6, 1));
        pieces.put(new Coords(7, 1), new Piece(this, PieceType.B_PAWN, 7, 1));

        pieces.put(new Coords(0, 7), new Piece(this, PieceType.W_ROOK, 0, 7));
        pieces.put(new Coords(7, 7), new Piece(this, PieceType.W_ROOK, 7, 7));
        pieces.put(new Coords(1, 7), new Piece(this, PieceType.W_KNIGHT, 1, 7));
        pieces.put(new Coords(6, 7), new Piece(this, PieceType.W_KNIGHT, 6, 7));
        pieces.put(new Coords(2, 7), new Piece(this, PieceType.W_BISHOP, 2, 7));
        pieces.put(new Coords(5, 7), new Piece(this, PieceType.W_BISHOP, 5, 7));
        pieces.put(new Coords(3, 7), new Piece(this, PieceType.W_QUEEN, 3, 7));
        pieces.put(new Coords(4, 7), new Piece(this, PieceType.W_KING, 4, 7));
        pieces.put(new Coords(0, 6), new Piece(this, PieceType.W_PAWN, 0, 6));
        pieces.put(new Coords(1, 6), new Piece(this, PieceType.W_PAWN, 1, 6));
        pieces.put(new Coords(2, 6), new Piece(this, PieceType.W_PAWN, 2, 6));
        pieces.put(new Coords(3, 6), new Piece(this, PieceType.W_PAWN, 3, 6));
        pieces.put(new Coords(4, 6), new Piece(this, PieceType.W_PAWN, 4, 6));
        pieces.put(new Coords(5, 6), new Piece(this, PieceType.W_PAWN, 5, 6));
        pieces.put(new Coords(6, 6), new Piece(this, PieceType.W_PAWN, 6, 6));
        pieces.put(new Coords(7, 6), new Piece(this, PieceType.W_PAWN, 7, 6));

        for (Entry<Coords, Piece> entry : pieces.entrySet())
        {
            Piece piece = entry.getValue();
            jPane.add(piece.getButton());
            jPane.setLayer(piece.getButton(), 1);
        }

        addAvailableMove(new Coords(1, 1), new Coords(1, 2));
        addAvailableMove(new Coords(1, 1), new Coords(1, 6));

        addPiece(PieceType.W_KING, new Coords(4, 4));
    }
}
